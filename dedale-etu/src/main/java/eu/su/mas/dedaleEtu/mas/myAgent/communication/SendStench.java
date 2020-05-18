package eu.su.mas.dedaleEtu.mas.myAgent.communication;

import eu.su.mas.dedaleEtu.mas.myAgent.Agent;
import eu.su.mas.dedaleEtu.mas.myAgent.knowledge.CommunicationList;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class SendStench extends SimpleBehaviour{
	
	private int countdown;

	private static final long serialVersionUID = 7456202516818522985L;
	
	public SendStench(Agent myAgent) {
		countdown = 5;
	}

	@Override
	public void action() {
		
		countdown--;
		
		try {
			this.myAgent.doWait(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		msg.setProtocol("stenchPosition");
		
		for (String agentName: CommunicationList.getInstance().getIdList()) {
			if (!agentName.equals(myAgent.getLocalName())) {
				msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
			}
		}
		
		msg.setContent(((Agent)myAgent).getStenchLastPosition());
		
		((Agent)myAgent).sendMessage(msg);
	}

	@Override
	public boolean done() {
		return countdown <= 0;
	}

}
