package uni.fmi.tourguide;

public class TestDatabase {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();

      
        //db.saveFeedback("Ancient Theater", 5, "Amazing location!");
        db.printAllFeedback();
    }
}
