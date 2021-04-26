package masProject;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Thesis  extends Agent{
	
	public void setup() {
		
		addBehaviour(new RecieveMessageFromStudent());
		
	}
	
	public class RecieveMessageFromStudent extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
			ACLMessage msg = receive(mt);
			
			if(msg!=null) {
				String content = msg.getContent();
				System.out.println();
				System.out.println("Message from Student to Thesis Agent");
				System.out.println(content);
			}
			
		}
		
	}

}
