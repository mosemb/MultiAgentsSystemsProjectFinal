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
	MessageTemplate mtStudentCommt;
	ThesisCommt thesisCommt;
	Object [] args;
	
	public void setup() { 
		
		args = getArguments(); 
		String choice = (String) args[0];
		//String [] choices  = {"Company", "Proposals", "StudentChoice"};
		
		//for(String x:choices) {
			
			
				
				
			switch (choice) {
			case "Company":
				System.out.println("Chose a company");
				System.out.println(getAID().getName());
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
		
		/**
		 * 
		 */

		int step = 0; 
		public void action() {
			
					
					switch (step) {
					case 0:
						System.out.println("Chose a company");
						ACLMessage pr = new ACLMessage(ACLMessage.PROPOSE); 
						pr.addReceiver(new AID( "commt",AID.ISLOCALNAME));
						pr.setConversationId("TChoice-Company");
						pr.setReplyWith("Proposal "+ System.currentTimeMillis());
						pr.setContent("Company Thesis");
						send(pr);
						
						// Prepare a reply from the proposals 
						mtStudentCommt = MessageTemplate.and(MessageTemplate.MatchConversationId("TChoice-Company"), 
								MessageTemplate.MatchInReplyTo(pr.getReplyWith()));
						step =1;
						System.out.println(pr);
						break;
					//case 1:
						 /*ACLMessage reply = myAgent.receive(mt);
						 //System.out.println("The MT"+mt);
						 //System.out.println("The Reply"+reply);
						 
						 
						 //ACLMessage reply = myAgent.receive(mt);
							
							if(reply!=null) {
								String cont = reply.getContent();
								
								if(reply.getPerformative()==ACLMessage.ACCEPT_PROPOSAL) {
									System.out.println("Accepted");
									System.out.println(cont); 
									
								}else if (reply.getPerformative()==ACLMessage.REJECT_PROPOSAL) {
									System.out.println("Rejected");
									System.out.println(cont);
								}
								
								 
							}else {
								block();
							}*
							
							//System.out.println(mt);
							//System.out.println("Test");
							step = 2;
							//System.out.println("Step no:"+step);
							break;*/
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
			//mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mtStudentCommt);
			
			if(msg!=null) {
				
				if(msg.getConversationId().equals("TChoice-Company")) {
					String content = msg.getContent();
					System.out.println(content);
				}
				
			}else {
				block();
			}
			
		}
		
	}
			
	}
	
	
	


