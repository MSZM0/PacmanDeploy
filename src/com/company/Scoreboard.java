package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class Scoreboard extends JPanel {
    private JButton backButton;
    private Vector<String> scores;

    public Scoreboard(ActionListener listener) {
        setLayout(null);
        setBackground(new Color(0, 0, 0));
        setPreferredSize(new Dimension(GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE,
            GameConstants.BOARD_HEIGHT * GameConstants.TILE_SIZE * GameConstants.SCALE));

        JLabel titleLabel = new JLabel("SCOREBOARD");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 30, GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE, 60);
        add(titleLabel);


        scores = loadScores();
        JTextArea scoreArea = new JTextArea();
        scoreArea.setEditable(false);
        scoreArea.setFont(new Font("Courier New", Font.PLAIN, 16));
        scoreArea.setBackground(new Color(30, 30, 30));
        scoreArea.setForeground(Color.CYAN);


        StringBuilder scoresText = new StringBuilder();
        scoresText.append("   RANK    SCORE\n");
        scoresText.append("   ----    -----\n");
        for (int i = 0; i < scores.size(); i++) {
            scoresText.append(String.format("    %d.    %s\n", i + 1, scores.get(i)));
        }
        if (scores.isEmpty()) {
            scoresText.append("   No scores yet!\n");
        }
        scoreArea.setText(scoresText.toString());

        JScrollPane scrollPane = new JScrollPane(scoreArea);
        scrollPane.setBounds(50, 130, GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE - 100, 300);
        scrollPane.setBackground(new Color(30, 30, 30));
        add(scrollPane);

        backButton = new JButton("Back");
        backButton.setActionCommand("BACK");
        backButton.setFont(new Font("Arial", Font.PLAIN, 18));
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(100, 100, 100));
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(GameConstants.BOARD_WIDTH * GameConstants.TILE_SIZE * GameConstants.SCALE / 2 - 75, 450, 150, 40);
        backButton.addActionListener(listener);
        add(backButton);
    }

    private Vector<String> loadScores() {
        Vector<String> scoresList = new Vector<>();
        File scoreFile = new File("scores.txt");

        if (scoreFile.exists()) {
            try (Scanner scanner = new Scanner(scoreFile)) {
                while (scanner.hasNextLine()) {
                    scoresList.add(scanner.nextLine());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        scoresList.sort((a, b) -> {
            try {
                int scoreA = Integer.parseInt(a.replaceAll("[^0-9]", ""));
                int scoreB = Integer.parseInt(b.replaceAll("[^0-9]", ""));
                return scoreB - scoreA;
            } catch (NumberFormatException e) {
                return 0;
            }
        });

        if (scoresList.size() > 10) {
            scoresList.setSize(10);
        }

        return scoresList;
    }

    public static void saveScore(String scoreEntry) {
        try (FileWriter fw = new FileWriter("scores.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(scoreEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(200, 0, 0));
        g.fillRect(0, 0, getWidth(), 5);
        g.setColor(new Color(255, 200, 0));
        g.fillRect(0, 5, getWidth(), 5);
        g.setColor(new Color(0, 200, 0));
        g.fillRect(0, 10, getWidth(), 5);
        g.setColor(new Color(0, 0, 200));
        g.fillRect(0, 15, getWidth(), 5);
    }
}
