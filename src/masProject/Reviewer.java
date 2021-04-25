package masProject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Reviewer extends Agent {
	
	
	public void setup() {
		
		addBehaviour(new RecieveMessageFromTh());
		addBehaviour(new RecieveMessfrStu());
		 
		
	}
	
	public class RecieveMessageFromTh extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchConversationId("StudentReviewer");
			ACLMessage msg = receive(mt);
			
			if(msg!=null) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Message from Thesis Committee to Reviewer");
				System.out.print(content);
			}
			
		}
		
	}
	
	public class RecieveMessfrStu extends CyclicBehaviour {

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
			MessageTemplate mt = MessageTemplate.MatchConversationId("ThesisProgress");
			ACLMessage msg = receive(mt); 
			
			if(msg!=null) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Message of Student to Reviewer ...");
				System.out.println(content);
				
				ACLMessage th = new ACLMessage(ACLMessage.INFORM);
				th.addReceiver(new AID("ThesisCommittee", AID.ISLOCALNAME));
				th.setConversationId("StudentThProgress");
				th.setReplyWith("Inform_Comm"+ System.currentTimeMillis());
				th.setContent("STUDENT THESIS PROGRESS ON COURSE");
				send(th);
			}
			
		}
		
	}
	

}
