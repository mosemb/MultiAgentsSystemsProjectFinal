package masProject;


import java.util.Random;

import jade.core.AID;
//import jade.core.AID;
import jade.core.Agent;
//import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ThesisCommt extends Agent{
	MessageTemplate mtThCommtSupr;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void setup() {
		
		
         // System.out.println("Agent "+getLocalName()+" is ready. ");
          
          addBehaviour(new  recieveFromStudent());
          addBehaviour(new RecieveMessageFromSupC());
          addBehaviour(new MsgSupStuChoice());
          
               
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
	
	public class MsgSupStuChoice extends CyclicBehaviour {
		@Override
		public void action() {
			// TODO Auto-generated method stub 
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
			ACLMessage msg = receive(mt);
			

			if (msg != null) {

				if (msg.getConversationId().equals("Confirm-Thesis")) {
					String content = msg.getContent();
					System.out.println("Reply from Student to Thesis Comm  ...");
					System.out.println(content);
				}

			} else {
				block();
			}

		}

	}
}
