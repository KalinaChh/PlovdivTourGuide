package uni.fmi.tourguide;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.CyclicBehaviour;

import java.util.List;

public class GuideAgent extends Agent {

    private PlovdivOntology ontology;

    @Override
    protected void setup() {
        System.out.println("GuideAgent " + getLocalName() + " started.");
        ontology = new PlovdivOntology("files/plovdiv.owl");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = receive(mt);

                if (msg != null) {
                    System.out.println("Received query from " + msg.getSender().getLocalName());

                    String content = msg.getContent(); //"History,90,Walking"
                    String[] parts = content.split(",");
                    if (parts.length != 3) {
                        System.out.println("Invalid query format.");
                        return;
                    }

                    String interest = parts[0].trim();
                    int duration = Integer.parseInt(parts[1].trim());
                    String mobility = parts[2].trim();

                    List<String> locations = ontology.getTourLocations(interest, duration, mobility);

                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);

                    if (locations.isEmpty()) {
                        reply.setContent("No matches found.");
                    } else {
                        StringBuilder sb = new StringBuilder("Suggested Tour:\n");
                        for (String loc : locations) {
                            sb.append(" - ").append(loc).append("\n");
                        }
                        reply.setContent(sb.toString());
                    }

                    send(reply);
                    System.out.println("Sent response to " + msg.getSender().getLocalName());
                } else {
                    block(); // wait for next message
                }
            }
        });
    }
}
