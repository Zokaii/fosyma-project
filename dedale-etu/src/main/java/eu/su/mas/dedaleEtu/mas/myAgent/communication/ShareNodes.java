package eu.su.mas.dedaleEtu.mas.myAgent.communication;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import eu.su.mas.dedaleEtu.mas.myAgent.Agent;
import eu.su.mas.dedaleEtu.mas.myAgent.knowledge.CommunicationList;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ShareNodes extends SimpleBehaviour{

	private static final long serialVersionUID = 1327225626801914943L;
	
	public ShareNodes(Agent myAgent) {
		super(myAgent);
	}
	
	public void askForCommunication() {
		
		ACLMessage msg=new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(myAgent.getAID());
		msg.setProtocol("shareNodesRequest");
		
		for (String agentName: CommunicationList.getInstance().getIdList()) {
			if (!agentName.equals(myAgent.getLocalName())) {
				msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
			}
		}
		
		((Agent)myAgent).sendMessage(msg);
	}

	public void answerForCommunication() {
		
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), 
				MessageTemplate.MatchProtocol("shareNodesRequest"));
		final ACLMessage msg =  myAgent.receive(msgTemplate);
		
		if (msg != null) {
			
				ACLMessage asw=new ACLMessage(ACLMessage.CONFIRM);
				asw.setSender(myAgent.getAID());
				asw.setProtocol("shareNodesConfirm");
				asw.addReceiver(msg.getSender());	
				((Agent)myAgent).sendMessage(asw);
				
		} else {
			block();
		}
	}
	
	public void confirmForCommunication() {
		
		final MessageTemplate msgTemplate = MessageTemplate.and( MessageTemplate.MatchPerformative(ACLMessage.CONFIRM), 
				MessageTemplate.MatchProtocol("shareNodesConfirm"));
		final ACLMessage msg = myAgent.receive(msgTemplate);
		
		if (msg != null) {
			((Agent)myAgent).setCommunicationAgent(msg.getSender());
			sendOpenNodes();
		
		} else {
			block();
		}
	}
	
	public void sendOpenNodes() {
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(myAgent.getAID());
		msg.setProtocol("nodesExchange");	
		msg.addReceiver(((Agent)myAgent).getCommunicationAgent());
		
		try {
			msg.setContentObject((Serializable)((Agent)myAgent).getOpenNodes());
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		((Agent)myAgent).sendMessage(msg);
	}
	
	public void receiveNodes() {
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
				MessageTemplate.MatchProtocol("nodesExchange"));
		final ACLMessage msg = myAgent.receive(msgTemplate);
		
		if (msg != null) {
			try {
				((Agent)myAgent).setCommunication(true);
				@SuppressWarnings("unchecked")
				List<String> nodes = (List<String>)msg.getContentObject();
				List<String> slicedNodes = ((Agent)myAgent).sliceNodes(nodes);
				
				final ACLMessage asw = new ACLMessage(ACLMessage.INFORM);
				asw.setSender(myAgent.getAID());
				asw.setProtocol("slicedNodesExchange");
				asw.addReceiver(msg.getSender());
				
				try {
					asw.setContentObject((Serializable)slicedNodes);
				} catch(IOException e) {
					e.printStackTrace();
				}
				
				((Agent)myAgent).sendMessage(asw);
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}else{
			block();
		}
	}
	
	public void receiveSlicedNodes() {
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
				MessageTemplate.MatchProtocol("slicedNodesExchange"));
		final ACLMessage msg = myAgent.receive(msgTemplate);
		
		if (msg != null) {
				try {
					((Agent)myAgent).setCommunication(true);
					@SuppressWarnings("unchecked")
					List<String> slicedNodes = (List<String>)msg.getContentObject();
					((Agent)myAgent).setOpenNodes(slicedNodes);
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
		}else{
			block();
		}
	}
	
	@Override
	public void action() {
		
		try {
			myAgent.doWait(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("opennodes "+myAgent.getLocalName()+((Agent)myAgent).getOpenNodes());
		
		if (((Agent)myAgent).getCommunication()) {
			//((Agent)myAgent).decrementCountDown();
			/*if (((Agent)myAgent).getCountDown() <= 0) {
				((Agent)myAgent).resetCountDown();
				((Agent)myAgent).setCommunication(false);
			}*/
		}
		
		else {
			askForCommunication();
			answerForCommunication();
			confirmForCommunication();
			receiveNodes();
			receiveSlicedNodes();
		}
	}	

	@Override
	public boolean done() {
		return false;
	}

}
