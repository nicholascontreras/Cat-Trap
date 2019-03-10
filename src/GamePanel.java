import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * @author Nicholas Contreras
 */

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements MouseListener, ActionListener {
	private JFrame frame;
	private JDialog popup;
	private JLabel popupResultLabel;

	private boolean generateNewGame;

	private GameBoard curGameBoard;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new GamePanel().initGame());
	}

	private void initGame() {
		frame = new JFrame("Cat Trap");

		this.setDoubleBuffered(true);
		this.setPreferredSize(new Dimension(1000, 1000));
		this.addMouseListener(this);

		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		popup = new JDialog(frame, "Play Again?", JDialog.ModalityType.APPLICATION_MODAL);
		JPanel popupPanel = new JPanel(new BorderLayout());
		popupResultLabel = new JLabel();
		popupResultLabel.setHorizontalAlignment(SwingConstants.CENTER);
		popupResultLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
		popupPanel.add(popupResultLabel, BorderLayout.NORTH);
		JLabel newGameLabel = new JLabel("  Select a difficulty to play  ");
		newGameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		newGameLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
		popupPanel.add(newGameLabel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
		JButton easyButton = new JButton("Easy");
		easyButton.setActionCommand("Easy");
		easyButton.addActionListener(this);
		buttonPanel.add(easyButton);
		JButton mediumButton = new JButton("Medium");
		mediumButton.setActionCommand("Medium");
		mediumButton.addActionListener(this);
		buttonPanel.add(mediumButton);
		JButton hardButton = new JButton("Hard");
		hardButton.setActionCommand("Hard");
		hardButton.addActionListener(this);
		buttonPanel.add(hardButton);

		popupPanel.add(buttonPanel, BorderLayout.SOUTH);

		popup.add(popupPanel);
		popup.pack();

		showNewGameWindow();

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				repaint();
			}
		}, 0, 50);
	}

	private void showNewGameWindow() {

		generateNewGame = false;

		if (curGameBoard != null) {
			popupResultLabel.setText(curGameBoard.isWon() ? "YOU WIN" : "YOU LOSE");
		} else {
			popupResultLabel.setText("");
		}

		popup.pack();
		
		popup.setVisible(true);

		if (!generateNewGame) {
			System.exit(0);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if (curGameBoard == null) {
			return;
		}

		int tileSize = getWidth() / (curGameBoard.getBoardSize() + 2);

		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		for (int row = 0; row < curGameBoard.getBoardSize(); row++) {
			for (int col = 0; col < curGameBoard.getBoardSize(); col++) {
				if (curGameBoard.getTileStateAt(row, col) == null) {
					continue;
				}
				switch (curGameBoard.getTileStateAt(row, col)) {
				case EMPTY:
					g2d.setColor(Color.LIGHT_GRAY);
					break;
				case BLOCKED:
					g2d.setColor(Color.DARK_GRAY);
					break;
				case CAT:
					g2d.setColor(Color.GREEN);
					break;
				}

				g2d.fillRect(tileSize * (col + 1), tileSize * (row + 1), tileSize, tileSize);
				g2d.setColor(Color.BLACK);
				g2d.drawRect(tileSize *  (col + 1), tileSize * (row + 1), tileSize, tileSize);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int tileSize = getWidth() / (curGameBoard.getBoardSize() + 2);
		int row = (e.getY() / tileSize) - 1;
		int col = (e.getX() / tileSize) - 1;
		
		if (row < 0 || row >= curGameBoard.getBoardSize()) {
			return;
		}
		if (col < 0 || col >= curGameBoard.getBoardSize()) {
			return;
		}

		if (curGameBoard.getTileStateAt(row, col) == GameBoard.TileState.EMPTY) {
			curGameBoard.addWall(row, col);
			MathMagicThingy.advanceGameTurn(curGameBoard);

			if (curGameBoard.isOver()) {
				showNewGameWindow();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		generateNewGame = true;
		curGameBoard = GameBoard.createNewGame(arg0.getActionCommand());
		popup.setVisible(false);
	}
}
