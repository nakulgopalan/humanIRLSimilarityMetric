package analysegamefiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import burlap.behavior.singleagent.auxiliary.StateEnumerator;
import burlap.behavior.stochasticgames.GameAnalysis;
import burlap.behavior.stochasticgames.auxiliary.GameSequenceVisualizer;
import burlap.domain.stochasticgames.gridgame.GGVisualizer;
import burlap.domain.stochasticgames.gridgame.GridGame;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.statehashing.HashableState;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.visualizer.Visualizer;

public class TestCode {

	public static void main(String[] args){
		GridGame gg = new GridGame();
		SGDomain domain = (SGDomain)gg.generateDomain();
		GGVisualizer ggV = new GGVisualizer();
		//		System.out.println("Working Directory = " +
		//	              System.getProperty("user.dir"));
		File path = new File("/home/ng/workspace/projectforcountingstates/coordinationSplitTrain/");


		String outputFileName = "gameResults.csv";

		for(int i =0;i<args.length;i++){
			String str = args[i];
			if(str.equals("-o")){
				outputFileName=args[i+1];
			}
		}


		String outputString = "";
		outputString = outputString + "Game Number"+  ","  + "similar actions from similar states"+ "," + "Total similar states" + "\n";


		String[] directories = path.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});


		//		for(String str: directories){
		//TODO: count states and actions per game and count states over games
		String str = "805685756";



		if(true){
			String fileName  = path + "/"+ str +"/";//"7628633/" 
			//		+ 		"/learned_1.game";
			File folder = new File(fileName);

			File[] listOfFiles = folder.listFiles();

			SimpleHashableStateFactory shf = new SimpleHashableStateFactory();


			//		Map<Integer,State> integerStateMap = new HashMap<Integer,State>();


			Map<HashableState,HashMap<JointAction,Integer>> humanStateActionCount = new HashMap<HashableState,HashMap<JointAction,Integer>>();
			Map<HashableState,HashMap<JointAction,Integer>> robotStateActionCount = new HashMap<HashableState,HashMap<JointAction,Integer>>();

			int actionSimilarity = 0;
			int stateSimilarity = 0;

			for(int i=0;i<listOfFiles.length;i++){

				if ((listOfFiles[i].getAbsolutePath().indexOf("learned")!= -1)){
					continue;
				}
				
				System.out.println(listOfFiles[i]);
				
				// read all files and store states in a hash map -> hash map of actions -> counts of actions
				if (!(listOfFiles[i].getAbsolutePath().indexOf("learned")!= -1)) {
					// it is a human game
					GameAnalysis ga = null;
					ga = GameAnalysis.parseFileIntoGA(listOfFiles[i].getAbsolutePath() , domain);
					List<State> sl = ga.states;

					List<State> setOfStates = new ArrayList<State>();

					for(State s: sl){
						boolean present = false;
						for(State sNew:setOfStates){
							if(s.equals(sNew)){
								present = true;
								break;
							}
						}
						if(!present){
							setOfStates.add(s);
							System.out.println(s.getCompleteStateDescription());
						}
						else{
							System.out.println("counted as present:" + s.getCompleteStateDescription());
						}
						
					}

					System.out.println("state size: " + setOfStates.size());

					List<JointAction> jal = ga.jointActions;
					System.out.println(jal.size() + " " + sl.size());
					for(int stateCount=0;stateCount<sl.size()-1;stateCount++){
						HashableState sTemp = shf.hashState(sl.get(stateCount));


						JointAction jaTemp = jal.get(stateCount);
						if(humanStateActionCount.containsKey(sTemp)){

							Set<HashableState> keySet = humanStateActionCount.keySet(); 
							Boolean testBool = false;
							for(HashableState test:keySet){
								
								if(test.equals(sTemp)){
									testBool = true;
									//System.out.println("was here");
									break;

								}
							}
							if(!testBool){
								System.out.println("state not equal but hashset is");
							}

							if(humanStateActionCount.get(sTemp).containsKey(jaTemp)){

								int value = humanStateActionCount.get(sTemp).get(jaTemp);
								humanStateActionCount.get(sTemp).put(jaTemp, value+1);
							}
							else{
								//this is if the action is not present
								humanStateActionCount.get(sTemp).put(jaTemp, 1);
							}
							//						stateActionCount.put(sTemp, value)
						}
						else{
							//this if the state is not present
							humanStateActionCount.put(sTemp, new HashMap<JointAction,Integer>());
							humanStateActionCount.get(sTemp).put(jaTemp, 1);
						}
					}
					break;
				}
				else{
					// it is a robot game
					GameAnalysis ga = null;
					ga = GameAnalysis.parseFileIntoGA(listOfFiles[i].getAbsolutePath(), domain);
					List<State> sl = ga.states;
					List<JointAction> jal = ga.jointActions;
					for(int stateCount=0;stateCount<sl.size()-1;stateCount++){
						HashableState sTemp = shf.hashState(sl.get(stateCount));
						JointAction jaTemp = jal.get(stateCount);
						if(robotStateActionCount.containsKey(sTemp)){
							if(robotStateActionCount.get(sTemp).containsKey(jaTemp)){
								int value = robotStateActionCount.get(sTemp).get(jaTemp);
								robotStateActionCount.get(sTemp).put(jaTemp, value+1);
							}
							else{
								//this is if the action is not present
								robotStateActionCount.get(sTemp).put(jaTemp, 1);
							}
							//						stateActionCount.put(sTemp, value)
						}
						else{
							//this if the state is not present
							robotStateActionCount.put(sTemp, new HashMap<JointAction,Integer>());
							robotStateActionCount.get(sTemp).put(jaTemp, 1);
						}
					}
				}



			}


			// out of the files now need to count mode of actions and see 
			//if they are the same for all the actions

			// if states present only then check for actions
			// check for the frequency of actions as well and then display 
			// with a frequency the mode of picking the modal joint action
			int tempCount =0;
			for(HashableState s:humanStateActionCount.keySet()){

				for(HashableState s1:robotStateActionCount.keySet()){
					if(s.equals(s1)){
						//					System.out.println(humanStateActionCount.get(s).size());
						//					System.out.println(s.getCompleteStateDescription());
						//					System.out.println(s1.getCompleteStateDescription());
						//					System.out.println(s.hashCode());
						//					System.out.println(s1.hashCode());
						tempCount+=1;
						break;
					}

					//				break;
				}
				//			break;
			}


			//		System.out.println("robot state");



			for(HashableState s:humanStateActionCount.keySet()){
				Map<JointAction,Integer> humanJaM = humanStateActionCount.get(s);
				if(!robotStateActionCount.containsKey(s)){
					//				System.out.println("was here");
					//				System.out.println(robotStateActionCount.keySet().size());
					//				System.out.println(humanStateActionCount.keySet().size());
					continue;
				}

				stateSimilarity = stateSimilarity + 1;

				Map<JointAction,Integer> robotJaM = robotStateActionCount.get(s);
				double humanTotalCount =0;
				double humanMaxJaCount = Double.MIN_VALUE;
				List<JointAction> humanMaxJa = new ArrayList<JointAction>();
				for(JointAction ja : humanJaM.keySet()){
					int tempValue = humanJaM.get(ja);
					humanTotalCount += tempValue;

					if(tempValue>humanMaxJaCount){
						humanMaxJa.clear();
						humanMaxJa.add(ja);
						humanMaxJaCount = tempValue;

					}
					else if(tempValue==humanMaxJaCount){
						humanMaxJa.add(ja);
					}
				}
				double freqHuman = humanMaxJaCount/humanTotalCount;

				double robotTotalCount =0;
				double robotMaxJaCount = Double.MIN_VALUE;
				List<JointAction> robotMaxJa = new ArrayList<JointAction>();
				for(JointAction ja : robotJaM.keySet()){
					int tempValue = robotJaM.get(ja);
					robotTotalCount += tempValue;

					if(tempValue>robotMaxJaCount){
						robotMaxJa.clear();
						robotMaxJa.add(ja);
						robotMaxJaCount = tempValue;

					}
					else if(tempValue==robotMaxJaCount){
						humanMaxJa.add(ja);
					}
				}
				double freqRobot = robotMaxJaCount/robotTotalCount;
				if(freqRobot==Double.POSITIVE_INFINITY){
					System.out.println("list size: " +robotJaM.size());
				}

				boolean equalityFlag = false;

				for(JointAction jaH : humanMaxJa){
					for(JointAction jaR: robotMaxJa){
						if(jaH.equals(jaR)){
							equalityFlag = true;
							break;
						}
					}
					if(equalityFlag){
						actionSimilarity +=1;
						break;
					}
				}



				//					System.out.println(equalityFlag + " frequencyR "+ freqRobot +" frequencyH "+ freqHuman);
				//					System.out.println(" Robot num "+ robotMaxJaCount +" robot denom "+ robotTotalCount);

				for(JointAction jaH : humanMaxJa){
					System.out.println("action human "+ jaH.actionName());
				}

				for(JointAction jaR : robotMaxJa){
					System.out.println("action robot "+ jaR.actionName());
				}





			}
			//				System.out.println("total equal states: " + tempCount);

			//				System.out.println(humanStateActionCount.keySet().size());
			//				System.out.println(robotStateActionCount.keySet().size());


			//
			//		GameAnalysis ga = null;
			//		ga = GameAnalysis.parseFileIntoGA(fileName, domain);
			//		int sizeJointAction = ga.jointActions.size();

			//		System.out.println(sizeJointAction);
			//		for(int i =0;i<sizeJointAction;i++){
			//			System.out.println(ga.jointActions.get(i).actionName());
			//		}
			//		
			//		for(int i=0;i<ga.states.size();i++){
			//			System.out.println(ga.states.get(i).getCompleteStateDescription()+ "\n"+i);
			//	
			//		}




			//		GameSequenceVisualizer gv = new GameSequenceVisualizer(v, domain, fileName);

			outputString = outputString + str +  ","  + actionSimilarity + "," + stateSimilarity + "\n";


			System.out.println("Number of states: "+humanStateActionCount.size());


		}
		//		}

		System.out.println(outputString);
		//		Writer writer = null;
		//
		//		try {
		//		    writer = new BufferedWriter(new OutputStreamWriter(
		//		          new FileOutputStream(outputFileName), "utf-8"));
		//		    writer.write(outputString);
		//		} catch (IOException ex) {
		//		  // report
		//		} finally {
		//		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		//		}

	}

}
