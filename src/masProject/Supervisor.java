package masProject;



import java.util.ArrayList;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Supervisor  extends Agent{
	
	ArrayList<String> thesisProposal = new ArrayList<String>();
	
	public ArrayList<String> getThesisProposaList(){
		
		return thesisProposal;
		
	}
	
	
	public void setup() {
		// Gets arguments from command line for the  proposal list. 
		Object [] args = getArguments();
		
		
		// Adds proposals to the arraylist thisProposal
		for(Object i: args) {
		
			thesisProposal.add((String) i);
			
		}
		
		
		addBehaviour(new RequestsFromThesisCom());
		addBehaviour(new MessageFromStudent());
		addBehaviour(new StudentToSupervisor());
	    addBehaviour(new RecieveStudentThesisChoice());
	    addBehaviour(new RecieveFromThCom());
	    
		
		
		
	}
	
	// Put agent clean-up operations here
	protected void takeDown() {
				// Printout a dismissal message
				System.out.println("Supervisor-agent "+getAID().getName()+" terminating.");
			}
	
	public class RequestsFromThesisCom extends CyclicBehaviour{
	/*
	 * 
	 * Recieves message from Thesis Committe to review the student thesis from a company
	 * 
	 * 
	 * */

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = receive(mt); 
			
			if(msg!=null) {
				// Recieves the message from the Thesis Committee 
				String content = msg.getContent();
				
				System.out.println();
				System.out.println("Message from Thesis Committee to Supervisor ...");
				System.out.println(content);
				
				// Sends back the message to The Thesis Committee Informing them the Thesis is being reviewed
				ACLMessage reply = msg.createReply(); // Reply message to sender. 
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent("THESIS BEING REVIEWED");
				myAgent.send(reply);
				
				// Sends message to Student informing them of the new details.
				ACLMessage toStudent = new ACLMessage(ACLMessage.CFP); 
				toStudent.addReceiver(new AID( "student",AID.ISLOCALNAME));
				toStudent.setConversationId("WayForwardCompany");
				toStudent.setReplyWith("ThesisNum "+ System.currentTimeMillis());
				toStudent.setContent("THESIS ASSIGNED, Start Date: 4/5/21 - End Date 10/8/21, THESIS STATUS - ON GOING");
				send(toStudent);
				
				//Sends message to Thesis Committee Thesis being reviewed. 
				ACLMessage toThCom = new ACLMessage(ACLMessage.INFORM_REF); 
				toThCom.addReceiver(new AID( "ThesisCommittee",AID.ISLOCALNAME));
				toThCom.setConversationId("TheAssignedToStuCompany");
				toThCom.setReplyWith("ThesisAssignement "+ System.currentTimeMillis());
				toThCom.setContent("THESIS ASSIGNED TO STUDENT");
				send(toThCom);
				
				
				
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
			ACLMessage no = receive(mTemplate);
			
			
			if(AclMessage!=null) {
				
				String contentString = AclMessage.getContent();
				System.out.println();
				System.out.println("Message from Student to Supervisor ... ");
				System.out.println(contentString);
			
			
				ACLMessage reply = AclMessage.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent("THESIS INFORMATION");
				myAgent.send(reply);
					
				
			} else {
				
				block();
			}
			
		}
		
		
	}
	
	public class NoOfProposals extends OneShotBehaviour{
		
		@Override
		public void action() {
			// TODO Auto-generated method stub
	            int step = 0;
					//System.out.println("Chose a company");
					ACLMessage pr = new ACLMessage(ACLMessage.CFP); 
					pr.addReceiver(new AID( "student",AID.ISLOCALNAME));
					pr.setConversationId("numberOfThesis");
					pr.setReplyWith("ThesisNum "+ System.currentTimeMillis());
					
					int size = thesisProposal.size();
					String s=String.valueOf(size);
				
					pr.setContent(s);
					send(pr);
					
					// Prepare a reply from the proposals 
					MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("numberOfThesis"), 
							MessageTemplate.MatchInReplyTo(pr.getReplyWith()));
					
					}
		}
		

	
	public class StudentToSupervisor extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mTemplate = MessageTemplate.MatchConversationId("TChoice-Proposal1");
			ACLMessage msg = receive(mTemplate);
			
			if(msg!=null) {
				String content = msg.getContent();
				
				System.out.println();
				System.out.println("Reply from Student to Supervisor about Thesis decision ... ");
				System.out.println(content);
				System.out.println();
	            
				String selected = "THESIS_SELECTED";
				
				 if(content.equals(selected) ) {
					 Random random = new Random(); 
					 int randomNo = random.nextInt((thesisProposal.size()-1)-0)+0;
					 System.out.println(" THE SELECTED THESIS IS ..."+thesisProposal.get(randomNo));
					 System.out.println(" THESIS NAME TO REMOVE FROM LIST ..."+thesisProposal.remove(randomNo));
					 System.out.println(" REMAINING THESIS PROPOSALS IN LIST ..."+ thesisProposal);
					 
					ACLMessage tostu = new ACLMessage(ACLMessage.PROPOSE);
					 tostu.addReceiver( new AID("student",AID.ISLOCALNAME));
					 tostu.setConversationId("Proposal_Assigned");
					 tostu.setReplyWith("Proposal_Thesis "+ System.currentTimeMillis());
					 tostu.setContent("THESIS ASSIGNED, Start Date: 4/5/21 - End Date 10/8/21, THESIS STATUS - ON GOING");
					 send(tostu);
					 
					/* ACLMessage toCommt = new ACLMessage(ACLMessage.INFORM);
						toCommt.addReceiver(new AID("ThesisCommittee", AID.ISLOCALNAME));                       
						toCommt.setConversationId("Proposal_Assigned_Thesis");
						toCommt.setReplyWith("Proposal_Accepted"+ System.currentTimeMillis());
						toCommt.setContent("THESIS ASSIGNED, TO STUDENT");
						send(toCommt); */
					 
				 }
					
			}else {
				
				block();
			}
			
		}
		
	}
	
	public class RecieveStudentThesisChoice extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mTemplate  = MessageTemplate.MatchConversationId("Possible-Proposal"); 
	        ACLMessage msg  = receive(mTemplate); 
	        
			if(msg!=null) {
				String content = msg.getContent(); 
				System.out.println("Student message to Supervisor :"+content); 
				
				 Random rand = new Random();
			     boolean selected = rand.nextBoolean();
			     
			     if(selected == true ) {
			    	 
			    	    ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						reply.setContent("THESIS HAS BEEN SELECTED, AM YOUR SUPERVISOR");
						myAgent.send(reply);
						
						ACLMessage tostudent = new ACLMessage(ACLMessage.INFORM);
						tostudent.addReceiver(new AID("student", AID.ISLOCALNAME));
						// Might make the name dynamic by changing the String instance variable.
						//toSupervisor.setConversationId("REVISE THESIS");
						tostudent.setConversationId("Assign_Thesis");
						tostudent.setReplyWith("Assign Thesis "+ System.currentTimeMillis());
						tostudent.setContent("THESIS ASSIGNED, Start Date: 4/5/21 - End Date 10/8/21, THESIS STATUS - ON GOING");
						send(tostudent);
						
						
						ACLMessage toCommt = new ACLMessage(ACLMessage.CONFIRM);
						toCommt.addReceiver(new AID("ThesisCommittee", AID.ISLOCALNAME));                       
						toCommt.setConversationId("Assign_Thesis_TC");
						toCommt.setReplyWith("Assign_Thesis_TC"+ System.currentTimeMillis());
						toCommt.setContent("THESIS ASSIGNED, TO STUDENT");
						send(toCommt);
						
						MessageTemplate	mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Assign_Thesis_TC"), 
								MessageTemplate.MatchInReplyTo(toCommt.getReplyWith()));
						
						MessageTemplate	mtAssingThesis = MessageTemplate.and(MessageTemplate.MatchConversationId("Assign_Thesis"), 
								MessageTemplate.MatchInReplyTo(tostudent.getReplyWith()));
						
						
			    	 
			     }else {
			    	    ACLMessage reply = msg.createReply();
			    	    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
						reply.setContent("THESIS HAS BEEN REJECTED BEGIN PROCESS AGAIN");
						myAgent.send(reply);
			    	 
			     }
				
			}else {
				//System.out.println(msg);
				block();
			}
		}
		
	}
	
	public class RecieveFromThCom extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			//MessageTemplate th = MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF); 
			MessageTemplate th = MessageTemplate.MatchConversationId("DReviewer");
			ACLMessage msg = receive(th);
			
			if(msg!=null) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Message from Thesis Committe to Supervisor concerning Reviewer name ...");
				System.out.println(content.toUpperCase());
				
				//System.out.println(content);
				
			}
			
		}
		
	}
	

}
