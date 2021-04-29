package masProject;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import masProject.Reviewer.RecieveBroadCast;

public class Thesis  extends Agent{
	
	public void setup() {
		
		// Thesis behavior
		addBehaviour(new RecieveMessageFromStudent());
		
		// Register services on yellow pages
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.setName(getAID());
				
				ServiceDescription sd = new ServiceDescription();
				sd.setType("thesis-agent");
				sd.setName("MASTERS-GRADUATION");
				
				dfd.addServices(sd);
				
				try {
					DFService.register(this, dfd);
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
				
				addBehaviour(new RecieveBroadCast());
		
	}
	
	public class RecieveMessageFromStudent extends CyclicBehaviour{
		/*
		 * Recieve message from student about thesis 
		 * */

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
			ACLMessage msg = receive(mt);
			
			if(msg!=null) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Message from Student to Thesis Agent");
				System.out.println(content);
			}
			
		}
		
	}
	
	public class RecieveBroadCast extends CyclicBehaviour {
		/*
		 * Recieves broacast messages. 
		 * */

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = receive(mt);
			
			if(msg!=null && msg.getConversationId().equals("BroadCast_id")) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Printing out broadcast message from Student! -Thesis ");
				System.out.println(content);
				
			}
			
		}
		
	}

}
