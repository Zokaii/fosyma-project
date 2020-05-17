package eu.su.mas.dedaleEtu.mas.myAgent.communication;

import java.io.IOException;
import java.io.Serializable;

import eu.su.mas.dedaleEtu.mas.myAgent.Agent;
import eu.su.mas.dedaleEtu.mas.myAgent.knowledge.CommunicationList;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class SendMap extends SimpleBehaviour{

	private static final long serialVersionUID = -6631389033025616323L;
	
	public SendMap(Agent myAgent) {
		super(myAgent);
	}

	@Override
	public void action() {
		
		try {
			this.myAgent.doWait(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		msg.setProtocol("mapExchange");
		
		for (String agentName: CommunicationList.getInstance().getIdList()) {
			if (!agentName.equals(myAgent.getLocalName())) {
				msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
			}
		}
		
		try {
			msg.setContentObject((Serializable)((Agent)myAgent).getInfos());
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		((Agent)myAgent).sendMessage(msg);
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}
