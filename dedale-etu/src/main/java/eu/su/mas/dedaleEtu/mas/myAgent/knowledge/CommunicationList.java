package eu.su.mas.dedaleEtu.mas.myAgent.knowledge;

import java.util.ArrayList;
import java.util.List;

public class CommunicationList {
	
	private static CommunicationList instance;
	
	private List<String> idList;
	
	private CommunicationList() {
		idList = new ArrayList<>();
	}
	
	public static CommunicationList getInstance() {
		if (instance == null) instance = new CommunicationList();
		return instance;
	}
	
	public List<String> getIdList(){
		return idList;
	}
	
	public void addIdToIdList(String id) {
		if (! idList.contains(id))
			idList.add(id);
	}

}
