package masProject;

import java.util.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPANames.MTP;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import studentChoice.InterestedProposal;
import studentChoice.InterestedProposal.Interested;

public class Student extends Agent {

	Supervisor stSupervisor = new Supervisor();
	ArrayList<String> tst = stSupervisor.getThesisProposaList();

	/**
	 * 
	 */
	boolean interested = false;
	int randomNo;
	private static final long serialVersionUID = 1L;
	boolean enrollDesertation;
	boolean examinProposals;
	boolean selectProposal;
	MessageTemplate mtStudentCommt, mtStudentSupp;
	Supervisor spSupervisor;
	ThesisCommt thesisCommt;
	String msgFrSup; // Message from supervisor 

	Object[] args;

	public void setup() {
		// System.out.println("Agent "+getLocalName()+" is ready. ");
		args = getArguments();
		String choice = (String) args[0];
		String[] choices = { "Company", "Proposals", "StudentChoice" };

		for (String x : choices) {

			if (x.equals(choice)) {
				choice = x;
			}

		}

		switch (choice) {
		case "Company":
			System.out.println("Student chose to do Thesis with  a company");
			// System.out.println(getAID().getName());
			addBehaviour(new choiceDesertation());
			addBehaviour(new RecievedMsgThCom());

			break;

		case "Proposals":
			System.out.println("Student chose from proposals");
			addBehaviour(new StudentProposal());
			addBehaviour(new ThesisInfoFromSup());
			// addBehaviour(new RecieveNumOfThesis());
			// addBehaviour(new InterestedProposal());

			break;

		case "StudentChoice":
			System.out.println("Its a Student Choice");
			// addBehavior(new test());
			// InnerClass otherInnerVar2 = new OuterClass().new InnerClass();
			//InterestedProposal outerClassObj = new InterestedProposal();
			//InterestedProposal.Interested innerClassObj = outerClassObj.new Interested();
		    String str = "THESIS ASSIGNED, Start Date: 4/5/21 - End Date 10/8/21, THESIS STATUS - ON GOING";
		    
			addBehaviour(new InterestedProposal());
			addBehaviour(new RecievedMsgSupStuChoice());
			addBehaviour(new MsgSupStuChoice()); //ConfirStudentChoice
			//addBehaviour(new ConfirStudentChoice());
			
			break;

		default:
			System.out.println("Not a valid Choice");
			break;
		}

	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Student-agent " + getAID().getName() + " terminating.");
	}

	public class choiceDesertation extends Behaviour {
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
				// System.out.println(pr);
				break;

			}

		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return step == 2;
		}
	}

	public class RecievedMsgThCom extends CyclicBehaviour {
		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage msg = myAgent.receive(mtStudentCommt);

			if (msg != null) {

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
		int num = 0;

		public void action() {
			selectProposal = false;

			switch (num) {
			case 0:
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
			// TODO Auto-generated method stub
			return num == 2;
		}
	}

	public class ThesisInfoFromSup extends CyclicBehaviour {

		public void action() {
			// TODO Auto-generated method stub
			ACLMessage msg = myAgent.receive(mtStudentSupp);

			if (msg != null) {

				String content = msg.getContent();
				System.out.println();
				System.out.println("Reply from Supervisor to Student ...");
				System.out.println(content);
				// System.out.println("Array size :"+tst.size());
				Random rd = new Random(); // Initialize the Random object for boolean

				interested = rd.nextBoolean(); // Randomly select a boolean value for either interested or not
				ACLMessage reply = msg.createReply();
				// System.out.println(reply);
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
				myAgent.send(reply);

			} else {
				block();
			}
		}

	}

	public class RecieveNumOfThesis extends CyclicBehaviour {

		@Override
		public void action() {
			// TODO Auto-generated method stub

			MessageTemplate mT = MessageTemplate.MatchConversationId("numberOfThesis");

			ACLMessage thNo = receive(mT);
			if (thNo != null) {
				String content = thNo.getContent();

				System.out.println("Number of possible thesis available : " + content);
				// System.out.println(content);
				Random random = new Random();
				randomNo = random.nextInt((Integer.parseInt(content) - 1) - 0) + 0;
				// System.out.println("Random number is "+randomNo);

			} else {

				block();
			}

		}
	}

	public class InterestedProposal extends Behaviour {

		@Override
		public void action() {
			// TODO Auto-generated method stub
			int step = 0;
			switch (step) {
			case 0:
				  

					ACLMessage pr = new ACLMessage(ACLMessage.INFORM_REF);
					pr.addReceiver(new AID("supervisor", AID.ISLOCALNAME));
					pr.setConversationId("Possible-Proposal");
					pr.setReplyWith("Inform_ref " + System.currentTimeMillis());
					//String string = String.valueOf(randomNo);
					pr.setContent("I GOT A POSSIBLE THESIS");
					
					send(pr);
					step = 2;
					// interested=false;
					MessageTemplate mtStudentSupp = MessageTemplate.and(MessageTemplate.MatchConversationId("Possible-Proposal"),
							MessageTemplate.MatchInReplyTo(pr.getReplyWith()));
					
				break;

			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return true;
		}

	}
	
	public class RecievedMsgSupStuChoice extends CyclicBehaviour {
		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage msg = myAgent.receive(mtStudentCommt);

			if (msg != null) {

				if (msg.getConversationId().equals("Possible-Proposal")) {
					String content = msg.getContent();
					System.out.println("Reply from Supervisor to Student ...");
					System.out.println(content);
					
					/*ACLMessage pr = new ACLMessage(ACLMessage.CONFIRM);
					pr.addReceiver(new AID("supervisor", AID.ISLOCALNAME));
					pr.setConversationId("Confirm-Thesis");
					pr.setReplyWith("Confirm_ref " + System.currentTimeMillis());
					pr.setContent("THESIS CONFIRMATION RECIEVED");
					send(pr);
					
					MessageTemplate mtStudentSupp = MessageTemplate.and(MessageTemplate.MatchConversationId("Confirm-Thesis"),
							MessageTemplate.MatchInReplyTo(pr.getReplyWith())); */
					
				}

			} else {
				block();
			}

		}

	}
	
	public class MsgSupStuChoice extends CyclicBehaviour {
		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage msg = myAgent.receive(mtStudentCommt);

			if (msg != null) {

				if (msg.getConversationId().equals("Assign_Thesis")) {
					msgFrSup = msg.getContent();
					System.out.println("Reply from Supervisor to Student Wayforward ...");
					System.out.println(msgFrSup );
				}

			} else {
				block();
			}

		}

	}
	
}
