package eu.su.mas.dedaleEtu.mas.myAgent.communication;

import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.myAgent.Agent;
import eu.su.mas.dedaleEtu.mas.myAgent.knowledge.CommunicationList;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Blocked extends SimpleBehaviour{

	private static final long serialVersionUID = 2782819682999456965L;
	
	public Blocked(Agent myAgent) {
		super(myAgent);
	}

	public void sendBlocked() {
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		msg.setProtocol("blocked");
		
		for (String agentName: CommunicationList.getInstance().getIdList()) {
			if (!agentName.equals(myAgent.getLocalName())) {
				msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
			}
		}
		((Agent)myAgent).setPreviousPosition(null);
		((Agent)myAgent).sendMessage(msg);
	}
	
	public void receiveBlocked() {
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
				MessageTemplate.MatchProtocol("blocked"));
		final ACLMessage msg = myAgent.receive(msgTemplate);
		
		if (msg != null) {
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs = ((Agent)myAgent).observe();
			Random r= new Random();
			int moveId=1+r.nextInt(lobs.size()-1);
			((Agent)myAgent).moveTo(lobs.get(moveId).getLeft());
		}else{
			block();
		}
	}
	
	@Override
	public void action() {
		
		Random r = new Random();
		try {
			myAgent.doWait(r.nextInt(200));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sendBlocked();
		receiveBlocked();
	}

	@Override
	public boolean done() {
		return true;
	}

}
