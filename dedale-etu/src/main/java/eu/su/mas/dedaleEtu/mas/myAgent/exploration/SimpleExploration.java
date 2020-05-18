package eu.su.mas.dedaleEtu.mas.myAgent.exploration;

import java.util.Iterator;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.myAgent.Agent;
import eu.su.mas.dedaleEtu.mas.myAgent.communication.Blocked;
import eu.su.mas.dedaleEtu.mas.myAgent.communication.ReceiveMap;
import eu.su.mas.dedaleEtu.mas.myAgent.hunt.FollowStench;
import eu.su.mas.dedaleEtu.mas.myAgent.hunt.SearchStench;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;

public class SimpleExploration extends SimpleBehaviour {
	
	private static final long serialVersionUID = -8301161740218668222L;
	
	private boolean finished = false;
	
	public SimpleExploration(Agent myAgent) {
		super(myAgent);
	}

	@Override
	public void action() {
		
		if (((Agent)myAgent).getMap() == null)
			((Agent)myAgent).setMap();
		
		String myPosition=((Agent)myAgent).getCurrentPosition();
		
		if (myPosition!=null){
			
			try {
				myAgent.doWait(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			
			if (! ((Agent)myAgent).getStenchs(lobs).isEmpty()) {
				finished = true;
				((Agent)myAgent).addBehaviour(new FollowStench(((Agent)myAgent)));
				return;
			}
			
			if (((Agent)myAgent).blocked()) {
				System.out.println("blocked");
				((Agent)myAgent).addBehaviour(new Blocked(((Agent)myAgent)));
				return;
			}
			
			
			((Agent)myAgent).setPreviousPosition(myPosition);

			((Agent)myAgent).getClosedNodes().add(myPosition);
			((Agent)myAgent).getOpenNodes().remove(myPosition);

			((Agent)myAgent).getMap().addNode(myPosition, MapAttribute.closed);
			
			String nextNode=null;
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				String nodeId=iter.next().getLeft();
				if (!((Agent)myAgent).getClosedNodes().contains(nodeId)){
					if (!((Agent)myAgent).getOpenNodes().contains(nodeId)){
						((Agent)myAgent).getOpenNodes().add(nodeId);
						((Agent)myAgent).getMap().addNode(nodeId, MapAttribute.open);
						((Agent)myAgent).getMap().addEdge(myPosition, nodeId);	
					}else{
						((Agent)myAgent).getMap().addEdge(myPosition, nodeId);
					}
					if (nextNode==null) nextNode=nodeId;
				}
			}

			if (((Agent)myAgent).getOpenNodes().isEmpty()){
				((Agent)myAgent).setMapCompleted();
				finished = true;
				((Agent)myAgent).addBehaviour(new SearchStench(((Agent)myAgent)));
				for(Behaviour b: ((Agent)myAgent).getListBehaviour()) {
					if (b instanceof ReceiveMap) {
						((ReceiveMap) b).setFinished();
					}
				}
				((Agent)myAgent).addBehaviour(new SearchStench(((Agent)myAgent)));
				System.out.println("Exploration successufully done, behaviour removed.");
			} else {
				if (nextNode==null){
					nextNode=((Agent)myAgent).getMap().getShortestPath(myPosition, ((Agent)myAgent).getOpenNodes().get(0)).get(0);
				}
				((Agent)myAgent).moveTo(nextNode);
			}
		}
	}

	@Override
	public boolean done() {
		return finished;
	}
}
