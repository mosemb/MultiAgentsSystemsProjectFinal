package masProject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import jade.core.AID;
//import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
//import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ThesisCommt extends Agent{
	MessageTemplate mtThCommtSupr;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<String> reviewers = new ArrayList<>();

	protected void setup() {
		Object [] args = getArguments();
		
		
		for(Object arg:args) {
			
			reviewers.add((String) arg);
			
		} 
		
		
	
          addBehaviour(new  recieveFromStudent());  // Behavior that recieves the message from Student Agent
          addBehaviour(new RecieveMessageFromSupC());
          addBehaviour(new RecieveFromSup());
          addBehaviour(new MessageFromReviewer());
          addBehaviour(new FromSupToThComtP());
          //RecieveFromSupCompany
          addBehaviour(new RecieveFromSupCompany());
        
        
          
               
    }
		  
	
	public class recieveFromStudent extends  CyclicBehaviour {
		/*
		 * This class recieves messages from the student after Company choice is chosen
		 * But also sends back message about either Company Proposal has been accepted or not. 
		 * */
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
				ACLMessage msg = receive(mt);
				
				if (msg != null) {
					// Recieves the message content from Student to Thesis Committee 
					String content = msg.getContent(); 
					Random rand = new Random();
					boolean acceptable = rand.nextBoolean(); // Pick either a boolean value
					System.out.println("Message from Student to Thesis Commitee ... ");
					ACLMessage reply = msg.createReply();
					System.out.println(content);
					System.out.println();
					
					
					if(acceptable==true) {
						reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						reply.setContent("PROPOSAL ACCEPTED"); 
						
					    // This links up with the supervisor agent. The agent is being told to review the thesis
						ACLMessage toSupervisor = new ACLMessage(ACLMessage.REQUEST);
						toSupervisor.addReceiver(new AID("supervisor", AID.ISLOCALNAME));
						toSupervisor.setConversationId("TChoice-Company2");
						toSupervisor.setReplyWith("Request ThCom1 "+ System.currentTimeMillis());
						toSupervisor.setContent("REVIEW THESIS");
						send(toSupervisor);
						
						mtThCommtSupr = MessageTemplate.and(MessageTemplate.MatchConversationId("TChoice-Company2"), 
								MessageTemplate.MatchInReplyTo(toSupervisor.getReplyWith()));
						
						
					}else {
						// Reply to Student about Company proposal from Thesis Committe
						reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
						reply.setContent("PROPOSAL REJECTED, START AGAIN");
					
			            
					}
					// Overall reply either positive or negative 
					myAgent.send(reply);
					
				}
				else {
					block();
				}
			}
		}
	
	public class RecieveMessageFromSupC extends CyclicBehaviour{
    /* Recieves message from Supervisor Agent informing them in the affirmative that the thesis is being reviewed. 
     * 
     *  */
		@Override
		public void action() {
			// TODO Auto-generated method stub
			
			ACLMessage msg = myAgent.receive(mtThCommtSupr);
			
			if(msg!=null&&msg.getConversationId()=="TChoice-Company2") {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Reply from Supervisor to Thesis Commitee ...");
				System.out.println(content);
				
			}else {
				block();
			}
			
		}
		
	}
	
	public class RecieveFromSupCompany extends CyclicBehaviour {
		
		/*
		 * Recieves message from Supervisor about Thesis progress with Student. 
		 * 
		 * */

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchConversationId("TheAssignedToStuCompany");
			ACLMessage msg = receive(mt);
			
			if(msg!=null) {
				
				 Random random = new Random(); 
				 int randomNo = random.nextInt((reviewers.size()-1)-0)+0;
				 String reviewer = reviewers.get(randomNo);
				 
				String content = msg.getContent();
				System.out.println();
				System.out.println("Message from Supervisor to Thesis Committee about thesis Assignement");
				System.out.println(content);
				
				    ACLMessage tostudent = new ACLMessage(ACLMessage.INFORM_IF);
					tostudent.addReceiver(new AID("student", AID.ISLOCALNAME));
					tostudent.setConversationId("ThReviewerCompany");
					tostudent.setReplyWith("ThReviewerCompany"+ System.currentTimeMillis());
					tostudent.setContent(reviewer);
					send(tostudent); 
				
			}
			
		}
		
	}
	
	public class RecieveFromSup extends Behaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
			//MessageTemplate mt = MessageTemplate.MatchConversationId("Assign_Thesis_TC");
			ACLMessage msg = receive(mt);
			
			if(msg!=null) {
				
				String content = msg.getContent();
				System.out.println();
				System.out.println("Message to Thesis Committee from Supervisor :");
				System.out.println(content);
				
			
				
				
				
				 Random random = new Random(); 
				 int randomNo = random.nextInt((reviewers.size()-1)-0)+0;
				 String reviewer = reviewers.get(randomNo);
				 System.out.println();
				 System.out.println("Reviewer details by Thesis Commitee");
				 System.out.println(" THE SELECTED REVIEWER IS ..."+reviewer);
				 System.out.println(" REVIEWER NAME TO REMOVE FROM LIST ..."+reviewers.remove(randomNo));
				 System.out.println(" REMAINING REVIEWERS IN LIST ..."+ reviewers);
				 System.out.println(" THESIS ON GOING");
				 
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM_REF);
					reply.setConversationId("DReviewer");
					reply.setContent(reviewer);
					myAgent.send(reply);
				 
				    ACLMessage tostudent = new ACLMessage(ACLMessage.CFP);
					tostudent.addReceiver(new AID("student", AID.ISLOCALNAME));
					tostudent.setConversationId("ThReviewer");
					tostudent.setReplyWith("Reviewer Th "+ System.currentTimeMillis());
					tostudent.setContent(reviewer);
					send(tostudent); 
					
					//System.out.println(tostudent.getClass().getName());
					
					ACLMessage toreviewer = new ACLMessage(ACLMessage.PROPAGATE);
					toreviewer.addReceiver(new AID("reviewer", AID.ISLOCALNAME));
					toreviewer.setConversationId("StudentReviewer");
					toreviewer.setReplyWith("Reviewer "+System.currentTimeMillis());
					toreviewer.setContent(reviewer.toUpperCase()+" YOU HAVE BEEN ASSIGNED TO STUDENT 1");
					send(toreviewer);
					//System.out.println(toSupervisor);
					
				 
				 
			}
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}

		
		
	}
	
	public class MessageFromReviewer extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate th = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = receive(th); 
			
			if(msg!=null&&msg.getConversationId().equals("StudentThProgress")) {
				String content = msg.getContent();
				System.out.println(); 
				System.out.println("Message from Reviewer to Thesis Commitee..."); 
				System.out.println(content);
			}
			
			
		}
		
	}
	
	public class FromSupToThComtP extends CyclicBehaviour {

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
			MessageTemplate mt = MessageTemplate.MatchConversationId("Proposal_Assigned_Thesis");
			ACLMessage msg = receive(mt);
			
			if(msg!=null) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Message from Supervisor to Thesis Commitee");
				System.out.print(content);
				
				/* Random random = new Random(); 
				 int randomNo = random.nextInt((reviewers.size()-1)-0)+0;
				 String reviewer = reviewers.get(randomNo);
				 System.out.println();
				 System.out.println("Reviewer details by Thesis Commitee");
				 System.out.println(" THE SELECTED REVIEWER IS ..."+reviewer);
				 System.out.println(" REVIEWER NAME TO REMOVE FROM LIST ..."+reviewers.remove(randomNo));
				 System.out.println(" REMAINING REVIEWERS IN LIST ..."+ reviewers);
				 System.out.println(" THESIS ON GOING");  */
				 
			}
			
		}
		
	}
	
	
	

}
