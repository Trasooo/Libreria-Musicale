import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class LibreriaMusicaleGUI extends JFrame {
    // Colori del tema
    private static final Color BACKGROUND_COLOR = new Color(18, 18, 18);
    private static final Color PANEL_COLOR = new Color(30, 30, 30);
    private static final Color ACCENT_COLOR = new Color(232, 17, 91); // Rosa Spotify
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color SECONDARY_TEXT = new Color(179, 179, 179);
    private static final Color BUTTON_COLOR = new Color(40, 40, 40);
    private static final Color HIGHLIGHT_COLOR = new Color(80, 80, 80);

    private GestoreLibreriaMusicale gestore;
    private JPanel panelPrincipale;
    private JPanel panelInserisci;
    private JPanel panelVisualizza;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public LibreriaMusicaleGUI() {
        gestore = new GestoreLibreriaMusicale("libreria_musicale.txt");
        inizializzaGUI();
    }

    private void inizializzaGUI() {
        setTitle("Libreria Musicale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Imposta il CardLayout per gestire le diverse schermate
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);
        creaPanelPrincipale();
        creaPanelInserisci();
        creaPanelVisualizza();
        mainPanel.add(panelPrincipale, "PRINCIPALE");
        mainPanel.add(panelInserisci, "INSERISCI");
        mainPanel.add(panelVisualizza, "VISUALIZZA");
        add(mainPanel);
        cardLayout.show(mainPanel, "PRINCIPALE");
    }

    private void styleButton(JButton button) {
        button.setBackground(ACCENT_COLOR); // Colore di sfondo contrastante
        button.setForeground(Color.BLACK); // Colore del testo leggibile
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1), // Bordo nero per contrasto
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HIGHLIGHT_COLOR); // Colore di evidenziazione
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR); // Ripristina il colore originale
            }
        });
    }

    private void creaPanelPrincipale() {
        panelPrincipale = new JPanel(new BorderLayout());
        panelPrincipale.setBackground(BACKGROUND_COLOR);
        panelPrincipale.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Titolo
        JLabel titolo = new JLabel("LIBRERIA MUSICALE", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 32));
        titolo.setForeground(ACCENT_COLOR);
        titolo.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));
        panelPrincipale.add(titolo, BorderLayout.NORTH);
        
        // Panel per i pulsanti
        JPanel panelPulsanti = new JPanel(new GridLayout(3, 1, 0, 20));
        panelPulsanti.setBackground(BACKGROUND_COLOR);
        panelPulsanti.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));
        
        // Pulsante Inserisci Canzone
        JButton btnInserisci = new JButton("Inserisci Canzone");
        styleButton(btnInserisci);
        btnInserisci.addActionListener(e -> cardLayout.show(mainPanel, "INSERISCI"));
        
        // Pulsante Visualizza Libreria
        JButton btnVisualizza = new JButton("Visualizza Libreria");
        styleButton(btnVisualizza);
        btnVisualizza.addActionListener(e -> cardLayout.show(mainPanel, "VISUALIZZA"));
        
        // Pulsante Esci (con doppio click)
        JButton btnEsci = new JButton("Esci (Doppio Click)");
        styleButton(btnEsci);
        btnEsci.setBackground(new Color(60, 10, 25));
        btnEsci.setForeground(new Color(255, 150, 180));
        btnEsci.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int risposta = JOptionPane.showConfirmDialog(
                        LibreriaMusicaleGUI.this,
                        "Sei sicuro di voler uscire?",
                        "Conferma uscita",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    if (risposta == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            }
        });
        
        panelPulsanti.add(btnInserisci);
        panelPulsanti.add(btnVisualizza);
        panelPulsanti.add(btnEsci);
        
        panelPrincipale.add(panelPulsanti, BorderLayout.CENTER);
    }

    private void creaPanelInserisci() {
        panelInserisci = new JPanel(new BorderLayout());
        panelInserisci.setBackground(BACKGROUND_COLOR);
        panelInserisci.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Titolo
        JLabel labelTitolo = new JLabel("INSERISCI NUOVA CANZONE", SwingConstants.CENTER);
        labelTitolo.setFont(new Font("Arial", Font.BOLD, 20));
        labelTitolo.setForeground(ACCENT_COLOR);
        labelTitolo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panelInserisci.add(labelTitolo, BorderLayout.NORTH);
        
        // Panel principale per il form
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(PANEL_COLOR);
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Stile per le etichette
        JLabel label;
        
        // Campo Artista
        gbc.gridx = 0; gbc.gridy = 0;
        label = new JLabel("Artista:");
        label.setForeground(TEXT_COLOR);
        panelForm.add(label, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField txtArtista = new JTextField(30);
        styleTextField(txtArtista);
        panelForm.add(txtArtista, gbc);
        
        // Campo Titolo
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        label = new JLabel("Titolo:");
        label.setForeground(TEXT_COLOR);
        panelForm.add(label, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField txtTitolo = new JTextField(30);
        styleTextField(txtTitolo);
        panelForm.add(txtTitolo, gbc);
        
        // Sezione Tipo di Canzone
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        label = new JLabel("Tipo:");
        label.setForeground(TEXT_COLOR);
        panelForm.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JPanel panelTipo = new JPanel();
        panelTipo.setBackground(PANEL_COLOR);
        panelTipo.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        
        ButtonGroup gruppoTipo = new ButtonGroup();
        JRadioButton radioAlbum = new JRadioButton("Fa parte di un Album", true);
        styleRadioButton(radioAlbum);
        JRadioButton radioSingolo = new JRadioButton("Singolo");
        styleRadioButton(radioSingolo);
        gruppoTipo.add(radioAlbum);
        gruppoTipo.add(radioSingolo);
        
        panelTipo.add(radioAlbum);
        panelTipo.add(radioSingolo);
        panelForm.add(panelTipo, gbc);
        
        // Campo Album
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1;
        label = new JLabel("Album:");
        label.setForeground(TEXT_COLOR);
        panelForm.add(label, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField txtAlbum = new JTextField(30);
        styleTextField(txtAlbum);
        panelForm.add(txtAlbum, gbc);
        
        // Pulsante per cercare il testo
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnCercaTesto = new JButton("Cerca Testo Online");
        styleButton(btnCercaTesto);
        panelForm.add(btnCercaTesto, gbc);
        
        // Campo Testo
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        label = new JLabel("Testo:");
        label.setForeground(TEXT_COLOR);
        panelForm.add(label, gbc);
        
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JTextArea txtTesto = new JTextArea(10, 30);
        txtTesto.setLineWrap(true);
        txtTesto.setWrapStyleWord(true);
        txtTesto.setBackground(BUTTON_COLOR);
        txtTesto.setForeground(TEXT_COLOR);
        txtTesto.setCaretColor(ACCENT_COLOR);
        txtTesto.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollTesto = new JScrollPane(txtTesto);
        scrollTesto.setBackground(PANEL_COLOR);
        panelForm.add(scrollTesto, gbc);
        
        // Logica per abilitare/disabilitare il campo Album
        radioSingolo.addActionListener(e -> {
            txtAlbum.setEnabled(false);
            txtAlbum.setText("Singolo");
            txtAlbum.setBackground(new Color(50, 50, 50));
        });
        
        radioAlbum.addActionListener(e -> {
            txtAlbum.setEnabled(true);
            txtAlbum.setText("");
            txtAlbum.setBackground(BUTTON_COLOR);
        });
        
        // Azione per il pulsante di ricerca testo
        btnCercaTesto.addActionListener(e -> {
            String artista = txtArtista.getText().trim();
            String titoloCanzone = txtTitolo.getText().trim();
            
            if (artista.isEmpty() || titoloCanzone.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Inserisci prima artista e titolo per cercare il testo!", 
                    "Campi mancanti", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Mostra un messaggio di caricamento
            btnCercaTesto.setText("Cercando...");
            btnCercaTesto.setEnabled(false);
            
            // Esegui la ricerca in un thread separato per non bloccare l'interfaccia
            SwingUtilities.invokeLater(() -> {
                try {
                    String testoTrovato = cercaTestoOnline(artista, titoloCanzone);
                    if (testoTrovato != null && !testoTrovato.isEmpty()) {
                        txtTesto.setText(testoTrovato);
                        JOptionPane.showMessageDialog(this, 
                            "Testo trovato e inserito automaticamente!", 
                            "Successo", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Testo non trovato. Inseriscilo manualmente.", 
                            "Testo non trovato", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Errore durante la ricerca del testo: " + ex.getMessage(), 
                        "Errore", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnCercaTesto.setText("Cerca Testo Online");
                    btnCercaTesto.setEnabled(true);
                }
            });
        });
        
        panelInserisci.add(panelForm, BorderLayout.CENTER);
        
        // Panel per i pulsanti
        JPanel panelPulsantiInserisci = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panelPulsantiInserisci.setBackground(BACKGROUND_COLOR);
        
        JButton btnSalva = new JButton("Salva Canzone");
        styleButton(btnSalva);
        btnSalva.setBackground(ACCENT_COLOR);
        btnSalva.setForeground(Color.BLACK);
        btnSalva.addActionListener(e -> {
            try {
                String artista = txtArtista.getText().trim();
                String album = txtAlbum.getText().trim();
                String titoloCanzone = txtTitolo.getText().trim();
                String testo = txtTesto.getText().trim();
                if (artista.isEmpty() || titoloCanzone.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Artista e Titolo sono obbligatori!",
                                                "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Se è selezionato "Singolo" e il campo album è vuoto o contiene "Singolo"
                if (radioSingolo.isSelected()) {
                    album = "Singolo";
                } else if (album.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Il campo Album è obbligatorio quando la canzone fa parte di un album!",
                                                "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                gestore.inserisciCanzone(artista, album, titoloCanzone, testo);
                JOptionPane.showMessageDialog(this, "Canzone salvata con successo!");
                
                // Pulisci i campi
                txtArtista.setText("");
                txtAlbum.setText("");
                txtTitolo.setText("");
                txtTesto.setText("");
                radioAlbum.setSelected(true);
                txtAlbum.setEnabled(true);
                txtAlbum.setBackground(BUTTON_COLOR);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Errore nel salvare la canzone: " + ex.getMessage(),
                                            "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnIndietro = new JButton("Indietro (Doppio Click)");
        styleButton(btnIndietro);
        btnIndietro.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    cardLayout.show(mainPanel, "PRINCIPALE");
                }
            }
        });
        
        panelPulsantiInserisci.add(btnSalva);
        panelPulsantiInserisci.add(btnIndietro);
        panelInserisci.add(panelPulsantiInserisci, BorderLayout.SOUTH);
    }
    
    private void styleTextField(JTextField textField) {
        textField.setBackground(BUTTON_COLOR);
        textField.setForeground(TEXT_COLOR);
        textField.setCaretColor(ACCENT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 35));
    }
    
    private void styleRadioButton(JRadioButton radio) {
        radio.setBackground(PANEL_COLOR);
        radio.setForeground(TEXT_COLOR);
        radio.setFocusPainted(false);
        radio.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void creaPanelVisualizza() {
        panelVisualizza = new JPanel(new BorderLayout());
        panelVisualizza.setBackground(BACKGROUND_COLOR);
        panelVisualizza.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Titolo
        JLabel labelTitolo = new JLabel("VISUALIZZA LIBRERIA", SwingConstants.CENTER);
        labelTitolo.setFont(new Font("Arial", Font.BOLD, 20));
        labelTitolo.setForeground(ACCENT_COLOR);
        labelTitolo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panelVisualizza.add(labelTitolo, BorderLayout.NORTH);
        
        // Panel per i pulsanti di scelta
        JPanel panelScelte = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelScelte.setBackground(BACKGROUND_COLOR);
        
        JButton btnCerca = new JButton("Cerca Canzone");
        styleButton(btnCerca);
        
        JButton btnTutte = new JButton("Mostra Tutte");
        styleButton(btnTutte);
        
        panelScelte.add(btnCerca);
        panelScelte.add(btnTutte);
        
        // Crea l'albero per la visualizzazione
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Libreria Musicale");
        JTree tree = new JTree(root);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Configura il renderer personalizzato per i colori
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setBackgroundSelectionColor(ACCENT_COLOR);
        renderer.setTextSelectionColor(Color.BLACK);
        renderer.setBackgroundNonSelectionColor(BUTTON_COLOR);
        renderer.setTextNonSelectionColor(TEXT_COLOR);
        tree.setCellRenderer(renderer);
        tree.setRowHeight(25);
        
        JScrollPane scrollTree = new JScrollPane(tree);
        scrollTree.setBackground(BACKGROUND_COLOR);
        scrollTree.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
        
        // Aggiungi listener per il doppio click sull'albero
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    if (selPath != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                        if (node instanceof CanzoneTreeNode) {
                            Canzone canzone = ((CanzoneTreeNode) node).getCanzone();
                            mostraTestoCanzone(canzone);
                        }
                    }
                }
            }
        });
        
        // Panel centrale
        JPanel panelCentrale = new JPanel(new BorderLayout());
        panelCentrale.setBackground(BACKGROUND_COLOR);
        panelCentrale.add(panelScelte, BorderLayout.NORTH);
        panelCentrale.add(scrollTree, BorderLayout.CENTER);
        panelVisualizza.add(panelCentrale, BorderLayout.CENTER);
        
        // Pulsante Indietro
        JPanel panelIndietro = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelIndietro.setBackground(BACKGROUND_COLOR);
        JButton btnIndietro = new JButton("Indietro (Doppio Click)");
        styleButton(btnIndietro);
        btnIndietro.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    cardLayout.show(mainPanel, "PRINCIPALE");
                }
            }
        });
        panelIndietro.add(btnIndietro);
        panelVisualizza.add(panelIndietro, BorderLayout.SOUTH);
        
        // Gestione eventi per ricerca e visualizzazione
        btnCerca.addActionListener(e -> {
            String criterio = JOptionPane.showInputDialog(this,
                "Inserisci il criterio di ricerca (titolo, album o artista):",
                "Cerca Canzone",
                JOptionPane.QUESTION_MESSAGE);
            
            if (criterio != null && !criterio.trim().isEmpty()) {
                try {
                    List<Canzone> risultati = gestore.cercaCanzone(criterio.trim());
                    mostraRisultatiNellAlbero(risultati, tree, "Risultati ricerca per: " + criterio);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Errore nella ricerca: " + ex.getMessage(),
                                                "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnTutte.addActionListener(e -> {
            try {
                List<Canzone> tutteLeCanzoni = gestore.visualizzaLibreria();
                mostraRisultatiNellAlbero(tutteLeCanzoni, tree, "Tutte le canzoni nella libreria");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Errore nel caricare la libreria: " + ex.getMessage(),
                                            "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    // Classe interna per i nodi dell'albero
    private static class CanzoneTreeNode extends DefaultMutableTreeNode {
        private final Canzone canzone;
        
        public CanzoneTreeNode(String displayText, Canzone canzone) {
            super(displayText);
            this.canzone = canzone;
        }
        
        public Canzone getCanzone() {
            return canzone;
        }
    }
    
    // Metodo per mostrare il testo della canzone
    private void mostraTestoCanzone(Canzone canzone) {
        JDialog dialog = new JDialog(this, "Testo Canzone", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Intestazione con titolo, artista e album
        JLabel header = new JLabel(
            "<html><h2 style='color:#" + Integer.toHexString(ACCENT_COLOR.getRGB()).substring(2) + 
            ";'>" + canzone.getTitolo() + "</h2>" +
            "<p><strong style='color:#" + Integer.toHexString(TEXT_COLOR.getRGB()).substring(2) + 
            ";'>Artista:</strong> " + canzone.getArtista() + "</p>" +
            "<p><strong style='color:#" + Integer.toHexString(TEXT_COLOR.getRGB()).substring(2) + 
            ";'>Album:</strong> " + canzone.getAlbum() + "</p></html>"
        );
        panel.add(header, BorderLayout.NORTH);
        
        // Area di testo per il testo della canzone
        JTextArea textArea = new JTextArea(canzone.getTesto());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(BUTTON_COLOR);
        textArea.setForeground(TEXT_COLOR);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Pulsante per chiudere
        JButton closeButton = new JButton("Chiudi");
        styleButton(closeButton);
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void mostraRisultatiNellAlbero(List<Canzone> canzoni, JTree tree, String titoloRoot) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(titoloRoot);
        DefaultTreeModel model = new DefaultTreeModel(root);
        
        if (canzoni.isEmpty()) {
            root.add(new DefaultMutableTreeNode("Nessuna canzone trovata"));
        } else {
            java.util.Map<String, DefaultMutableTreeNode> artistiNodes = new java.util.HashMap<>();
            
            for (Canzone canzone : canzoni) {
                String artista = canzone.getArtista();
                
                // Crea il nodo artista se non esiste
                DefaultMutableTreeNode artistaNode = artistiNodes.get(artista);
                if (artistaNode == null) {
                    artistaNode = new DefaultMutableTreeNode(artista);
                    artistiNodes.put(artista, artistaNode);
                    root.add(artistaNode);
                }
                
                // Crea il nodo canzone con l'oggetto Canzone
                CanzoneTreeNode canzoneNode = new CanzoneTreeNode(
                    canzone.getTitolo() + " (" + canzone.getAlbum() + ")", 
                    canzone
                );
                artistaNode.add(canzoneNode);
            }
        }
        
        tree.setModel(model);
        
        // Espandi tutti i nodi
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private String cercaTestoOnline(String artista, String titolo) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            String url = "https://api.lyrics.ovh/v1/" + URLEncoder.encode(artista, "UTF-8") + "/" + URLEncoder.encode(titolo, "UTF-8");
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            
            // Controlla se la risposta è valida
            if (response.getStatusLine().getStatusCode() == 200) {
                // L'API restituisce un JSON con il campo "lyrics"
                JSONObject jsonResponse = new JSONObject(responseString);
                if (jsonResponse.has("lyrics")) {
                    String lyrics = jsonResponse.getString("lyrics");
                    // Rimuovi eventuali caratteri di escape o formattazioni indesiderate
                    return lyrics.trim();
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("Errore durante la ricerca del testo: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.put("OptionPane.background", BACKGROUND_COLOR);
                UIManager.put("Panel.background", BACKGROUND_COLOR);
                UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
            } catch (Exception e) {
                // Usa il look and feel predefinito
            }
            
            new LibreriaMusicaleGUI().setVisible(true);
        });
    }
}
