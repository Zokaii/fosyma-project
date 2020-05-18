package eu.su.mas.dedaleEtu.mas.myAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.myAgent.communication.ReceiveMap;
import eu.su.mas.dedaleEtu.mas.myAgent.communication.SendMap;
import eu.su.mas.dedaleEtu.mas.myAgent.communication.ShareNodes;
import eu.su.mas.dedaleEtu.mas.myAgent.exploration.SimpleExploration;
import jade.core.AID;
import jade.core.behaviours.Behaviour;

public class Agent extends AbstractDedaleAgent{

	private static final long serialVersionUID = -4446230480298233691L;
	
	private List<Behaviour> listBehaviour;
	
	private boolean communication;
	private AID communicationAgent;
	private Set<String> closedNodes;
	private List<String> openNodes;
	private List<String> sharedNodes;
	private MapRepresentation map;
	private boolean mapCompleted;
	private String previousPosition;
	private boolean tryPrevious = false;
	private String stenchLastPosition;

	public void setCommunication(boolean bool) {
		communication = bool;
	}
	
	public boolean getCommunication() {
		return communication;
	}
	
	public void setCommunicationAgent(AID agent) {
		communicationAgent = agent;
	}
	
	public AID getCommunicationAgent() {
		return communicationAgent;
	}
	
	public List<Behaviour> getListBehaviour(){
		return listBehaviour;
	}
	
	public List<String> getOpenNodes(){
		return openNodes;
	}
	
	public void setOpenNodes(List<String> newOpenNodes){
		openNodes = newOpenNodes;
		for (String n: openNodes) {
			if (! closedNodes.contains(n))
				map.addNode(n, MapAttribute.open);
		}
	}
	
	public Set<String> getClosedNodes(){
		return closedNodes;
	}
	
	public List<String> getSharedNodes(){
		return sharedNodes;
	}
	
	public MapRepresentation getMap() {
		return map;
	}
	
	public void setMap() {
		map = new MapRepresentation();
	}
	
	public boolean getMapCompleted() {
		return mapCompleted;
	}
	
	public void setMapCompleted() {
		mapCompleted = true;
	}
	
	public void setPreviousPosition(String newPos) {
		previousPosition = newPos;
	}
	
	public boolean blocked() {
		return previousPosition == getCurrentPosition();
	}
	
	public String getStenchLastPosition() {
		return stenchLastPosition;
	}
	
	public void setStenchLastPosition(String pos) {
		stenchLastPosition = pos;
	}
	
	public boolean getTryPrevious() {
		return tryPrevious;
	}
	
	public void setTryPrevious(boolean bool) {
		tryPrevious = bool;
	}
	
	public Map<String, Object> getInfos(){
		Map<String, Object> infos = new HashMap<>();
		infos.put("closedNodes", closedNodes);
		infos.put("openNodes", openNodes);
		infos.put("edges", map.getEdges());
		infos.put("mapCompleted", mapCompleted);
		return infos;
	}
	
	@SuppressWarnings("unchecked") // c'est pas propre :(
	public void mergeInfos(Map<String, Object> infos) {
		
		if ((boolean)infos.get("mapCompleted"))
			setMapCompleted();
		
		for (String node: (List<String>) infos.get("openNodes")) {
			if (! openNodes.contains(node) && ! closedNodes.contains(node)) {
				openNodes.add(node);
				map.addNode(node, MapAttribute.open);
			}
		}

		for (String node: (Set<String>) infos.get("closedNodes")) {
			if (!closedNodes.contains(node)) {
				if(openNodes.contains(node))
					openNodes.remove(node);
				closedNodes.add(node);
				map.addNode(node, MapAttribute.closed);
			}
		}
		
		for (Couple<String, String> edge : (List<Couple<String, String>>) infos.get("edges")) {
			String node0 = edge.getLeft();
			String node1 = edge.getRight();
			map.addEdge(node0, node1);
		}
	}
	
	public List<String> sliceNodes(List<String> nodes){
		System.out.println("slice");
		List<String> combinedNodes = new ArrayList<>();
		
		for (String n: openNodes) {
				combinedNodes.add(n);
		}
		
		for (String n: nodes) {
			if (!combinedNodes.contains(n))
				combinedNodes.add(n);
		}
		
		Collections.sort(combinedNodes);
		
		List<String> slicedNodes1 = new ArrayList<>();
		List<String> slicedNodes2 = new ArrayList<>();
		
		for (int i=0; i<combinedNodes.size(); i++) {
			slicedNodes1.add(combinedNodes.get(i));
			slicedNodes2.add(combinedNodes.get(combinedNodes.size()-i-1));
		}
		
		setOpenNodes(slicedNodes1);
		System.out.println("sliced1"+slicedNodes1);
		System.out.println("sliced2"+slicedNodes2);
		return slicedNodes2;
		
	}
	
	public List<String> getStenchs(List<Couple<String,List<Couple<Observation,Integer>>>> lobs) {
		List<String> stenchPos = new ArrayList<>();
		for (Couple<String,List<Couple<Observation,Integer>>> obs: lobs) {
			for (Couple<Observation, Integer> item :obs.getRight()) {
				if (item.getLeft().equals(Observation.STENCH)) {
					stenchPos.add(obs.getLeft());
					break;
				}
			}
		}
		return stenchPos;
	}
	
	protected void setup(){

		super.setup();
		
		communication = false;
		
		closedNodes = new HashSet<>();
		openNodes = new ArrayList<>();
		sharedNodes = new ArrayList<>();
		
		listBehaviour = new ArrayList<>();
		
		listBehaviour.add(new SimpleExploration(this));
		listBehaviour.add(new SendMap(this));
		listBehaviour.add(new ReceiveMap(this));
		//listBehaviour.add(new ShareNodes(this));
		
		addBehaviour(new startMyBehaviours(this, listBehaviour));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
}
