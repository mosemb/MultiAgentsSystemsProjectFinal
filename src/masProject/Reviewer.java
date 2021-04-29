package masProject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import masProject.ThesisCommt.RecieveBroadCast;

public class Reviewer extends Agent {
	
	
	public void setup() {
		
		// Register services on yellow pages
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.setName(getAID());
				
				ServiceDescription sd = new ServiceDescription();
				sd.setType("reviewer-agent");
				sd.setName("MASTERS-GRADUATION");
				
				dfd.addServices(sd);
				
				try {
					DFService.register(this, dfd);
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
		
		
		// Behaviors of Agent
		addBehaviour(new RecieveMessageFromTh());
		addBehaviour(new RecieveMessfrStu());
		addBehaviour(new RecieveMessageFromSupCom());
		addBehaviour(new RecieveBroadCast());
		addBehaviour(new RecieveBroadCastFromStudent());
		
	}
	
	public class RecieveMessageFromTh extends CyclicBehaviour{
		/*
		 * Recieve message from thesis committee about the reviewer
		 * 
		 * */
		public void action() {
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
      /*
       * Recieve message from student to reviewer about progress of thesis
       * 
       * */
	
		public void action() {
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
	
	public class RecieveMessageFromSupCom extends CyclicBehaviour {
     /* Message from Thesis committe to Reviewer about Thesis assignment connects 
      * with inner class RecieveFromSupCompany
      * */
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchConversationId("ThReviewerCompany2");
			ACLMessage msg = receive(mt); 
			
			if(msg!=null) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Message from Thesis Commitee to Reviewer about Thesis assignemnt ...");
				System.out.println(content);
				
			}
			
			
		}
		
	}
	
	public class RecieveBroadCast extends CyclicBehaviour {
		/*
		 * Recieves messages for the broadcast to all agents in the yellow pages. 
		 * */

		@Override
		public void action() {
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = receive(mt);
			
			if(msg!=null && msg.getConversationId().equals("BroadCast_id")) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Printing out broadcast message from Student! -Reviewer ");
				System.out.println(content);
				
			}
			
		}
		
	}
	
	public class RecieveBroadCastFromStudent extends CyclicBehaviour {
		/*
		 * Recieves messages from the student to all reviewer agents. 
		 * The process of message reception is the same as in all other circumstances. 
		 * 
		 * */

		@Override
		public void action() {
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = receive(mt);
			
			if(msg!=null && msg.getConversationId().equals("Reviewer_BroadCast_id")) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Printing out broadcast message from Student! -Reviewer ");
				System.out.println(content);
				
			}
			
		}
		
	}
	

}
