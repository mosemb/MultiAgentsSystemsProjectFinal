package masProject;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Supervisor  extends Agent{
	
	public void setup() {
		
		addBehaviour(new RequestsFromThesisCom());
		addBehaviour(new MessageFromStudent());
		
		
	}
	
	// Put agent clean-up operations here
	protected void takeDown() {
				// Printout a dismissal message
				System.out.println("Supervisor-agent "+getAID().getName()+" terminating.");
			}
	
	public class RequestsFromThesisCom extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = receive(mt); 
			
			if(msg!=null) {
				String content = msg.getContent();
				
				System.out.println();
				System.out.println("Message from Thesis Committee to Supervisor ...");
				System.out.println(content);
				
				ACLMessage reply = msg.createReply(); // Reply message to sender. 
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent("THESIS BEING REVIEWED");
				myAgent.send(reply);
				
				
				
			}else {
				
				block();
			}
			
			
		}
		
	}
	
	public class MessageFromStudent extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mTemplate  = MessageTemplate.MatchConversationId("TChoice-Proposal"); 
					//MatchPerformative(ACLMessage.INFORM);
			ACLMessage AclMessage  = receive(mTemplate); 
			
			if(AclMessage!=null) {
				
				String contentString = AclMessage.getContent();
				System.out.println("Message from Student to Supervisor ... ");
				System.out.println(contentString);
			} else {
				
				block();
			}
			
		}
		
		
	}
	
	public class SendMoreInfoOnThePro extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
			
		}
		
	}
	
	

}
