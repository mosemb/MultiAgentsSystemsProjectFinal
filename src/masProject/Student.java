package masProject;



import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames.MTP;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class Student extends Agent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean enrollDesertation; 
	boolean examinProposals; 
	boolean selectProposal;
	MessageTemplate mtStudentCommt, mtStudentSupp;
	
	ThesisCommt thesisCommt;
	Object [] args;
	
	public void setup() { 
		System.out.println("Agent "+getLocalName()+" is ready. ");
		args = getArguments(); 
		String choice = (String) args[0];
		String [] choices  = {"Company", "Proposals", "StudentChoice"};
		
		for(String x:choices) { 
			
			if(x.equals(choice)) {
				choice = x;
			}
			
		}
		
			switch (choice) {
			case "Company":
				System.out.println("Student Chose to do Thesis with  a company");
				//System.out.println(getAID().getName());
				addBehaviour(new choiceDesertation());
				addBehaviour(new RecievedMsgThCom());
				break;
				
			case "Proposals":
				System.out.println("Chose from Proposals");
				break;
				
			case "StudentChoice":
				System.out.println("Its a Student Choice");
				break;
				

			default:
				System.out.println("Not a valid Choice");
				break; 
			}	

	}
	
	// Put agent clean-up operations here
	protected void takeDown() {
			// Printout a dismissal message
			System.out.println("Student-agent "+getAID().getName()+" terminating.");
		}
	
	public class choiceDesertation extends Behaviour {
		int step = 0; 
		public void action() {
			
					
					switch (step) {
					case 0:
						//System.out.println("Chose a company");
						ACLMessage pr = new ACLMessage(ACLMessage.PROPOSE); 
						pr.addReceiver(new AID( "ThesisCommittee",AID.ISLOCALNAME));
						pr.setConversationId("TChoice-Company");
						pr.setReplyWith("Proposal "+ System.currentTimeMillis());
						pr.setContent("COMPANY THESIS");
						send(pr);
						
						// Prepare a reply from the proposals 
						mtStudentCommt = MessageTemplate.and(MessageTemplate.MatchConversationId("TChoice-Company"), 
								MessageTemplate.MatchInReplyTo(pr.getReplyWith()));
						step =1;
						//System.out.println(pr);
						break;
				
						}
							
			}
		
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return step==2;
		} 
			}
	
	public class RecievedMsgThCom extends CyclicBehaviour{
		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage msg = myAgent.receive(mtStudentCommt);
			
			if(msg!=null) {
				
				if(msg.getConversationId().equals("TChoice-Company")) {
					String content = msg.getContent();
					System.out.println("Reply from Thesis Commitee to Student ...");
					System.out.println(content);
				}
				
			}else {
				block();
			}
			
		}
		
	}
	
	public class StudentProposal extends CyclicBehaviour{
		
		public void action() {
			 selectProposal = true;
			if(selectProposal) {
				
				ACLMessage pr = new ACLMessage(ACLMessage.INFORM); 
				pr.addReceiver(new AID( "supervisor",AID.ISLOCALNAME));
				pr.setConversationId("TChoice-Proposal");
				pr.setReplyWith("Inform "+ System.currentTimeMillis());
				pr.setContent("PROPOSAL THESIS");
				send(pr);
				
				// Prepare a reply from the proposals 
				mtStudentSupp = MessageTemplate.and(MessageTemplate.MatchConversationId("TChoice-Proposal"), 
						MessageTemplate.MatchInReplyTo(pr.getReplyWith()));
				
				
			}else {
				
				block();
				
			}
			
		
		}
	}
	

			
	}
	
	
	


