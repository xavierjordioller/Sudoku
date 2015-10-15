import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class Sudoku {
	Integer[][] sudokuArray = new Integer[9][9];

	Map<Point, List<Integer>> validPositions = new HashMap<Point, List<Integer>>();
	Stack<Point> history = new Stack<Point>();
	int currentX = 0; // colonne
	int currentY = 0; // ligne
	long startTime;
	int globalTesting = 0;

	public void printMap() {
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		for (int i = 0; i < 9; i++) {
			if (i == 3 || i == 6) {
				System.out.println();
			}
			for (int j = 0; j < 9; j++) {
				if (j == 3 || j == 6) {
					System.out.print(" ");
				}
				System.out.print(String.valueOf(sudokuArray[i][j]));
			}
			System.out.println();
		}
	}

	public Sudoku(String path) {
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			int y = 0;
			for (String line; (line = br.readLine()) != null;) {
				char[] characters = line.toCharArray();
				for (int x = 0; x < characters.length; x++) {
					sudokuArray[y][x] = Integer.parseInt(String
							.valueOf(characters[x]));
				}
				y++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateValidPosition() {
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (sudokuArray[y][x] != 0)
					continue;
				for (int i = 1; i <= 9; i++) {
					if (isValid(x, y, i)) {
						if (validPositions.get(new Point(y, x)) == null) {
							List<Integer> myList = new ArrayList<Integer>();
							myList.add(i);
							validPositions.put(new Point(y, x), myList);
						} else {
							List<Integer> myList = validPositions
									.get(new Point(y, x));
							myList.add(i);
							validPositions.put(new Point(y, x), myList);
						}
					}

				}
			}
		}
		startTime = System.nanoTime();
	}

	// check if first cell is a 0

	public void solve(boolean exist) {
		history.push(new Point(currentY, currentX));

		for (int i : validPositions.get(new Point(currentY, currentX))) {
			forward(i);
		}

		backtrack();
	}

	public void backtrack() {
		sudokuArray[currentY][currentX] = 0;
		if (history.empty()) {
			return; // not able to solve it
		}

		history.pop();

		if (history.size() > 0) {
			Point previousPoint = history.peek();
			currentX = previousPoint.y;
			currentY = previousPoint.x;
		} else {
			currentX = 0;
			currentY = 0;
		}

		int temp = sudokuArray[currentY][currentX];
		sudokuArray[currentY][currentX] = 0;

		updateMapBackward(temp);
	}

	public void forward(int k) {
		if (currentX == 8 && currentY == 8) {
			long endTime = System.nanoTime();
			double duration = (endTime - startTime) / 1000000000.0; // divide by
																	// 1000000
																	// to get
																	// milliseconds.
			System.out.println(duration);
			System.out.println("Found it");

		}
		validPositions.toString();
		updateMapForward(k);
		sudokuArray[currentY][currentX] = k;
		Point nextPoint = nextAvailableCell();

		if (nextPoint == null) {
			return; // succesful
		}
		currentX = nextPoint.y;
		currentY = nextPoint.x;
		solve(false);
	}

	public Point nextAvailableCell() {
		int nextCurrentX = currentX;
		int nextCurrentY = currentY;
		nextCurrentX++;

		for (; nextCurrentY < 9; nextCurrentY++) {
			for (; nextCurrentX < 9; nextCurrentX++) {
				if (sudokuArray[nextCurrentY][nextCurrentX] == 0)
					return new Point(nextCurrentY, nextCurrentX);
			}
			nextCurrentX = 0;
		}

		return null;
	}

	public boolean isValid(int x, int y, int k) {
		int rowNb = (y / 3) * 3;
		int colNb = (x / 3) * 3;
		for (int i = 0; i < 9; i++) {
			if (sudokuArray[y][i] == k)
				return false;
			if (sudokuArray[i][x] == k)
				return false;
			if (sudokuArray[rowNb + (i % 3)][colNb + (i / 3)] == k)
				return false;
		}
		return true;
	}

	public void updateMap(int x, int y, int k) {

	}

	public void updateMapForward(int k) {
		// colonne
		for (int i = (currentY + 1); i < 9; i++) {
			Point point = new Point(i, currentX);
			if (validPositions.containsKey(point)) {
				validPositions.get(point).remove(new Integer(k));
			}
		}

		// ligne
		for (int i = (currentX + 1); i < 9; i++) {
			Point point = new Point(currentY, i);
			if (validPositions.containsKey(point)) {
				validPositions.get(new Point(currentY, i)).remove(
						new Integer(k));
			}
		}
		int rx = (currentX) / 3 + 1;
		int ry = (currentY) / 3 + 1;
		int j = currentX + 1;
		for (int i = currentY; i < 3 * ry; i++) {
			for (; j < 3 * rx; j++) {
				Point pointInGrid = new Point(i, j);
				if (validPositions.containsKey(pointInGrid)) {
					validPositions.get(pointInGrid).remove(new Integer(k));
				}

			}
			j = (rx - 1) * 3;
		}
	}

	public void updateMapBackward(int k) {
		for (int i = (currentY + 1); i < 9; i++) {
			Point point = new Point(i, currentX);
			if (validPositions.containsKey(point)) {
				if (isValid(currentX, i, k))
					validPositions.get(point).add(new Integer(k));
			}
		}

		for (int i = (currentX + 1); i < 9; i++) {
			Point point = new Point(currentY, i);
			if (validPositions.containsKey(point)) {
				if (isValid(i, currentY, k))
					validPositions.get(point).add(new Integer(k));
			}
		}
		int rx = (currentX) / 3 + 1;
		int ry = (currentY) / 3 + 1;
		int j = currentX + 1;
		for (int i = currentY; i < 3 * ry; i++) {
			for (; j < 3 * rx; j++) {
				Point pointInGrid = new Point(i, j);
				if (validPositions.containsKey(pointInGrid)) {
					if (!validPositions.get(pointInGrid).contains(
							new Integer(k)))
						if (isValid(j, i, k))
							validPositions.get(pointInGrid).add(new Integer(k));

				}
			}
			j = (rx - 1) * 3;
		}
	}

	// column
	public void detectHiddenSingles() {
		int numberDetected = 0;
		for (int j = 1; j <= 9; j++) {
			for (int y = 0; y < 9; y++) {
				Point pointCol = null;
				int numberCol = -1;

				Point pointRow = null;
				int numberRow = -1;

				Point pointBox = null;
				int numberBox = -1;

				for (int x = 0; x < 9; x++) {
					if (validPositions.get(new Point(y, x)) == null)
						continue;
					if (validPositions.get(new Point(y, x)).contains(j)) {
						if (numberCol > -1) {
							numberCol = -1;
							break;
						}

						pointCol = new Point(y, x);
						numberCol = j;
					}
				}

				for (int x = 0; x < 9; x++) {
					if (validPositions.get(new Point(x, y)) == null)
						continue;
					if (validPositions.get(new Point(x, y)).contains(j)) {
						if (numberRow > -1) {
							numberRow = -1;
							break;
						}

						pointRow = new Point(x, y);
						numberRow = j;
					}
				}

				for (int x = 0; x < 9; x++) {
					Point point = new Point((y / 3) * 3 + (x / 3), x % 3
							+ ((y % 3) * 3));
					if (validPositions.get(point) == null)
						continue;
					if (validPositions.get(point).contains(j)) {
						if (numberBox > -1) {
							numberBox = -1;
							break;
						}

						pointBox = point;
						numberBox = j;
					}
				}

				if (numberCol != -1) {
					numberDetected++;
					sudokuArray[pointCol.x][pointCol.y] = numberCol;
					validPositions.clear();
					generateValidPosition();
				}

				if (numberRow != -1) {
					numberDetected++;
					sudokuArray[pointRow.x][pointRow.y] = numberRow;
					validPositions.clear();
					generateValidPosition();
				}

				if (numberBox != -1) {
					numberDetected++;
					sudokuArray[pointBox.x][pointBox.y] = numberBox;
					validPositions.clear();
					generateValidPosition();
				}
			}
		}

		// System.out.println("Col Hidden single detected : " + numberDetected);
	}

	public void onlyOnePossibility() {
		int numberDetected = 0;
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (validPositions.get(new Point(y, x)) == null)
					continue;
				if (validPositions.get(new Point(y, x)).size() > 1)
					continue;
				sudokuArray[y][x] = validPositions.get(new Point(y, x)).get(0);
				validPositions.clear();
				generateValidPosition();
				numberDetected++;
			}
		}

		 //System.out.println("Col Hidden single detected : " + numberDetected);
	}

	public void lockedCandidate() {
		// line

		// it works in the box line and column

		// this technique removes hypothesis ( could be use later on with
		// finding hidden singles or nakes singles
		
		int removedHypothesis = 0;

		for (int i = 0; i < 27; i++) {
			List<Integer> testedNumber = new ArrayList<Integer>();
			for (int k = 0; k < 3; k++) {
				Point point = new Point(i / 3, (i % 3) * 3 + k); // pointToTest
				if (validPositions.get(point) != null) {
					for (int hypothesisNumber : validPositions.get(point)) {
						testedNumber.add(hypothesisNumber);

						boolean removeOthers = true;
						int rowNb = (point.x / 3) * 3;
						int colNb = (point.y / 3) * 3;
						for (int number = 0; number < 9; number++) {
							if (rowNb + (number % 3) == point.x)
								continue;

							if (validPositions.get(new Point(rowNb
									+ (number % 3), colNb + (number / 3))) == null)
								continue;
							if (validPositions.get(
									new Point(rowNb + (number % 3), colNb
											+ (number / 3))).contains(
									hypothesisNumber)) {
								removeOthers = false;
								break;
							}
						}

						// number x is only possible in this row...
						if (removeOthers) {
							removedHypothesis++;
							for (int number = 0; number < 9; number++) {

								if (point.y / 3 == number / 3)
									continue;
								if (validPositions.get(new Point(point.x,
										number)) == null)
									continue;
								validPositions.get(new Point(point.x, number))
										.remove(new Integer(hypothesisNumber));
							}
						}
					}

				}

			}
		}
		
		//System.out.println("Removed hypothesis: " + removedHypothesis);
	}
}