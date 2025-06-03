package uni.fmi.tourguide;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

@SuppressWarnings("serial")
public class TourGuideGUI extends JFrame {

    private JCheckBox historyBox = new JCheckBox("History");
    private JCheckBox artBox = new JCheckBox("Art");
    private JCheckBox natureBox = new JCheckBox("Nature");

    private JComboBox<String> durationBox = new JComboBox<>(new String[]{"30", "60", "90", "120"});
    private JComboBox<String> mobilityBox = new JComboBox<>(new String[]{"Walking", "Cycling", "Driving"});

    public static JTextArea resultArea = new JTextArea(10, 30);
    private JButton searchBtn = new JButton("Find Tour");

    private ContainerController container;

    public TourGuideGUI() {
        super("🧭 Plovdiv Tour Guide");

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top controls
        JPanel inputPanel = new JPanel();
        inputPanel.add(historyBox);
        inputPanel.add(artBox);
        inputPanel.add(natureBox);

        inputPanel.add(new JLabel("Duration (min):"));
        inputPanel.add(durationBox);

        inputPanel.add(new JLabel("Mobility:"));
        inputPanel.add(mobilityBox);

        inputPanel.add(searchBtn);
        add(inputPanel, BorderLayout.NORTH);

        // Results
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
        
     // Load and add map image
        URL imageUrl = getClass().getResource("/uni/fmi/tourguide/resources/Plovdiv_City_Map.jpg");
        if (imageUrl != null) {
        	ImageIcon originalIcon = new ImageIcon(getClass().getResource("/uni/fmi/tourguide/resources/Plovdiv_City_Map.jpg"));

        	// Scale it to desired size
        	Image scaledImage = originalIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        	ImageIcon scaledIcon = new ImageIcon(scaledImage);

        	// Show in a JLabel
        	JLabel mapLabel = new JLabel(scaledIcon);

            mapLabel.setHorizontalAlignment(JLabel.CENTER);
            add(mapLabel, BorderLayout.SOUTH); 

        } else {
            System.err.println("Map image not found!");
        }


        pack();
        setVisible(true);

        // JADE startup
        initJadePlatform();
        MessageReceiverAgent.outputArea = resultArea;

        // Search button logic
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String interest = getSelectedInterest();
                String duration = (String) durationBox.getSelectedItem();
                String mobility = (String) mobilityBox.getSelectedItem();

                if (interest == null) {
                    resultArea.setText("❌ Please select at least one interest.");
                    return;
                }

                String query = interest + "," + duration + "," + mobility;
                resultArea.setText("🔍 Searching for: " + query);

                try {
                    Object[] args = new Object[]{query};
                    AgentController tourist = container.createNewAgent("tourist" + System.currentTimeMillis(),
                            "uni.fmi.tourguide.TouristAgent", args);
                    tourist.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private String getSelectedInterest() {
        if (historyBox.isSelected()) return "History";
        if (artBox.isSelected()) return "Art";
        if (natureBox.isSelected()) return "Nature";
        return null;
    }

    private void initJadePlatform() {
        try {
            Runtime rt = Runtime.instance();
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.GUI, "true");
            profile.setParameter(Profile.LOCAL_PORT, "1210"); // avoid conflict

            container = rt.createMainContainer(profile);

            AgentController guideAgent = container.createNewAgent(
                    "guide", "uni.fmi.tourguide.GuideAgent", null);
            guideAgent.start();
            
            AgentController receiverAgent = container.createNewAgent(
            	    "guiReceiver", "uni.fmi.tourguide.MessageReceiverAgent", null);
            	receiverAgent.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static void main(String[] args) {
        new TourGuideGUI();
    }
}
