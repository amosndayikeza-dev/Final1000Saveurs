package apps.app.utils;

public class TestBCrypt {
    public static void main(String[] args) {
        try {
            Class.forName("org.mindrot.jbcrypt.BCrypt");
            System.out.println("OK");
        } catch (ClassNotFoundException e) {
            System.out.println("JAR manquant");
        }
    }
}