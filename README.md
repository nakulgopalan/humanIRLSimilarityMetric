# humanIRLSimilarityMetric
We wanted to measure the similarity between actions taken by the human players and the actions taken by the AI agents that learned their policies using the batch IRL methods. One metric can be to see if the actions taken with maximam frequency given similar states to both human and AI agents are the same. 
For this we first list states that are similar in games between human agents and games between AI agents across different rounds. Further for these states we count the Joint actions taken for these states by both human and AI agents. 
Next we see if the action chosen with maximum frequency in human and AI games are the same joint actions. These results were taken for agents learning from all rounds of play between human agents and only the last ten rounds of play between the human agents.
We noticed that for all games the over half (73%) the similar states had the same actions taken between the human and AI agents. This number is  much higher (81%) when the AI agents are trained only over the last 10 games. Further we noticed that the AI agents get into more similar states as the human agents if trained only over the last 10 states.

To run the comparison - run ReadGame.java with arguments of path '-p' and output file name '-o'. Example on eclipse give ReadGame.java the program arguments of:
-p /home/ng/workspace/projectforcountingstates/coordinationSplitTrain10/ -o testName.csv

TO DO:
Add an ant build