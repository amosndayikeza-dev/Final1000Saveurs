package apps.app.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

public class PasswordUtil {

    /**
     * Hache un mot de passe en clair avec SHA-256.
     * @param password le mot de passe en clair
     * @return la chaîne hexadécimale du haché (64 caractères)
     * @throws RuntimeException si l'algorithme SHA-256 n'est pas disponible (cas rare)
     */

    public static String hash(String password){
        try{
            // 1. Obtenir une instance de MessageDigest avec l'algorithme SHA-256

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            //convertir le mot de passe en tableaux d'octs
            byte[] passwordBytes = password.getBytes("UTF-8");

            //Calculer le hashe (digets) du mot de passe
            byte[] hashBytes = md.digest(passwordBytes);

            //convertir le tableax d'octes en chaine hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (Byte b : hashBytes){
                // Convertir chaque octet en deux caractères hexadécimaux
                String hex = String.format("%02x", b);
                hexString.append(hex);

            }

            return hexString.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Vérifie qu'un mot de passe en clair correspond à un haché stocké.
     * @param plainPassword le mot de passe saisi par l'utilisateur
     * @param hashedPassword le haché stocké en base de données
     * @return true si le mot de passe correspond, false sinon
     */

    public static boolean check(String plainPassword,String hashedPassword){
        String hashOfPlain = hash(plainPassword);

        return hashOfPlain.equals(hashedPassword);
    }

}
