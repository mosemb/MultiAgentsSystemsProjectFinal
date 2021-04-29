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
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ThesisCommt extends Agent{
	// The list of known seller agents
		private AID[] studentAgents;
		
	MessageTemplate mtThCommtSupr;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// arraylist to take in the reviewers
	ArrayList<String> reviewers = new ArrayList<>();

	protected void setup() {
		// array to keep commandline arguments 
		Object [] args = getArguments();
		
		// Commandline arguments are put in an arraylist
		for(Object arg:args) {
			
			reviewers.add((String) arg);
			
		} 
		// Register services on yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("ThesisCommt-agent");
		sd.setName("MASTERS-GRADUATION");
		
		dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Behaviors for the Agent
          addBehaviour(new  recieveFromStudent());  
          addBehaviour(new RecieveMessageFromSupC());
          //addBehaviour(new RecieveFromSup()); 
          addBehaviour(new MessageFromReviewer());
          addBehaviour(new FromSupToThComtP()); 
          addBehaviour(new RecieveFromSupCompany());
          addBehaviour(new RecieveBroadCast());
          addBehaviour(new StudentChoiceRecieve());
          addBehaviour(new  RecieveBroadCastFromStudent());
          
          
          // System.out.println("Trying to buy "+targetBookTitle);
		 // Update the list of seller agents
          
      	// Add a TickerBehaviour that schedules a request to seller agents every minute
       // System.out.println("Found the following seller agents:");     
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
					System.out.println();
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
				  
				// Reply to Supervisor the name of reviewer
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM_REF);
				reply.setConversationId("THReviewer");
				reply.setContent("THE REVIEWER FOR THIS THESIS IS "+reviewer);
				send(reply);
				   
				
				   // Reply to Student about name of reviewer
				    ACLMessage tostudent = new ACLMessage(ACLMessage.INFORM_IF);
					tostudent.addReceiver(new AID("student", AID.ISLOCALNAME));
					tostudent.setConversationId("ThReviewerCompany");
					tostudent.setReplyWith("ThReviewerCompany"+ System.currentTimeMillis());
					tostudent.setContent(reviewer);
					send(tostudent); 
					
					
					// Reply to Reviwer details of student
					 ACLMessage toReviwer = new ACLMessage(ACLMessage.INFORM);
					 toReviwer.addReceiver(new AID("reviewer", AID.ISLOCALNAME));
					 toReviwer.setConversationId("ThReviewerCompany2");
					 toReviwer.setReplyWith("ThReviewerCompany2"+ System.currentTimeMillis());
					 toReviwer.setContent(reviewer.toUpperCase()+" You are being assinged to Student 2");
						send(toReviwer); 
				
			}
			
		}
		
	}
	
	
	
	public class MessageFromReviewer extends CyclicBehaviour{
      /*
       * Recieve message from reviewer 
       * 
       * 
       * */
		public void action() {
			MessageTemplate th = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = receive(th); 
			
			// Check whether the message is not empty and has a particular ID. 
			if(msg!=null&&msg.getConversationId().equals("StudentThProgress")) {
				String content = msg.getContent();
				System.out.println(); 
				System.out.println("Message from Reviewer to Thesis Commitee..."); 
				System.out.println(content);
			}
			
			
		}
		
	}
	
	public class FromSupToThComtP extends CyclicBehaviour {
		/*
		 * Message from Supervisor to Thesis committee. 
		 * 
		 * */

		@Override
		public void action() {
			
			
			MessageTemplate mt = MessageTemplate.MatchConversationId("Proposal_Assigned_Thesis");
			ACLMessage msg = receive(mt);
			
			if(msg!=null) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Message from Supervisor to Thesis Commitee");
				System.out.print(content);
				 
			}
			
		}
		
	}
	
	public class RecieveBroadCast extends CyclicBehaviour {
		/*
		 * Recieves broadcast messages for all the agents
		 * 
		 * */

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = receive(mt);
			
			if(msg!=null && msg.getConversationId().equals("BroadCast_id")) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Printing out broadcast message from Student! -ThesisCommitee ");
				System.out.println(content);
				
			}
			
		}
		
	}
	
	 public class YellowPages extends Behaviour{
		 
		 /*
		  * Sends messages to all the student agents. 
		  * 
		  * */
	    	private AID[] allAgents;
	    	
	    	int stop = 0;
			@Override
			public void action() {
				System.out.println();
				System.out.println("Found the following agents on yellow pages");
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("student-agent");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template); 
					Thread.sleep(3000);
					
					allAgents= new AID[result.length];
				stop = 0;
				// Send a message to all agents in the student agents 
				   ACLMessage toall = new ACLMessage(ACLMessage.INFORM);
				   
					for (int i = 0; i < result.length; ++i) {
						allAgents[i] = result[i].getName();
						System.out.println(allAgents[i].getName());
						toall.addReceiver(allAgents[i]);
						stop = stop+1;
					}
					toall.setReplyWith("BroadCastMessage"+System.currentTimeMillis());
					toall.setConversationId("Student_BroadCast_id");
					toall.setContent("Thesis Committee checking in on you");
					send(toall);
				}
				catch (FIPAException | InterruptedException fe) {
					fe.printStackTrace();
				}

			}

			@Override
			public boolean done() {
				// TODO Auto-generated method stub
				return true;
			}
		
	    }
	 
	 public class StudentChoiceRecieve extends CyclicBehaviour {
         /*
          * Receive messages for student choice
          * */
		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM); 
			ACLMessage msg = receive(mt);
			
			if(msg!=null&&msg.getConversationId().equals("Assign_Thesis_SC")) {
				
				String content = msg.getContent();
				System.out.println("Printing from Thesis Committe"); 
				System.out.println();
				System.out.println("Message from Supervisor to Thesis Committee");
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
				 
				//Send message about reviewer
				    ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM_REF);
					reply.setConversationId("DReviewer");
					reply.setContent(reviewer);
					myAgent.send(reply);
					
					// Send message to student
				    ACLMessage tostudent = new ACLMessage(ACLMessage.CFP);
					tostudent.addReceiver(new AID("student", AID.ISLOCALNAME));
					tostudent.setConversationId("ThReviewer");
					tostudent.setReplyWith("Reviewer Th "+ System.currentTimeMillis());
					tostudent.setContent(reviewer);
					send(tostudent); 
					

					//Send message to reviewer
					ACLMessage toreviewer = new ACLMessage(ACLMessage.PROPAGATE);
					toreviewer.addReceiver(new AID("reviewer", AID.ISLOCALNAME));
					toreviewer.setConversationId("StudentReviewer");
					toreviewer.setReplyWith("Reviewer "+System.currentTimeMillis());
					toreviewer.setContent(reviewer.toUpperCase()+" YOU HAVE BEEN ASSIGNED TO STUDENT 1");
					send(toreviewer);
				
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
				
				if(msg!=null && msg.getConversationId().equals("ThesisCom_BroadCast_id")) {
					String content = msg.getContent();
					System.out.println();
					System.out.println("Printing out broadcast message from Student! -Thesis Committee ");
					System.out.println(content);
					
				}
				
			}
			
		}
	 
}
