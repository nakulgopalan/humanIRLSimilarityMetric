package analysegamefiles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import burlap.behavior.stochasticgames.GameAnalysis;
import burlap.domain.stochasticgames.gridgame.GridGame;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.agentactions.GroundedSGAgentAction;

public class NormLearningResultsForGame {

	final String north = "north";
	final String south = "south";
	final String east = "east";
	final String west = "west";
	final String wait = "wait";
	Map <String, Integer> gameVectorInit = new HashMap<String, Integer>();
	
	public NormLearningResultsForGame(){
		gameVectorInit.put("NS", 0);
		gameVectorInit.put("SN", 0);
		gameVectorInit.put("EW", 0);
		gameVectorInit.put("WE", 0);
		gameVectorInit.put("N", 0);
		gameVectorInit.put("S", 0);
		gameVectorInit.put("E", 0);
		gameVectorInit.put("W", 0);
		gameVectorInit.put("wait", 0);
	}

	

	public static void main(String[] args){
		GridGame gg = new GridGame();
		SGDomain domain = (SGDomain)gg.generateDomain();

		NormLearningResultsForGame nl = new NormLearningResultsForGame();

		boolean debugPrint = false;

		//		System.out.println("length of args" + args.length);

		//		String pathString = "/home/ng/workspace/projectforcountingstates/coordinationSplitTrain10/";


		String path = "/home/ng/workspace/projectforcountingstates/coordinationSplitTrain/7628633/";


		String str = "learned_";

		//TODO: read actions names from the domain some how - because wait action may be here now!


		//		String fileName  = path + "/"+ str +"/";//"7628633/" 
		//TODO: add a for loop here
		String fileName = path + str  + 0 + ".game";

		GameAnalysis ga = null;
		ga = GameAnalysis.parseFileIntoGA(fileName , domain);



		List<State> sl = ga.states;
		List<JointAction> jal = ga.jointActions;

		List<GroundedSGAgentAction> gsaa1List = new ArrayList<GroundedSGAgentAction>();
		List<GroundedSGAgentAction> gsaa2List = new ArrayList<GroundedSGAgentAction>();


		for(JointAction ja :jal){
			//ja is a single joint action
			List<GroundedSGAgentAction> gal = ja.getActionList();
			// gal = are single ga's from this joint action
			gsaa1List.add(gal.get(0));
			gsaa2List.add(gal.get(1));
			//			System.out.println(ja.actionName());
			//			
			//			for(GroundedSGAgentAction gsaa : gal){
			//				gsaa1List.add(e);
			//			}
		}





		String outputFileName = "gameResults10.csv";
		File folder = new File(fileName);

		File[] listOfFiles = folder.listFiles();


	}

	private double[] getReturnVector(List<GroundedSGAgentAction> gsaa1List){
		Map <String, Integer> agent1Vector = new HashMap(gameVectorInit);
//		Map <String, Integer> agent2Vector = new HashMap(gameVectorInit);
		//TODO: going through the lists
		int northcount = 0;
		int southcount = 0;
		int eastcount = 0;
		int westcount = 0;

		for(GroundedSGAgentAction gsaa: gsaa1List){
			switch(gsaa.actionName()){
			case north:
				agent1Vector.put("N", agent1Vector.get("N")+1);
				if(southcount>0){
					agent1Vector.put("SN", agent1Vector.get("SN")+1);
					southcount--;
				}
				else{
					northcount++;
				}
				break;
			case south:
				agent1Vector.put("S", agent1Vector.get("S")+1);
				if(northcount>0){
					agent1Vector.put("NS", agent1Vector.get("NS")+1);
					northcount--;
				}
				else{
					southcount++;
				}
				break;
			case east:
				agent1Vector.put("E", agent1Vector.get("E")+1);
				if(westcount>0){
					agent1Vector.put("WE", agent1Vector.get("WE")+1);
					westcount--;
				}
				else{
					eastcount++;
				}
				break;
			case west:
				agent1Vector.put("W", agent1Vector.get("W")+1);
				if(southcount>0){
					agent1Vector.put("EW", agent1Vector.get("EW")+1);
					eastcount--;
				}
				else{
					westcount++;
				}
				break;
			case wait:
				agent1Vector.put("W", agent1Vector.get("W")+1);
				break;
			default:
				System.out.println(gsaa.actionName() + " is not in the case statement");

			}
		}
		double[] returnVector = new double[agent1Vector.keySet().size()];

		//			System.out.println(key + " :" + agent1Vector.get(key));
		returnVector[0] = agent1Vector.get("NS");
		returnVector[1] = agent1Vector.get("SN");
		returnVector[2] = agent1Vector.get("EW");
		returnVector[3] = agent1Vector.get("WE");
		returnVector[4] = agent1Vector.get("N");
		returnVector[5] = agent1Vector.get("S");
		returnVector[6] = agent1Vector.get("E");
		returnVector[7] = agent1Vector.get("W");
		returnVector[8] = agent1Vector.get("wait");

		return returnVector;
	}

}
