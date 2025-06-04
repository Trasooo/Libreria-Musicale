import java.io.IOException;
import java.util.List;
public interface LibreriaMusicale {
   void inserisciCanzone(String artista, String album, String titolo, String testo) throws IOException;
   List<Canzone> visualizzaLibreria() throws IOException;
   List<Canzone> cercaCanzone(String criterio) throws IOException;
}


