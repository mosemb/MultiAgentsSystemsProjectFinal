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
		
		
	
          addBehaviour(new  recieveFromStudent());
          addBehaviour(new RecieveMessageFromSupC());
          addBehaviour(new RecieveFromSup());
         // MessageFromReviewer
          addBehaviour(new MessageFromReviewer());
        
          
               
    }
		  
	
	public class recieveFromStudent extends  CyclicBehaviour {
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
				ACLMessage msg = receive(mt);
				
				if (msg != null) {
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
						
					
						ACLMessage toSupervisor = new ACLMessage(ACLMessage.REQUEST);
						toSupervisor.addReceiver(new AID("supervisor", AID.ISLOCALNAME));
						toSupervisor.setConversationId("TChoice-Company2");
						toSupervisor.setReplyWith("Request ThCom1 "+ System.currentTimeMillis());
						toSupervisor.setContent("REVIEW THESIS");
						send(toSupervisor);
						
						mtThCommtSupr = MessageTemplate.and(MessageTemplate.MatchConversationId("TChoice-Company2"), 
								MessageTemplate.MatchInReplyTo(toSupervisor.getReplyWith()));
						
						//System.out.println(mtThCommtSupr);
						
					
						
					}else {
						reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
						reply.setContent("PROPOSAL REJECTED, START AGAIN");
						//System.out.println("Go back to commandline to choose either Company, Proposal or StudentChoice");
			            
					}
					myAgent.send(reply);
					
				}
				else {
					block();
				}
			}
		}
	
	public class RecieveMessageFromSupC extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
			ACLMessage msg = myAgent.receive(mtThCommtSupr);
			//System.out.println(msg);
			
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
	
	
	

}
