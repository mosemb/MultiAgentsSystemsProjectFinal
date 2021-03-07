package masProject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ThesisCommt extends Agent{
	
	protected void setup() {
		
		
          System.out.println("Agent "+getLocalName()+" is ready. ");
          //addBehaviour(b);
		
		   addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				
				//ACLMessage msg = myAgent.receive();
				//if (msg != null) {
				// Message received. Process it
				
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
				ACLMessage msg = receive(mt);
				
				if (msg != null) {
					String title = msg.getContent();
					System.out.println("Message matching custom template received:");
					System.out.println(title);
					//System.out.println(title);
				}
				else {
					block();
				}
			}
		} );
		
	}
	
}
