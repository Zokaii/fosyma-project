package eu.su.mas.dedaleEtu.mas.myAgent.communication;

import java.util.Map;

import eu.su.mas.dedaleEtu.mas.myAgent.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceiveMap extends SimpleBehaviour{

	private static final long serialVersionUID = -3706253487110348440L;

	private boolean finished;
	
	public ReceiveMap(Agent myAgent) {
		super(myAgent);
		finished = false;
	}

	@Override
	public void action() {
		
		try {
			this.myAgent.doWait(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
				MessageTemplate.MatchProtocol("mapExchange"));
		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		
		if (msg != null) {
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> infos = (Map<String, Object>)msg.getContentObject();
				((Agent)myAgent).mergeInfos(infos);
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}else{
			block();
		}
	}
	
	public void setFinished() {
		finished = true;
	}

	@Override
	public boolean done() {
		return finished;
	}

}
