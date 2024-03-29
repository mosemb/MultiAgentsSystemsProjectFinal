package masProject;

import java.util.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames.MTP;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Student extends Agent {

	Supervisor stSupervisor = new Supervisor();
	ArrayList<String> tst = stSupervisor.getThesisProposaList();

	/**
	 * 
	 */
	boolean interested = false;
	int randomNo;
	private static final long serialVersionUID = 1L;
	boolean selectProposal;
	MessageTemplate mtStudentCommt, mtStudentSupp;
	Supervisor spSupervisor;
	ThesisCommt thesisCommt;
	String msgFrSup; // Message from supervisor 

	Object[] args;

	public void setup() {
		
		// Get commandline arguments
		args = getArguments();
		String choice = (String) args[0];
		String[] choices = { "Company", "Proposals", "StudentChoice", "YellowPages", "Student", "Reviewer"};
		
		//System.out.println("agent "+getAID().getLocalName() +" starting.");

		// Available choices
		for (String x : choices) {

			if (x.equals(choice)) {
				choice = x;
			}

		}
        /* From available choices entered at commandline run behaviors based either 
		 Company, Proposals, StudentChoice
		 
		 */
		switch (choice) {
		case "Company":
			System.out.println();
			System.out.println("Student chose to do Thesis with  a company"); 
			addBehaviour(new choiceDesertation());
			addBehaviour(new RecievedMsgThCom());
			addBehaviour(new RecieveMsgFromSupCom());
			addBehaviour(new FromThComReviewerCompany());
			

			break;

		case "Proposals":
			System.out.println();
			System.out.println("Student chose from proposals");
			addBehaviour(new StudentProposal());
			addBehaviour(new ThesisInfoFromSup());
			addBehaviour(new RecieveFromSupProposal());
			
			break;

		case "StudentChoice":
			System.out.println();
			System.out.println("Its a Student Choice");
			addBehaviour(new InterestedProposal());
			//addBehaviour(new RecievedMsgSupStuChoice());
			addBehaviour(new MsgSupStuChoice()); 
			addBehaviour(new ThRecieveFrThCom());
		
			
			break;
		case "YellowPages":
			System.out.println();
			System.out.println("Agents on yellow pages");
			addBehaviour(new YellowPages());
			break;
			
		case "ThesisComm":
			System.out.println();
			System.out.println("Thesis communication");
			//YellowPagesThCom
			addBehaviour(new YellowPagesThCom());
			//addBehaviour(new RecieveBroadCastThCom());
			break;
			
		case "Supervisor":
			System.out.println();
			System.out.println("Supervisor communication");
			addBehaviour(new YellowPagesSup());
			break;
			
		case "Reviewer":
			System.out.println();
			System.out.println("Reviewer communication");
			addBehaviour(new YellowPagesRev());
				
			break;

		default:
			System.out.println("Not a valid Choice");
			break;
		}
		
		// Register student on yellow pages
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.setName(getAID());
				
				ServiceDescription sd = new ServiceDescription();
				sd.setType("student-agent");
				sd.setName("MASTERS-GRADUATION");
				
				dfd.addServices(sd);
				
				try {
					DFService.register(this, dfd);
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
		
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Student-agent " + getAID().getName() + " terminating.");
	}
    
	public class choiceDesertation extends Behaviour {
		/*
		 * This class is called when the "Company" Choice is chosen. 
		 * The student sends out the message to the Thesis Committe. 
		 * 
		 * */
		int step = 0;

		public void action() {

			switch (step) {
			case 0:
				// System.out.println("Chose a company");
				ACLMessage pr = new ACLMessage(ACLMessage.PROPOSE);
				pr.addReceiver(new AID("ThesisCommittee", AID.ISLOCALNAME));
				pr.setConversationId("TChoice-Company");
				pr.setReplyWith("Proposal " + System.currentTimeMillis());
				pr.setContent("COMPANY THESIS");
				send(pr);

				// Prepare a reply from the proposals
				mtStudentCommt = MessageTemplate.and(MessageTemplate.MatchConversationId("TChoice-Company"),
						MessageTemplate.MatchInReplyTo(pr.getReplyWith()));
				step = 2;
				break;

			}

		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return step == 2;
		}
	}
	
	public class RecieveMsgFromSupCom extends CyclicBehaviour{
		/*
		 * Recieves message from Supervisor Agent about new Details for the thesis. 
		 * Connects with inner class RequestsFromThesisCom 
		 * */

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchConversationId("WayForwardCompany");
			ACLMessage msg = receive(mt);
			
			if(msg!=null) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Message from Supervisor to Student About new Dates for the Thesis ... ");
				System.out.println(content);
			}
			
		}
		
	}
	
	public class FromThComReviewerCompany extends CyclicBehaviour{
		
		/* Thesis committee sends message to student about the Reviewer of the Thesis
		 * Connected to Inner class RecieveFromSupCompany
		 * */

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchConversationId("ThReviewerCompany");
			ACLMessage msg = receive(mt); 
			
			if(msg!=null) {
				String content = msg.getContent();
				System.out.println ();
				System.out.println("Message from Thesis Committe To Student About Thesis Reviewer");
				System.out.println(content.toUpperCase());
				
			}
			
			
		}
		
	}

	public class RecievedMsgThCom extends CyclicBehaviour {
		/*
		 * Recieve message from Thesis committee to Student ... 
		 * 
		 * */
		@Override
		public void action() {
			// Recieve message 
			ACLMessage msg = myAgent.receive(mtStudentCommt);

			if (msg != null) {
                   // Recieve message with the following requirements
				if (msg.getConversationId().equals("TChoice-Company")) {
					String content = msg.getContent();
					System.out.println("Reply from Thesis Commitee to Student ...");
					System.out.println(content);
				}

			} else {
				block();
			}

		}

	}

	public class StudentProposal extends Behaviour {
		/*
		 * Student sends message to Supervisor about Thesis Proposal. 
		 * */
		int num = 0;

		public void action() {
			selectProposal = false;

			switch (num) {
			case 0:
				// Prepare and send message 
				ACLMessage pr = new ACLMessage(ACLMessage.QUERY_REF);
				pr.addReceiver(new AID("supervisor", AID.ISLOCALNAME));
				pr.setConversationId("TChoice-Proposal");
				pr.setReplyWith("Inform " + System.currentTimeMillis());
				pr.setContent("PROPOSED THESIS, SEND MORE INFO");
				send(pr);

				// Prepare a reply from the proposals
				mtStudentSupp = MessageTemplate.and(MessageTemplate.MatchConversationId("TChoice-Proposal"),
						MessageTemplate.MatchInReplyTo(pr.getReplyWith()));
				num = 1;
				break;
			case 2:
				break;

			}

		}

		@Override
		public boolean done() {
			return num == 2;
		}
	}

	public class ThesisInfoFromSup extends CyclicBehaviour {
      /*
       * Get thesis information from supervisor and decide whether thesis is accepted or not. 
       * 
       * */
		public void action() {
			// Recieve message
			ACLMessage msg = myAgent.receive(mtStudentSupp);

			if (msg != null) {
               
				// Get specific message. 
				String content = msg.getContent();
				System.out.println();
				System.out.println("Reply from Supervisor to Student ...");
				System.out.println(content);
				Random rd = new Random(); // Initialize the Random object for boolean

				interested = rd.nextBoolean(); // Randomly select a boolean value for either interested or not
				ACLMessage reply = msg.createReply();
		
				
				if (interested == true) {

					reply.setPerformative(ACLMessage.CONFIRM);
					String string = String.valueOf(randomNo);

					reply.setContent("THESIS_SELECTED");
					reply.setConversationId("TChoice-Proposal1");
				} else {

					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("THESIS NOT SELECTED");
					reply.setConversationId("TChoice-Proposal1");
				}
				// Send reply to sender. 
				myAgent.send(reply);

			} else {
				block();
			}
		}

	}

	

	public class InterestedProposal extends Behaviour {
		/*
		 * Student sends message to supervisor about possible thesis proposal
		 * */

		@Override
		public void action() {
		
				// Send message to supervisor about student based proposal
					ACLMessage pr = new ACLMessage(ACLMessage.PROXY);
					pr.addReceiver(new AID("supervisor", AID.ISLOCALNAME));
					pr.setConversationId("Possible-Proposal_1");
					pr.setReplyWith("Inform_Proposal " + System.currentTimeMillis());
					pr.setContent("I GOT A POSSIBLE THESIS");
					send(pr);
					
					MessageTemplate mtStudentSupp = MessageTemplate.and(MessageTemplate.MatchConversationId("Possible-Proposal_1"),
							MessageTemplate.MatchInReplyTo(pr.getReplyWith()));

			}
		

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return true;
		}

	}
	
	public class RecievedMsgSupStuChoice extends CyclicBehaviour {
		/*
		 * Message from Supervisor to Student about Possible thesis Proposal
		 * 
		 * */
		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage msg = myAgent.receive(mtStudentCommt);

			if (msg != null) {
                    // Recieve message 
				if (msg.getConversationId().equals("Possible-Proposal")) {
					String content = msg.getContent();
					System.out.println();
					System.out.println("Reply from Supervisor to Student ...");
					System.out.println(content);
						
				}

			} else {
				block();
			}

		}

	}
	
	public class MsgSupStuChoice extends CyclicBehaviour {
		/*
		 * Message from Supervisor to Student about StudentChoice Thesis
		 * 
		 * */
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = receive(mt);

			if (msg != null&& msg.getConversationId().equals("Student_Thesis_Accepted")) {
                    // Recieve message. 
					String msgFrSup = msg.getContent();
					System.out.println();
					System.out.println("Reply from Supervisor to Student Wayforward ...");
					System.out.println(msgFrSup );
			
			}else {
				block(); 
			}

	} }
	
	public class ThRecieveFrThCom extends CyclicBehaviour {
        /*
         * Reviewer information sent by Thesis Committee. 
         * Send thesis information to Reviewer and Thesis Agents 
         * 
         * */
		public void action() {
			
			MessageTemplate mt = MessageTemplate.MatchConversationId("ThReviewer");
			ACLMessage msg = receive(mt); 
			
			if (msg!=null) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Thesis Reviewer name from Thesis Committe to Student... ");
				System.out.println(content.toUpperCase());
				
				ACLMessage progress = new ACLMessage(ACLMessage.SUBSCRIBE);
				progress.addReceiver(new AID("reviewer", AID.ISLOCALNAME));
				progress.setReplyWith("Inform_progress " + System.currentTimeMillis());
				progress.setConversationId("ThesisProgress");
				progress.setContent("THESIS PROGRESS ON COURSE");
				send(progress);
				
				ACLMessage thesis = new ACLMessage(ACLMessage.AGREE);
				thesis.addReceiver(new AID("thesis",AID.ISLOCALNAME));
				thesis.setReplyWith("Agree thesis in progress"+System.currentTimeMillis());
				thesis.setConversationId("ThesisWritting");
				thesis.setContent("THESIS WRITTING IN PROGRESS");
				send(thesis); 
				
			}
			
		}
		
	}
	
	public class RecieveFromSupProposal extends CyclicBehaviour{
		/*
		 * Message from supervisor to student about wayforward for available proposals
		 * 
		 * */

		public void action() {
			MessageTemplate mt = MessageTemplate.MatchConversationId("Proposal_Assigned");
			ACLMessage msg = receive(mt);
			
			if(msg!=null) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Message from Supervisor to Student about Wayforward... ");
				System.out.println(content);
			}
			
		}
		
	}
	
    public class YellowPages extends Behaviour{
    	/*
    	 * Returns all agents in the yellow pages. 
    	 * */
    	private AID[] allAgents;
    	
    	int stop = 0;
		@Override
		public void action() {
			// Return all the agents in yellow pages. 
			System.out.println();
			System.out.println("Found the following agents on yellow pages");
			DFAgentDescription template = new DFAgentDescription();
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template); 
				Thread.sleep(4000);
				
				allAgents= new AID[result.length];
			stop = 0;
			// Send a message to all agents 
			   ACLMessage toall = new ACLMessage(ACLMessage.INFORM);
			   
				for (int i = 0; i < result.length; ++i) {
					allAgents[i] = result[i].getName();
					System.out.println(allAgents[i].getName());
					toall.addReceiver(allAgents[i]);
					stop = stop+1;
					//System.out.println(result.length);
				}
				toall.setReplyWith("BroadCastMessage"+System.currentTimeMillis());
				toall.setConversationId("BroadCast_id");
				toall.setContent("Student checking in on you");
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
    
    public class RecieveBroadCastThCom extends CyclicBehaviour {
    	/*Recieves all broadcast messages for all agents in the yellow pages. */

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = receive(mt);
			
			if(msg!=null && msg.getConversationId().equals("Student_BroadCast_id")) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Printing out broadcast message from Thesis Committee! -Student ");
				System.out.println(content);
				
			}
			
		}
		
	}
    
    public class RecieveBroadCastSup extends CyclicBehaviour {
    	/*
    	 * Recieves broadcasts to all agents in the yellow pages that are students
    	 * */

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = receive(mt);
			
			if(msg!=null && msg.getConversationId().equals("Supervisor_BroadCast_id")) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Printing out broadcast message from Sup Committee! -Student ");
				System.out.println(content);
				
			}
			
		}
		
	}
    

	 public class YellowPagesSup extends Behaviour{
		 /*
		  * Sends yellowpage messages to all supervisor agents. 
		  * */
	    	private AID[] allAgents;
	    	
	    	int stop = 0;
			@Override
			public void action() {
				System.out.println();
				System.out.println("Found the following agents on yellow pages");
				System.out.println();
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("supervisor-agent");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template); 
					Thread.sleep(3000);
					
					allAgents= new AID[result.length];
				stop = 0;
				//Send a message to all agents in the student agents 
				   ACLMessage toall = new ACLMessage(ACLMessage.CFP);
				   
					for (int i = 0; i < result.length; ++i) {
						allAgents[i] = result[i].getName();
						System.out.println(allAgents[i].getName());
						toall.addReceiver(allAgents[i]);
						stop = stop+1;
					}
					toall.setReplyWith("BroadCastMessage"+System.currentTimeMillis());
					toall.setConversationId("Student_BroadCast_id");
					toall.setContent("Student checking in on you");
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
	 
	 public class YellowPagesRev extends Behaviour{
		 /*
		  * Sends yellowpage messages to all reviewer agents. 
		  * */
	    	private AID[] allAgents;
	    	
	    	int stop = 0;
			@Override
			public void action() {
				System.out.println();
				System.out.println("Found the following agents on yellow pages");
				System.out.println();
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("reviewer-agent");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template); 
					Thread.sleep(3000);
					
					allAgents= new AID[result.length];
				stop = 0;
				//Send a message to all agents in the student agents 
				   ACLMessage toall = new ACLMessage(ACLMessage.CFP);
				   
					for (int i = 0; i < result.length; ++i) {
						allAgents[i] = result[i].getName();
						System.out.println(allAgents[i].getName());
						toall.addReceiver(allAgents[i]);
						stop = stop+1;
					}
					toall.setReplyWith("BroadCastMessage"+System.currentTimeMillis());
					toall.setConversationId("Reviewer_BroadCast_id");
					toall.setContent("Student checking in on you");
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
	 
	 public class YellowPagesThCom extends Behaviour{
		 /*
		  * Sends yellowpage messages to all Thesis Committee agents. 
		  * */
	    	private AID[] allAgents;
	    	
	    	int stop = 0;
			@Override
			public void action() {
				System.out.println();
				System.out.println("Found the following agents on yellow pages");
				System.out.println();
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("ThesisCommt-agent");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template); 
					Thread.sleep(3000);
					
					allAgents= new AID[result.length];
				stop = 0;
				//Send a message to all agents in the student agents 
				   ACLMessage toall = new ACLMessage(ACLMessage.CFP);
				   
					for (int i = 0; i < result.length; ++i) {
						allAgents[i] = result[i].getName();
						System.out.println(allAgents[i].getName());
						toall.addReceiver(allAgents[i]);
						stop = stop+1;
					}
					toall.setReplyWith("BroadCastMessage"+System.currentTimeMillis());
					toall.setConversationId("ThesisCom_BroadCast_id");
					toall.setContent("Student checking in on you");
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
	
}
