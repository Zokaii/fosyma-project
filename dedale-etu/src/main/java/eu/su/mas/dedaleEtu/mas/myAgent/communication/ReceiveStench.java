package eu.su.mas.dedaleEtu.mas.myAgent.communication;

import eu.su.mas.dedaleEtu.mas.myAgent.Agent;
import eu.su.mas.dedaleEtu.mas.myAgent.hunt.GoToStench;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveStench extends SimpleBehaviour{
	
	private boolean finished;

	public ReceiveStench(Agent myAgent) {
		super(myAgent);
		finished = false;
	}

	private static final long serialVersionUID = 787453160730596689L;

	@Override
	public void action() {
		try {
			this.myAgent.doWait(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
				MessageTemplate.MatchProtocol("stenchPosition"));
		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		
		if (msg != null) {
			((Agent)myAgent).setStenchLastPosition(msg.getContent());
			((Agent)myAgent).addBehaviour(new GoToStench(((Agent)myAgent)));
			finished = true;
		}else{
			block();
		}
	}

	@Override
	public boolean done() {
		return finished;
	}

}
