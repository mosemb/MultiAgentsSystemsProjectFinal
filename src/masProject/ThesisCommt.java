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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void setup() {
		
		
          System.out.println("Agent "+getLocalName()+" is ready. ");
          
          
          addBehaviour(new TickerBehaviour(this,2000) {
			
			@Override
			protected void onTick() {
				// TODO Auto-generated method stub
				myAgent.addBehaviour(new  recieveFromStudent());
				
			}
		});
          
    }
		  
	
	public class recieveFromStudent extends  CyclicBehaviour {
			public void action() {
				
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
				ACLMessage msg = receive(mt);
				
				if (msg != null) {
					String title = msg.getContent();
					boolean acceptable = false; 
					System.out.println("Message matching custom template received:");
					ACLMessage reply = msg.createReply();
					System.out.println(title);
					
					
					if(acceptable==true) {
						reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						reply.setContent("PROPOSAL ACCEPTED"); 
					
						
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
	
	
	
	
}
