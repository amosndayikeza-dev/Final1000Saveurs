package apps.app.utils;


// Import des classes nécessaires pour la communication avec l'imprimante ESC/POS
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.output.TcpIpOutputStream;
import java.io.IOException;

/**
 * Classe utilitaire de configuration pour l'imprimante thermique Xprinter.
 * Centralise les paramètres de connexion (IP, port) et fournit une instance d'EscPos.
 */
public class PrinterConfig {

        // Adresse IP de l'imprimante sur le réseau local (à modifier selon votre matériel)
        private static final String PRINTER_IP = "192.168.1.100";

        // Port TCP standard pour les imprimantes ESC/POS (généralement 9100)
        private static final int PRINTER_PORT = 9100;

        /**
         * Crée et retourne un objet EscPos prêt à envoyer des commandes d'impression.
         * @return EscPos configuré
         * @throws IOException si la connexion à l'imprimante échoue
         */
        public static EscPos getEscPos() throws IOException {
            // Établit une connexion TCP avec l'imprimante
            TcpIpOutputStream  tcpStream  = new TcpIpOutputStream (PRINTER_IP, PRINTER_PORT);
            // Crée l'objet EscPos à partir de cette connexion
            return new EscPos(tcpStream );
        }

}
