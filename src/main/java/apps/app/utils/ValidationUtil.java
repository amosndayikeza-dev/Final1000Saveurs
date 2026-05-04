package apps.app.utils;

public class ValidationUtil {


        public static boolean isValidAdjustment(int adjustment, int currentStock) {
            return currentStock + adjustment >= 0;
        }
        // Autres helpers (validation email, etc.)

}
