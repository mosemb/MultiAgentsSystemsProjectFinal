package studentChoice;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.Agent;

import masProject.Student;

public class InterestedProposal extends Student {
	
	public class Interested extends Behaviour {
	
	int step = 0;
	public void action() {
		// TODO Auto-generated method stub
		
		switch (step) {
		case 0:

			ACLMessage pr = new ACLMessage(ACLMessage.PROPOSE);
			MessageTemplate mtStudentSupp = MessageTemplate.and(MessageTemplate.MatchConversationId("Possible-Proposal"),
					MessageTemplate.MatchInReplyTo(pr.getReplyWith()));
			
				pr.addReceiver(new AID("supervisor", AID.ISLOCALNAME));
				pr.setConversationId("ThesisStudentChoice");
				pr.setReplyWith("Inform_ref " + System.currentTimeMillis());
				pr.setContent("POSSIBLE THESIS");
				send(pr);
		
			
				System.out.println("Test interested");
				//System.out.println(pr);
				
			break;

		}
	}



	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		System.out.println("In done");
		return true;
	}


} 
	}
