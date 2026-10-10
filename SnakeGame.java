import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Snake Arena - a complete Java Swing mini-project.
 * Run from the project root:
 *   javac SnakeGame.java
 *   java SnakeGame
 */
public class SnakeGame extends JFrame {
    private static final Color BG = new Color(5, 18, 30);
    private static final Color NEON = new Color(57, 255, 135);
    private static final Color CYAN = new Color(25, 235, 190);
    private static final Color WHITE = new Color(240, 255, 248);
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final MenuScreen menu = new MenuScreen();
    private final HelpScreen help = new HelpScreen();
    private final GameScreen game = new GameScreen();
    private final OverScreen over = new OverScreen();

    private boolean soundOn = true;
    private int bestScore = 0;
    private int finalScore = 0;

    public SnakeGame() {
        super("Snake Arena - Java Edition");
        root.add(menu, "MENU");
        root.add(help, "HELP");
        root.add(game, "GAME");
        root.add(over, "OVER");
        setContentPane(root);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        cards.show(root, "MENU");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setForeground(WHITE);
        b.setBackground(new Color(15, 112, 72));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(45, 240, 150), 2));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(235, 44));
        b.setMaximumSize(new Dimension(235, 44));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(25, 155, 92)); }
            public void mouseExited(MouseEvent e) { b.setBackground(new Color(15, 112, 72)); }
        });
        return b;
    }

    private JLabel makeLabel(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void beep() {
        if (soundOn) Toolkit.getDefaultToolkit().beep();
    }

    private void startGame() {
        game.resetGame();
        cards.show(root, "GAME");
        game.requestFocusInWindow();
    }

    private void finishGame() {
        finalScore = game.score;
        bestScore = Math.max(bestScore, finalScore);
        game.stopGame();
        over.updateScreen();
        cards.show(root, "OVER");
    }

    private BufferedImage loadAsset(String name) {
        String[] paths = {"assets/" + name, "SnakeArena_Java_Project/assets/" + name};
        for (String path : paths) {
            try {
                File f = new File(path);
                if (f.exists()) return ImageIO.read(f);
            } catch (Exception ignored) { }
        }
        return null;
    }

    // Decorative jungle background, with optional background image asset.
    private class JunglePanel extends JPanel {
        JunglePanel() { setBackground(BG); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, new Color(4, 17, 34),
                    w, h, new Color(0, 53, 47)));
            g2.fillRect(0, 0, w, h);

            BufferedImage bgImage = loadAsset("jungle-background.png");
            if (bgImage != null) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f));
                g2.drawImage(bgImage, 0, 0, w, h, null);
                g2.setComposite(AlphaComposite.SrcOver);
            }

            g2.setColor(new Color(5, 42, 39));
            int[] tx = {0, 65, 120, 190, 260, 335, 420, 500, 590, 680, 780};
            int[] ty = {h, h - 115, h, h - 155, h, h - 125, h, h - 170, h, h - 115, h};
            g2.fillPolygon(tx, ty, tx.length);

            g2.setColor(new Color(13, 94, 52));
            for (int i = 0; i < 14; i++) {
                int x = (i * 79) % Math.max(w, 1);
                int y = (i * 53) % Math.max(h, 1);
                g2.fillOval(x - 20, y - 12, 52, 19);
                g2.fillOval(x, y, 35, 13);
            }
            g2.setColor(new Color(100, 255, 160, 150));
            for (int i = 0; i < 35; i++) {
                int x = (i * 137 + 17) % Math.max(w, 1);
                int y = (i * 71 + 23) % Math.max(h, 1);
                g2.fillOval(x, y, 3, 3);
            }
            g2.dispose();
        }
    }

    private class MenuScreen extends JunglePanel {
        private final JLabel bestLabel = makeLabel("", 17, WHITE);
        private final JButton soundButton = makeButton("SOUND: ON");

        MenuScreen() {
            setPreferredSize(new Dimension(780, 620));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(38, 20, 25, 20));
            add(makeLabel("SNAKE ARENA", 46, NEON));
            add(Box.createVerticalStrut(5));
            add(makeLabel("JAVA ARCADE EDITION", 15, CYAN));
            add(Box.createVerticalStrut(12));
            add(makeLabel("EAT  •  GROW  •  SURVIVE", 14, WHITE));
            add(Box.createVerticalStrut(10));

            JLabel art = makeLabel("S N A K E", 30, NEON);
            BufferedImage banner = loadAsset("snake-banner.png");
            if (banner != null) {
                art.setText("");
                art.setIcon(new ImageIcon(banner.getScaledInstance(300, 95, Image.SCALE_SMOOTH)));
            }
            add(art);
            add(Box.createVerticalStrut(8));
            add(bestLabel);
            add(Box.createVerticalStrut(22));

            JButton play = makeButton("▶   PLAY GAME");
            JButton instructions = makeButton("HOW TO PLAY");
            JButton exit = makeButton("EXIT GAME");
            for (JButton b : new JButton[]{play, instructions, soundButton, exit}) {
                b.setAlignmentX(Component.CENTER_ALIGNMENT);
                add(b);
                add(Box.createVerticalStrut(9));
            }
            play.addActionListener(e -> startGame());
            instructions.addActionListener(e -> cards.show(root, "HELP"));
            soundButton.addActionListener(e -> {
                soundOn = !soundOn;
                soundButton.setText(soundOn ? "SOUND: ON" : "SOUND: OFF");
            });
            exit.addActionListener(e -> System.exit(0));
        }

        @Override public void paint(Graphics g) {
            super.paint(g);
            bestLabel.setText("BEST SCORE THIS SESSION: " + bestScore);
        }
    }

    private class HelpScreen extends JunglePanel {
        HelpScreen() {
            setPreferredSize(new Dimension(780, 620));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(62, 50, 35, 50));
            add(makeLabel("HOW TO PLAY", 36, NEON));
            add(Box.createVerticalStrut(34));
            String[] lines = {
                "Use the ARROW KEYS to move the snake.",
                "Eat red apples to earn 10 points.",
                "Your snake grows after eating food.",
                "Avoid the walls and your own body.",
                "Press P or SPACE to pause / resume.",
                "Try to beat your best score!"
            };
            for (String line : lines) {
                JLabel item = new JLabel("  •  " + line);
                item.setFont(new Font("SansSerif", Font.PLAIN, 18));
                item.setForeground(WHITE);
                item.setAlignmentX(Component.CENTER_ALIGNMENT);
                add(item);
                add(Box.createVerticalStrut(22));
            }
            add(Box.createVerticalStrut(8));
            JButton back = makeButton("←   BACK TO MENU");
            back.setAlignmentX(Component.CENTER_ALIGNMENT);
            back.addActionListener(e -> cards.show(root, "MENU"));
            add(back);
        }
    }

    private class GameScreen extends JPanel {
        private static final int TILE = 24, COLS = 25, ROWS = 19;
        private static final int BOARD_W = COLS * TILE, BOARD_H = ROWS * TILE, HEADER = 68;
        private final ArrayList<Point> snake = new ArrayList<>();
        private final Random random = new Random();
        private Point food;
        private char direction = 'R', nextDirection = 'R';
        private boolean running, paused, directionChanged, ended;
        private int score;
        private final JLabel scoreLabel = new JLabel("SCORE: 0");
        private final JLabel bestLabel = new JLabel("BEST: 0");
        private final JButton pauseButton = makeButton("Ⅱ PAUSE");
        private final JButton homeButton = makeButton("⌂ MENU");
        private final Timer timer;

        GameScreen() {
            setPreferredSize(new Dimension(BOARD_W, BOARD_H + HEADER));
            setBackground(BG);
            setLayout(null);
            setFocusable(true);
            scoreLabel.setBounds(12, 14, 145, 35);
            scoreLabel.setForeground(NEON);
            scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
            bestLabel.setBounds(155, 14, 125, 35);
            bestLabel.setForeground(WHITE);
            bestLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
            pauseButton.setBounds(BOARD_W - 250, 12, 125, 42);
            homeButton.setBounds(BOARD_W - 120, 12, 110, 42);
            pauseButton.setFont(new Font("SansSerif", Font.BOLD, 12));
            homeButton.setFont(new Font("SansSerif", Font.BOLD, 12));
            add(scoreLabel); add(bestLabel); add(pauseButton); add(homeButton);
            pauseButton.addActionListener(e -> togglePause());
            homeButton.addActionListener(e -> { stopGame(); cards.show(root, "MENU"); });
            timer = new Timer(125, e -> moveSnake());
            setupKeys();
        }

        private void setupKeys() {
            addDirectionKey("UP", KeyEvent.VK_UP, 'U');
            addDirectionKey("DOWN", KeyEvent.VK_DOWN, 'D');
            addDirectionKey("LEFT", KeyEvent.VK_LEFT, 'L');
            addDirectionKey("RIGHT", KeyEvent.VK_RIGHT, 'R');
            addActionKey("PAUSE_P", KeyEvent.VK_P, this::togglePause);
            addActionKey("PAUSE_SPACE", KeyEvent.VK_SPACE, this::togglePause);
        }

        private void addDirectionKey(String name, int key, char dir) {
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0), name);
            getActionMap().put(name, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (!running || paused || directionChanged) return;
                    if (direction == 'U' && dir == 'D') return;
                    if (direction == 'D' && dir == 'U') return;
                    if (direction == 'L' && dir == 'R') return;
                    if (direction == 'R' && dir == 'L') return;
                    nextDirection = dir;
                    directionChanged = true;
                }
            });
        }

        private void addActionKey(String name, int key, Runnable action) {
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0), name);
            getActionMap().put(name, new AbstractAction() {
                public void actionPerformed(ActionEvent e) { action.run(); }
            });
        }

        void resetGame() {
            timer.stop();
            snake.clear();
            snake.add(new Point(8, 8));
            snake.add(new Point(7, 8));
            snake.add(new Point(6, 8));
            direction = nextDirection = 'R';
            directionChanged = false;
            score = 0;
            running = true;
            paused = false;
            ended = false;
            scoreLabel.setText("SCORE: 0");
            bestLabel.setText("BEST: " + bestScore);
            pauseButton.setText("Ⅱ PAUSE");
            placeFood();
            timer.start();
            repaint();
            requestFocusInWindow();
        }

        void stopGame() { running = false; timer.stop(); }

        private void togglePause() {
            if (!running || ended) return;
            paused = !paused;
            if (paused) {
                timer.stop();
                pauseButton.setText("▶ RESUME");
            } else {
                timer.start();
                pauseButton.setText("Ⅱ PAUSE");
            }
            repaint();
            requestFocusInWindow();
        }

        private void placeFood() {
            if (snake.size() >= COLS * ROWS) {
                finishGame();
                return;
            }
            do {
                food = new Point(random.nextInt(COLS), random.nextInt(ROWS));
            } while (snake.contains(food));
        }

        private void moveSnake() {
            if (!running || paused || ended) return;
            direction = nextDirection;
            directionChanged = false;
            Point head = new Point(snake.get(0));
            switch (direction) {
                case 'U': head.y--; break;
                case 'D': head.y++; break;
                case 'L': head.x--; break;
                case 'R': head.x++; break;
            }
            if (head.x < 0 || head.x >= COLS || head.y < 0 || head.y >= ROWS) {
                finishGame(); return;
            }
            boolean eating = head.equals(food);
            int limit = eating ? snake.size() : snake.size() - 1;
            for (int i = 0; i < limit; i++) {
                if (head.equals(snake.get(i))) { finishGame(); return; }
            }
            snake.add(0, head);
            if (eating) {
                score += 10;
                scoreLabel.setText("SCORE: " + score);
                beep();
                placeFood();
                if (!running) return;
            } else {
                snake.remove(snake.size() - 1);
            }
            repaint();
        }

        private void finishGame() {
            if (ended) return;
            ended = true;
            stopGame();
            SnakeGame.this.finishGame();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(2, 15, 26));
            g2.fillRect(0, HEADER, BOARD_W, BOARD_H);

            g2.setColor(new Color(14, 48, 57));
            for (int x = 0; x <= COLS; x++) g2.drawLine(x * TILE, HEADER, x * TILE, HEADER + BOARD_H);
            for (int y = 0; y <= ROWS; y++) g2.drawLine(0, HEADER + y * TILE, BOARD_W, HEADER + y * TILE);

            g2.setColor(CYAN);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(2, HEADER + 2, BOARD_W - 5, BOARD_H - 5, 12, 12);

            if (food != null) drawFood(g2);

            for (int i = snake.size() - 1; i >= 0; i--) {
                Point p = snake.get(i);
                int x = p.x * TILE, y = HEADER + p.y * TILE;
                g2.setColor(i == 0 ? new Color(180, 255, 195) : NEON);
                g2.fillRoundRect(x + 2, y + 2, TILE - 4, TILE - 4, 9, 9);
                if (i == 0) {
                    g2.setColor(new Color(4, 45, 28));
                    if (direction == 'L' || direction == 'R') {
                        int ex = direction == 'R' ? x + 15 : x + 5;
                        g2.fillOval(ex, y + 6, 4, 4);
                        g2.fillOval(ex, y + 14, 4, 4);
                    } else {
                        int ey = direction == 'D' ? y + 15 : y + 5;
                        g2.fillOval(x + 6, ey, 4, 4);
                        g2.fillOval(x + 14, ey, 4, 4);
                    }
                }
            }

            if (paused) {
                g2.setColor(new Color(0, 8, 15, 215));
                g2.fillRoundRect(BOARD_W / 2 - 150, HEADER + BOARD_H / 2 - 60, 300, 120, 20, 20);
                g2.setColor(NEON);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(BOARD_W / 2 - 150, HEADER + BOARD_H / 2 - 60, 300, 120, 20, 20);
                g2.setFont(new Font("SansSerif", Font.BOLD, 32));
                drawCentered(g2, "PAUSED", HEADER + BOARD_H / 2);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                drawCentered(g2, "Press P or SPACE to resume", HEADER + BOARD_H / 2 + 28);
            }
            g2.dispose();
        }

        private void drawFood(Graphics2D g2) {
            int x = food.x * TILE, y = HEADER + food.y * TILE;
            BufferedImage apple = loadAsset("apple.png");
            if (apple != null) {
                g2.drawImage(apple, x + 1, y + 1, TILE - 2, TILE - 2, null);
            } else {
                g2.setColor(new Color(255, 65, 80));
                g2.fillOval(x + 3, y + 3, TILE - 6, TILE - 6);
                g2.setColor(new Color(255, 160, 165));
                g2.fillOval(x + 6, y + 5, 6, 6);
            }
        }

        private void drawCentered(Graphics2D g, String text, int y) {
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, (BOARD_W - fm.stringWidth(text)) / 2, y);
        }
    }

    private class OverScreen extends JunglePanel {
        private final JLabel scoreText = makeLabel("", 27, NEON);
        private final JLabel bestText = makeLabel("", 18, WHITE);

        OverScreen() {
            setPreferredSize(new Dimension(780, 620));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(55, 20, 30, 20));
            add(makeLabel("GAME OVER", 46, new Color(255, 100, 105)));
            add(Box.createVerticalStrut(10));
            add(makeLabel("SNAKE ARENA", 24, CYAN));
            add(Box.createVerticalStrut(22));
            add(scoreText);
            add(Box.createVerticalStrut(12));
            add(bestText);
            add(Box.createVerticalStrut(30));
            JButton again = makeButton("▶   PLAY AGAIN");
            JButton home = makeButton("⌂   MAIN MENU");
            JButton exit = makeButton("✕   EXIT GAME");
            for (JButton b : new JButton[]{again, home, exit}) {
                b.setAlignmentX(Component.CENTER_ALIGNMENT);
                add(b);
                add(Box.createVerticalStrut(12));
            }
            again.addActionListener(e -> startGame());
            home.addActionListener(e -> cards.show(root, "MENU"));
            exit.addActionListener(e -> System.exit(0));
        }

        void updateScreen() {
            scoreText.setText("YOUR SCORE: " + finalScore);
            bestText.setText("BEST SCORE: " + bestScore);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeGame::new);
    }
}
