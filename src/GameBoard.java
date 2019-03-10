
/**
 * @author Nicholas Contreras
 */

public class GameBoard {

	public enum TileState {
		EMPTY, BLOCKED, CAT;
	}

	private class Tile {
		private TileState state;
		private boolean isEdge;

		private Tile() {
			state = TileState.EMPTY;
			isEdge = false;
		}
	}

	private static final int[] BOARD_SIZES = { 10, 15, 20 };
	private static final String[] DIFFICULITY_STRINGS = { "Easy", "Medium", "Hard" };

	private int difficulty;
	private boolean gameWin, gameLose;
	private Tile[][] board;

	public static GameBoard createNewGame(String difficulityString) {
		int difficulty = -1;

		for (int i = 0; i < DIFFICULITY_STRINGS.length; i++) {
			if (difficulityString.equals(DIFFICULITY_STRINGS[i])) {
				difficulty = i;
				break;
			}
		}

		while (true) {
			GameBoard newBoard = new GameBoard(difficulty);
			newBoard.createBlankBoard();
			newBoard.roughenEdges(difficulty * 2, 0.2);
			newBoard.placeRandomStartingWalls();
			boolean isValid = newBoard.placeCat();
			if (isValid) {
				newBoard.removeUnreachableTiles();
				double result = MathMagicThingy.findCatDistanceToWin(newBoard);

				if (result > 4) {
					return newBoard;
				}
			}
		}
	}

	private GameBoard(int difficulty) {
		this.difficulty = difficulty;
		gameWin = false;
		gameLose = false;
		board = new Tile[BOARD_SIZES[difficulty]][BOARD_SIZES[difficulty]];
	}

	private void createBlankBoard() {
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				board[row][col] = new Tile();
				if (row == 0 || row == board.length - 1 || col == 0 || col == board[row].length - 1) {
					board[row][col].isEdge = true;
				}
			}
		}
	}

	public int getBoardSize() {
		return board.length;
	}

	public TileState getTileStateAt(int row, int col) {
		return board[row][col] == null ? null : board[row][col].state;
	}

	public void setTileState(int row, int col, TileState state) {
		board[row][col].state = state;
	}

	public boolean isEdgeTile(int row, int col) {
		return board[row][col] == null ? false : board[row][col].isEdge;
	}

	public void addWall(int row, int col) {
		board[row][col].state = TileState.BLOCKED;
	}

	public void triggerWin() {
		gameWin = true;
	}

	public void triggerLoss() {
		gameLose = true;
	}

	public boolean isWon() {
		return gameWin;
	}

	public boolean isOver() {
		return gameWin || gameLose;
	}

	private void roughenEdges(int cycles, double removalFactor) {
		for (int i = 0; i < cycles; i++) {
			for (int row = 0; row < board.length; row++) {
				for (int col = 0; col < board[row].length; col++) {
					if (board[row][col] != null && board[row][col].isEdge) {
						if (Math.random() < removalFactor) {
							board[row][col] = null;
							if (row != 0 && board[row - 1][col] != null) {
								board[row - 1][col].isEdge = true;
							}
							if (row != board.length - 1 && board[row + 1][col] != null) {
								board[row + 1][col].isEdge = true;
							}
							if (col != 0 && board[row][col - 1] != null) {
								board[row][col - 1].isEdge = true;
							}
							if (col != board[row].length - 1 && board[row][col + 1] != null) {
								board[row][col + 1].isEdge = true;
							}
						}
					}
				}
			}
		}
	}

	private void removeUnreachableTiles() {

		int catRow = -1, catCol = -1;
		boolean foundCat = false;

		for (int row = 0; row < board.length && !foundCat; row++) {
			for (int col = 0; col < board[row].length && !foundCat; col++) {
				if (board[row][col] != null && board[row][col].state == TileState.CAT) {
					catRow = row;
					catCol = col;
					foundCat = true;
				}
			}
		}

		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				if (board[row][col] != null && board[row][col].state == TileState.EMPTY) {
					int[] path = MathMagicThingy.findPath(row, col, catRow, catCol, this);
					if (path[0] == Integer.MAX_VALUE) {
						board[row][col] = null;
					}
				}
			}
		}
	}

	private void placeRandomStartingWalls() {
		int num = (int) ((Math.random() * 5) + 5);

		for (int i = 0; i < num; i++) {
			int row = (int) (Math.random() * board.length);
			int col = (int) (Math.random() * board[row].length);

			if (board[row][col] != null && board[row][col].state == TileState.EMPTY) {
				board[row][col].state = TileState.BLOCKED;
			} else {
				i--;
			}
		}
	}

	private boolean placeCat() {
		for (int i = 0; i < 99999; i++) {
			int row = (int) (Math.random() * board.length);
			int col = (int) (Math.random() * board[row].length);
			if (board[row][col] != null && !board[row][col].isEdge && board[row][col].state == TileState.EMPTY) {
				board[row][col].state = TileState.CAT;
				return true;
			}
		}
		return false;
	}

	public GameBoard makeCopy() {
		GameBoard newBoard = new GameBoard(difficulty);
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				if (board[row][col] != null) {
					newBoard.board[row][col] = new Tile();
					newBoard.board[row][col].isEdge = board[row][col].isEdge;
					newBoard.board[row][col].state = board[row][col].state;
				}
			}
		}
		return newBoard;
	}

	@Override
	public String toString() {
		String output = "";
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				if (board[row][col] != null) {
					switch (board[row][col].state) {
					case EMPTY:
						output += " ";
						break;
					case BLOCKED:
						output += "0";
						break;
					case CAT:
						output += "M";
						break;
					}
				} else {
					output += "X";
				}
			}
			output += "\n";
		}
		return output;
	}
}
