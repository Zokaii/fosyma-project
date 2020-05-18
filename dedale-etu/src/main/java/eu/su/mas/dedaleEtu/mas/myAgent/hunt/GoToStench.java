package eu.su.mas.dedaleEtu.mas.myAgent.hunt;

import java.util.List;

import eu.su.mas.dedaleEtu.mas.myAgent.Agent;
import jade.core.behaviours.SimpleBehaviour;

public class GoToStench extends SimpleBehaviour{

	private static final long serialVersionUID = 3750742966361396637L;
	
	private List<String> path;
	private int step;
	private boolean finished;
	
	
	public GoToStench(Agent myAgent) {
		super(myAgent);
		path = myAgent.getMap().getShortestPath(myAgent.getCurrentPosition(), myAgent.getStenchLastPosition());
		step = 0;
	}

	@Override
	public void action() {
		String nextNode = null;
		if (step < path.size()) {
			nextNode = path.get(step++);
			((Agent)myAgent).moveTo(nextNode);
		} else {
			((Agent)myAgent).addBehaviour(new SearchStench((Agent)myAgent));
			finished = true;
		}
	}

	@Override
	public boolean done() {
		return finished;
	}

}
