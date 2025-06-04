public class Canzone {
    private String artista;
    private String album;
    private String titolo;
    private String testo;

    public Canzone(String artista, String album, String titolo, String testo) {
        this.artista = artista;
        this.album = album;
        this.titolo = titolo;
        this.testo = testo;
    }

    public String getArtista() { return artista; }
    public String getAlbum() { return album; }
    public String getTitolo() { return titolo; }
    public String getTesto() { return testo; }
}


