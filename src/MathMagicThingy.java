/**
 * @author Nicholas Contreras
 */

public class MathMagicThingy {

	public static int[] findNextCatMove(GameBoard gameBoard) {
		int catRow = -1, catCol = -1;
		boolean foundCat = false;

		for (int row = 0; row < gameBoard.getBoardSize() && !foundCat; row++) {
			for (int col = 0; col < gameBoard.getBoardSize() && !foundCat; col++) {
				if (gameBoard.getTileStateAt(row, col) == GameBoard.TileState.CAT) {
					catRow = row;
					catCol = col;
					foundCat = true;
				}
			}
		}

		int[] bestResult = new int[] { Integer.MAX_VALUE, -1, -1 };

		for (int row = 0; row < gameBoard.getBoardSize(); row++) {
			for (int col = 0; col < gameBoard.getBoardSize(); col++) {
				if (gameBoard.isEdgeTile(row, col) && gameBoard.getTileStateAt(row, col) == GameBoard.TileState.EMPTY) {
					int[] result = findPath(catRow, catCol, row, col, gameBoard);

					// System.out.println("Found path result: " + result[0]);

					if (result[0] < bestResult[0]) {
						bestResult = result;
					}
				}
			}
		}

		if (bestResult[0] == Integer.MAX_VALUE) {
			return null;
		} else {
			return new int[] { bestResult[1], bestResult[2] };
		}
	}

	public static int findCatDistanceToWin(GameBoard gameBoard) {
		int catRow = -1, catCol = -1;
		boolean foundCat = false;

		for (int row = 0; row < gameBoard.getBoardSize() && !foundCat; row++) {
			for (int col = 0; col < gameBoard.getBoardSize() && !foundCat; col++) {
				if (gameBoard.getTileStateAt(row, col) == GameBoard.TileState.CAT) {
					catRow = row;
					catCol = col;
					foundCat = true;
				}
			}
		}

		int[] bestResult = new int[] { Integer.MAX_VALUE, -1, -1 };

		for (int row = 0; row < gameBoard.getBoardSize(); row++) {
			for (int col = 0; col < gameBoard.getBoardSize(); col++) {
				if (gameBoard.isEdgeTile(row, col) && gameBoard.getTileStateAt(row, col) == GameBoard.TileState.EMPTY) {
					int[] result = findPath(catRow, catCol, row, col, gameBoard);

					// System.out.println("Found path result: " + result[0]);

					if (result[0] < bestResult[0]) {
						bestResult = result;
					}
				}
			}
		}

		if (bestResult[0] == Integer.MAX_VALUE) {
			return -1;
		} else {
			return bestResult[0];
		}
	}

	public static int[] findPath(int startRow, int startCol, int endRow, int endCol, GameBoard board) {
		int[][] pathfindingMesh = new int[board.getBoardSize()][board.getBoardSize()];

		for (int row = 0; row < board.getBoardSize(); row++) {
			for (int col = 0; col < board.getBoardSize(); col++) {
				if (board.getTileStateAt(row, col) != GameBoard.TileState.EMPTY
						&& board.getTileStateAt(row, col) != GameBoard.TileState.CAT) {
					pathfindingMesh[row][col] = -1;
				}
			}
		}

		pathfindingMesh[endRow][endCol] = 1;

		boolean noPath = false;
		for (int i = 1; !noPath; i++) {
			noPath = true;

			for (int row = 0; row < board.getBoardSize(); row++) {
				for (int col = 0; col < board.getBoardSize(); col++) {
					if (pathfindingMesh[row][col] == i) {
						if (row == startRow && col == startCol) {
							int bestRow = row, bestCol = col, bestScore = i;

							if (row != 0 && pathfindingMesh[row - 1][col] > 0
									&& pathfindingMesh[row - 1][col] < bestScore) {
								bestRow = row - 1;
								bestCol = col;
								bestScore = pathfindingMesh[row - 1][col];
							}
							if (row != pathfindingMesh.length - 1 && pathfindingMesh[row + 1][col] > 0
									&& pathfindingMesh[row + 1][col] < bestScore) {
								bestRow = row + 1;
								bestCol = col;
								bestScore = pathfindingMesh[row + 1][col];
							}
							if (col != 0 && pathfindingMesh[row][col - 1] > 0
									&& pathfindingMesh[row][col - 1] < bestScore) {
								bestRow = row;
								bestCol = col - 1;
								bestScore = pathfindingMesh[row][col - 1];
							}
							if (col != pathfindingMesh[row].length - 1 && pathfindingMesh[row][col + 1] > 0
									&& pathfindingMesh[row][col + 1] < bestScore) {
								bestRow = row;
								bestCol = col + 1;
								bestScore = pathfindingMesh[row][col + 1];
							}
							return new int[] { bestScore, bestRow, bestCol };
						}

						if (row != 0 && pathfindingMesh[row - 1][col] == 0) {
							pathfindingMesh[row - 1][col] = i + getNearbyWallFactor(row - 1, col, board);
							noPath = false;
						}
						if (row != pathfindingMesh.length - 1 && pathfindingMesh[row + 1][col] == 0) {
							pathfindingMesh[row + 1][col] = i + getNearbyWallFactor(row + 1, col, board);
							noPath = false;
						}
						if (col != 0 && pathfindingMesh[row][col - 1] == 0) {
							pathfindingMesh[row][col - 1] = i + getNearbyWallFactor(row, col - 1, board);
							noPath = false;
						}
						if (col != pathfindingMesh[row].length - 1 && pathfindingMesh[row][col + 1] == 0) {
							pathfindingMesh[row][col + 1] = i + getNearbyWallFactor(row, col + 1, board);
							noPath = false;
						}
					}
				}
			}
		}
		return new int[] { Integer.MAX_VALUE, -1, -1 };
	}

	private static int getNearbyWallFactor(int row, int col, GameBoard board) {
		int factor = 1;

		if (row != 0 && board.getTileStateAt(row - 1, col) == GameBoard.TileState.BLOCKED) {
			factor *= factor;
		}
		if (row != board.getBoardSize() - 1 && board.getTileStateAt(row + 1, col) == GameBoard.TileState.BLOCKED) {
			factor *= factor;
		}
		if (col != 0 && board.getTileStateAt(row, col - 1) == GameBoard.TileState.BLOCKED) {
			factor *= factor;
		}
		if (col != board.getBoardSize() - 1 && board.getTileStateAt(row, col + 1) == GameBoard.TileState.BLOCKED) {
			factor *= factor;
		}
		return factor;
	}

	public static void advanceGameTurn(GameBoard gameBoard) {
		int[] nextCatMove = findNextCatMove(gameBoard);

		if (nextCatMove == null) {
			gameBoard.triggerWin();
		} else {
			for (int row = 0; row < gameBoard.getBoardSize(); row++) {
				for (int col = 0; col < gameBoard.getBoardSize(); col++) {
					if (gameBoard.getTileStateAt(row, col) == GameBoard.TileState.CAT) {
						gameBoard.setTileState(row, col, GameBoard.TileState.EMPTY);
					}
				}
			}
			gameBoard.setTileState(nextCatMove[0], nextCatMove[1], GameBoard.TileState.CAT);
			if (gameBoard.isEdgeTile(nextCatMove[0], nextCatMove[1])) {
				gameBoard.triggerLoss();
			}
		}
	}
}
