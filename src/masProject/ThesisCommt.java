package masProject;


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
		
		
          System.out.println("Agent "+getLocalName()+" is ready. ");
          
          addBehaviour(new  recieveFromStudent());
          addBehaviour(new RecieveMessageFromSupC());
          
     
          
    }
		  
	
	public class recieveFromStudent extends  CyclicBehaviour {
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
				ACLMessage msg = receive(mt);
				
				if (msg != null) {
					String content = msg.getContent(); 
					boolean acceptable = true; 
					System.out.println("Message from Student to Thesis Commitee ... ");
					ACLMessage reply = msg.createReply();
					System.out.println(content);
					System.out.println();
					
					
					if(acceptable==true) {
						reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						reply.setContent("PROPOSAL ACCEPTED"); 
						
					
						ACLMessage toSupervisor = new ACLMessage(ACLMessage.REQUEST);
						toSupervisor.addReceiver(new AID("supervisor", AID.ISLOCALNAME));
						// Might make the name dynamic by changing the String instance variable.
						//toSupervisor.setConversationId("REVISE THESIS");
						toSupervisor.setConversationId("TChoice-Company2");
						toSupervisor.setReplyWith("Request ThCom1 "+ System.currentTimeMillis());
						toSupervisor.setContent("REVIEW THESIS");
						send(toSupervisor);
						
						mtThCommtSupr = MessageTemplate.and(MessageTemplate.MatchConversationId("TChoice-Company2"), 
								MessageTemplate.MatchInReplyTo(toSupervisor.getReplyWith()));
						
						//System.out.println(mtThCommtSupr);
						
					
						
					}else {
						reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
						reply.setContent("PROPOSAL REJECTED");
			
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
}
