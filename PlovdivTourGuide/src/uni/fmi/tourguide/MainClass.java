package uni.fmi.tourguide;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Runtime;

public class MainClass {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();

            // change ports, sometimes i get an error? 
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.GUI, "true");
            profile.setParameter(Profile.LOCAL_HOST, "localhost");
            profile.setParameter(Profile.LOCAL_PORT, "1200");

            ContainerController cc = rt.createMainContainer(profile);
            System.out.println("JADE platform started");

            AgentController guideAgent = cc.createNewAgent(
                "guide", "uni.fmi.tourguide.GuideAgent", null);
            guideAgent.start();

            Object[] touristArgs = new Object[] { "History,90,Walking" };
            AgentController touristAgent = cc.createNewAgent(
                "tourist", "uni.fmi.tourguide.TouristAgent", touristArgs);
            touristAgent.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
