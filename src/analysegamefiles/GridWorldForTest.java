package analysegamefiles;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.oomdp.auxiliary.common.SinglePFTF;
import burlap.oomdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.oomdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.singleagent.environment.Environment;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.behavior.policy.EpsilonGreedy;

public class GridWorldForTest {
	public static void main(String [] args){
		
		GridWorldDomain gw = new GridWorldDomain(11,11); //11x11 grid world
		gw.setMapToFourRooms(); //four rooms layout
		gw.setProbSucceedTransitionDynamics(0.8); //stochastic transitions with 0.8 success rate
		Domain domain = gw.generateDomain(); //generate the grid world domain

		//setup initial state
		State s = GridWorldDomain.getOneAgentOneLocationState(domain);
		GridWorldDomain.setAgent(s, 0, 0);
		GridWorldDomain.setLocation(s, 0, 10, 10);
		
		RewardFunction rf = new UniformCostRF(); 
		TerminalFunction tf = new SinglePFTF(domain.getPropFunction(GridWorldDomain.PFATLOCATION)); 
		StateConditionTest goalCondition = new TFGoalCondition(tf);
		
		State initialState = GridWorldDomain.getOneAgentOneLocationState(domain);
		GridWorldDomain.setAgent(initialState, 0, 0);
		GridWorldDomain.setLocation(initialState, 0, 10, 10);	
		
		HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
		Environment env = new SimulatedEnvironment(domain, rf, tf, initialState);
		
		ValueIteration planner = new ValueIteration(domain, rf, tf, 0.99, hashingFactory, 0.001, 100);
		Policy p = planner.planFromState(initialState);
		
		Policy ep = new EpsilonGreedy(planner, 1.34);
//		ep.setSolver(null);
		KLDivergenceRegularPolicies kl = new KLDivergenceRegularPolicies(p, ep, initialState, domain); 

		System.out.println("value of comparison is: " + kl.runPolicyComparison());
		
		
	}

}
