package uni.fmi.tourguide;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import javax.swing.*;

public class MessageReceiverAgent extends Agent {

    public static JTextArea outputArea;

    @Override
    protected void setup() {
        System.out.println("MessageReceiverAgent started.");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    System.out.println("GUI Received:\n" + content);

                    // Show in GUI
                    if (outputArea != null) {
                        SwingUtilities.invokeLater(() -> outputArea.setText("Suggested Tour:\n" + content));
                    }
                } else {
                    block();
                }
            }
        });
    }
}
