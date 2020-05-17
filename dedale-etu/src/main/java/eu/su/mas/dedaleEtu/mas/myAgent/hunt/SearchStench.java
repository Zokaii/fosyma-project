package eu.su.mas.dedaleEtu.mas.myAgent.hunt;

import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.myAgent.Agent;
import jade.core.behaviours.SimpleBehaviour;

public class SearchStench extends SimpleBehaviour {
	
	private boolean finished = false;

	private static final long serialVersionUID = -98517711645842652L;
	
	public SearchStench(Agent myAgent) {
		super(myAgent);
	}
	
	@Override
	public void action() {
		
		String myPosition = ((Agent)myAgent).getCurrentPosition();
		
		if (myPosition != null) {
			
			try {
				myAgent.doWait(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}

			List<Couple<String,List<Couple<Observation,Integer>>>> lobs = ((Agent)myAgent).observe();
			List<String> stenchPos = ((Agent)myAgent).getStenchs(lobs);
			
			if (!stenchPos.isEmpty()) {
				((Agent)myAgent).addBehaviour(new FollowStench((Agent)myAgent));
				finished = true;
			} else {
				String nextNode = null;
				Random r= new Random();
				int moveId=1+r.nextInt(lobs.size()-1);
				nextNode = lobs.get(moveId).getLeft();
				((Agent)myAgent).moveTo(nextNode);
			}
		}
	}

	@Override
	public boolean done() {
		return finished;
	}

}
