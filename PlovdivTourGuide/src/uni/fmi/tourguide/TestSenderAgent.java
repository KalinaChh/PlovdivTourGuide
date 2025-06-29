package uni.fmi.tourguide;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class TestSenderAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("TestSenderAgent started.");

        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID("guide", AID.ISLOCALNAME));  // 'guide' must match the local name of GuideAgent
        msg.setLanguage("English");
        msg.setContent("History,90,Walking");  // test input

        send(msg);
        System.out.println("Sent test request to GuideAgent.");
    }
}
