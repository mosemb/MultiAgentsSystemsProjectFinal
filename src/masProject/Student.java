package masProject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class Student extends Agent{
	
	boolean enrollDesertation; 
	boolean examinProposals; 
	ThesisCommt thesisCommt;
	Object [] args;
	
	public void setup() { 
		
		args = getArguments(); 
		
		System.out.println(getAID().getName());
		addBehaviour(new choiceDesertation());
		
	}
	
	// Put agent clean-up operations here
		protected void takeDown() {
			// Printout a dismissal message
			System.out.println("Student-agent "+getAID().getName()+" terminating.");
		}
	
	public class choiceDesertation extends Behaviour {
		
		String choice = (String) args[0];
		String [] choices  = {"Company", "Proposals", "StudentChoice"};
		public void action() {
			
			
			for(String x: choices) {
				
				if(choice.equals(x)) {
					choice = x;
					break;}
			}
			
			
				switch (choice) {
				
				case "Company":
					System.out.println("Chose a company");
					ACLMessage pr = new ACLMessage(ACLMessage.PROPOSE); 
					pr.addReceiver(new AID( "commtee",AID.ISLOCALNAME));
					pr.setConversationId("Thesis-Choice-Company");
					pr.setReplyWith("Proposal "+ System.currentTimeMillis());
					pr.setContent("Company Thesis");
					send(pr);
					System.out.print(pr);
					
					break;
					
				case "Proposals":
					System.out.println("Chose a proposal"); 
					break;
					
				case "StudentChoice":
					System.out.println("Chose a student choice");
					break;
				default:
					System.out.println("Choice not found");
					break;
				
			} 
			
			
			
		}
		
		
		public boolean done() {
			
			return choice.equals((String)args[0]);
		}
		
	} 
	
	
	

}
