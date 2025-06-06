package uni.fmi.tourguide;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

public class TourGuideGUI extends JFrame {
    private JList<String> interestList = new JList<>(new String[]{
        "Art", "Architecture", "Culture", "Family", "Food",
        "History", "Music", "Nature", "Nightlife", "Religion",
        "Shopping", "Sport"
    });

    private JTextField maxDurationField = new JTextField(5);
    private JComboBox<String> mobilityBox = new JComboBox<>(new String[]{
        "Walking", "Car", "Accessible", "Bicycle", "PublicTransport"
    });

    private JTextArea resultArea = new JTextArea(10, 40);
    private JTextField feedbackLocationField = new JTextField(20);
    private JComboBox<Integer> ratingBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
    private JTextArea commentArea = new JTextArea(3, 20);

    private DatabaseManager dbManager = new DatabaseManager();

    public TourGuideGUI() {
        setTitle("🗺️ Plovdiv Tour Guide");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

      
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Filter Your Tour", TitledBorder.LEFT, TitledBorder.TOP));

        interestList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane interestScroll = new JScrollPane(interestList);
        interestScroll.setPreferredSize(new Dimension(200, 100));
        filterPanel.add(new JLabel("Select Interests:"));
        filterPanel.add(interestScroll);
        filterPanel.add(Box.createVerticalStrut(10));

        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        durationPanel.add(new JLabel("Max Duration (min):"));
        durationPanel.add(maxDurationField);
        filterPanel.add(durationPanel);
        filterPanel.add(Box.createVerticalStrut(10));

        JPanel mobilityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mobilityPanel.add(new JLabel("Mobility Option:"));
        mobilityPanel.add(mobilityBox);
        filterPanel.add(mobilityPanel);
        filterPanel.add(Box.createVerticalStrut(10));

        JButton findButton = new JButton("🔍 Find Tour");
        findButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterPanel.add(findButton);

        add(filterPanel, BorderLayout.WEST);

       
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane resultScroll = new JScrollPane(resultArea);

        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Recommended Locations", TitledBorder.LEFT, TitledBorder.TOP));
        resultsPanel.add(resultScroll, BorderLayout.CENTER);

        add(resultsPanel, BorderLayout.CENTER);

        // ==== Feedback Panel ====
        JPanel feedbackPanel = new JPanel();
        feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));
        feedbackPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Rate a Location", TitledBorder.LEFT, TitledBorder.TOP));

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameRow.add(new JLabel("Location Name:"));
        nameRow.add(feedbackLocationField);
        feedbackPanel.add(nameRow);

        JPanel ratingRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingRow.add(new JLabel("Rating (1-5):"));
        ratingRow.add(ratingBox);
        feedbackPanel.add(ratingRow);

        JPanel commentRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commentRow.add(new JLabel("Comment:"));
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentRow.add(new JScrollPane(commentArea));
        feedbackPanel.add(commentRow);

        JPanel feedbackButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitFeedbackBtn = new JButton("💾 Submit Feedback");
        JButton viewFeedbackBtn = new JButton("📋 View All Feedback");
        feedbackButtons.add(submitFeedbackBtn);
        feedbackButtons.add(viewFeedbackBtn);
        feedbackPanel.add(feedbackButtons);

        add(feedbackPanel, BorderLayout.SOUTH);

       
        findButton.addActionListener((ActionEvent e) -> {
            List<String> interests = interestList.getSelectedValuesList();
            int maxDuration = getMaxDuration();
            String mobility = (String) mobilityBox.getSelectedItem();

            PlovdivOntology ontology = new PlovdivOntology("files/plovdiv.owl");

            List<String> results = new ArrayList<>();
            for (String interest : interests) {
                results.addAll(ontology.getTourLocations(interest, maxDuration, mobility));
            }

            resultArea.setText(results.isEmpty() ? " No matching locations found." : String.join("\n", results));
        });

        submitFeedbackBtn.addActionListener((ActionEvent e) -> {
            String location = feedbackLocationField.getText().trim();
            int rating = (int) ratingBox.getSelectedItem();
            String comment = commentArea.getText().trim();

            if (!location.isEmpty()) {
                dbManager.saveFeedback(location, rating, comment);
                JOptionPane.showMessageDialog(null, "Feedback saved!");
                feedbackLocationField.setText("");
                commentArea.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Please enter a location name.");
            }
        });

        viewFeedbackBtn.addActionListener((ActionEvent e) -> {
            JTextArea feedbackText = new JTextArea(15, 40);
            feedbackText.setEditable(false);
            feedbackText.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(feedbackText);
            String allFeedback = dbManager.getAllFeedbackAsString();
            feedbackText.setText(allFeedback.isEmpty() ? "No feedback available." : allFeedback);
            JOptionPane.showMessageDialog(null, scrollPane, "All Feedback", JOptionPane.INFORMATION_MESSAGE);
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private int getMaxDuration() {
        try {
            return Integer.parseInt(maxDurationField.getText());
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TourGuideGUI());
    }
}