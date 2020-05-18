package eu.su.mas.dedaleEtu.mas.myAgent.hunt;

import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.myAgent.Agent;
import jade.core.behaviours.SimpleBehaviour;

public class FollowStench extends SimpleBehaviour {

	private static final long serialVersionUID = -5283596889691258112L;
	
	private boolean finished;
	
	public FollowStench(Agent myAgent) {
		super(myAgent);
		finished = false;
	}

	@Override
	public void action() {
		
		String myPosition = ((Agent)myAgent).getCurrentPosition();
		
		if (myPosition != null) {
			
			try {
				myAgent.doWait(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs = ((Agent)myAgent).observe();
			List<String> stenchPos = ((Agent)myAgent).getStenchs(lobs);
			
			String nextNode = null;
			
			if (! stenchPos.isEmpty()) {
				nextNode = stenchPos.get(0);
				((Agent)myAgent).moveTo(nextNode);
			} else {
				((Agent)myAgent).addBehaviour(new SearchStench(((Agent)myAgent)));
				finished = true;
			}
		}
	}

	@Override
	public boolean done() {
		return finished;
	}

}
