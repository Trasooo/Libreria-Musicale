import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestoreLibreriaMusicale implements LibreriaMusicale {
	private final String filePath;

	public GestoreLibreriaMusicale(String filePath) {
		this.filePath = filePath;
		creaFileSeNonEsiste();
	}

	private void creaFileSeNonEsiste() {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			System.err.println("Errore durante la creazione del file: " + e.getMessage());
		}
	}// In GestoreLibreriaMusicale.java

	@Override
	public void inserisciCanzone(String artista, String album, String titolo, String testo) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
			// Normalize all line endings to \n, then escape
			String testoNormalizzato = testo.replace("\r\n", "\n").replace("\r", "\n");
			String testoSalvataggio = testoNormalizzato.replace("\n", "\\n");
			writer.write(artista + "|" + album + "|" + titolo + "|" + testoSalvataggio);
			writer.newLine();
		}
	}

	@Override
	public List<Canzone> visualizzaLibreria() throws IOException {
		List<Canzone> canzoni = new ArrayList<>();
		File file = new File(filePath);

		if (file.length() == 0) {
			return canzoni; // Lista vuota
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\\|", 4);
				if (parts.length == 4) {
					String testoCanzone = parts[3].replace("\\n", "\n");
					Canzone canzone = new Canzone(parts[0], parts[1], parts[2], testoCanzone);
					canzoni.add(canzone);
				}
			}
		}
		return canzoni;
	}

	@Override
	public List<Canzone> cercaCanzone(String criterio) throws IOException {
		List<Canzone> tutteLeCanzoni = visualizzaLibreria();
		List<Canzone> risultati = new ArrayList<>();

		String criterioLower = criterio.toLowerCase();

		for (Canzone canzone : tutteLeCanzoni) {
			if (canzone.getTitolo().toLowerCase().contains(criterioLower)
					|| canzone.getAlbum().toLowerCase().contains(criterioLower)
					|| canzone.getArtista().toLowerCase().contains(criterioLower)) {
				risultati.add(canzone);
			}
		}

		return risultati;
	}
}
