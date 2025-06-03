package uni.fmi.tourguide;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class TouristAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("TouristAgent " + getLocalName() + " started.");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            String query = (String) args[0];
            System.out.println("Sending query: " + query);
            addBehaviour(new SendQueryBehaviour(query));
        } else {
            System.out.println("No preferences received.");
            doDelete();
        }
    }

    private class SendQueryBehaviour extends Behaviour {
        private final String query;
        private boolean done = false;

        public SendQueryBehaviour(String query) {
            this.query = query;
        }

        @Override
        public void action() {
            try {
                
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.addReceiver(new AID("guide", AID.ISLOCALNAME));
                request.setContent(query);
                send(request);
                System.out.println("📨 Sent query to GuideAgent");

              
                ACLMessage reply = blockingReceive();
                if (reply != null) {
                    String replyContent = reply.getContent();
                    System.out.println("Received tour recommendation:\n" + replyContent);

                
                    ACLMessage guiMsg = new ACLMessage(ACLMessage.INFORM);
                    guiMsg.addReceiver(new AID("guiReceiver", AID.ISLOCALNAME));
                    guiMsg.setContent(replyContent);
                    send(guiMsg);

                    done = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean done() {
            return done;
        }
    }
}
