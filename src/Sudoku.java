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
	// Integer[][] sudokuArray = new Integer[9][9];

	// Map<Point, List<Integer>> validPositions = new HashMap<Point,
	// List<Integer>>();
	Stack<Point> history = new Stack<Point>();
	int currentX = 0; // colonne
	int currentY = 0; // ligne
	long startTime;
	int nbPlaced = 0;
	long callNb = 0;

	public void printMap(int[][] sudokuArray) {
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

	public Sudoku() {

	}

	public int[][] readBoard(String path) {
		int[][] sudokuArray = new int[9][9];
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			int y = 0;
			for (String line; (line = br.readLine()) != null;) {
				char[] characters = line.toCharArray();
				for (int x = 0; x < characters.length; x++) {
					sudokuArray[y][x] = Integer.parseInt(String.valueOf(characters[x]));
				}
				y++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sudokuArray;
	}

	public Map<Point, List<Integer>> generateValidPosition(int[][] sudokuArray) {
		Map<Point, List<Integer>> validPositions = new HashMap<Point, List<Integer>>();
		callNb++;
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (sudokuArray[y][x] != 0)
					continue;
				for (int i = 1; i <= 9; i++) {
					if (isValid(sudokuArray, x, y, i)) {
						if (validPositions.get(new Point(y, x)) == null) {
							List<Integer> myList = new ArrayList<Integer>();
							myList.add(i);
							validPositions.put(new Point(y, x), myList);
						} else {
							List<Integer> myList = validPositions.get(new Point(y, x));
							myList.add(i);
							validPositions.put(new Point(y, x), myList);
						}
					}

				}
			}
		}
		return validPositions;
	}
	
	public long calculateNbPossibilities(Map<Point, List<Integer>> validPositions) {
		long nbPossibilities = 0;
		
		for(Entry<Point, List<Integer>> entry : validPositions.entrySet()) {
			nbPossibilities += entry.getValue().size();
		}
		
		return nbPossibilities;
	}

	// check if first cell is a 0

	public void solve(int[][] sudokuArray, boolean exist, Map<Point, List<Integer>> validPositions) {
		history.push(new Point(currentY, currentX));
		nbPlaced++;
		//lockedCandidate(validPositions); // will need to write the code for this to work ...
		validPositions = detectHiddenSingles(sudokuArray, validPositions);
		validPositions = onlyOnePossibility(sudokuArray, validPositions);
		
		if (validPositions != null) {
			if (validPositions.get(new Point(currentY, currentX)) != null) {
				if (validPositions.get(new Point(currentY, currentX)).size() > 0) {
					for (int i : validPositions.get(new Point(currentY, currentX))) {
						forward(copyArray(sudokuArray), i, copyMap(validPositions));
					}
				}
			} else {
				if (sudokuArray[currentY][currentX] > 0)
					forward(copyArray(sudokuArray), -1, copyMap(validPositions));
			}
		}
		backtrack();
	}

	public void backtrack() {
		if (history.empty()) {
			return; // not able to solve it
		}

		if(history.size() == 1) {
			System.out.println("testing");
		}
		
		history.pop();
		
		

		Point previousPoint = history.peek();
		currentX = previousPoint.y;
		currentY = previousPoint.x;
		// updateMapBackward(temp);
	}

	public int[][] copyArray(int[][] sudokuArray) {
		int[][] copySudokuArray = new int[9][9];
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				copySudokuArray[i][j] = sudokuArray[i][j];

		return copySudokuArray;
	}

	public void forward(int[][] sudokuArray, int k, Map<Point, List<Integer>> validPositions) {


		if (k != -1) {
			updateMapForward(k, validPositions);
			/*validPositions.clear();
			validPositions = generateValidPosition(sudokuArray);*/
			sudokuArray[currentY][currentX] = k;
		}
		Point nextPoint = nextAvailableCell(sudokuArray);

		if (nextPoint == null) {
			long endTime = System.nanoTime();

			double duration = (endTime - startTime) / 1000000000.0; // divide by
																	// 1000000
																	// to get
			System.out.println(duration);
			System.out.println("Found it");
			printMap(sudokuArray);
			try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return; // succesful
		}
		currentX = nextPoint.y;
		currentY = nextPoint.x;
		//printMap(sudokuArray);
		solve(sudokuArray, false, validPositions);
	}

	public void startTimer() {
		startTime = System.nanoTime();
	}

	public Map<Point, List<Integer>> copyMap(Map<Point, List<Integer>> validPositions) {

		Map<Point, List<Integer>> newMap = new HashMap<Point, List<Integer>>();
		for (Entry<Point, List<Integer>> entry : validPositions.entrySet()) {
			newMap.put(new Point(entry.getKey()), new ArrayList<Integer>(entry.getValue()));
		}
		return newMap;
	}

	public Point nextAvailableCell(int[][] sudokuArray) {
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

	public boolean isValid(int[][] sudokuArray, int x, int y, int k) {
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

	public void updateMapForward(int k, Map<Point, List<Integer>> validPositions) {
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
				validPositions.get(new Point(currentY, i)).remove(new Integer(k));
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
	
	public void compareValidPositions(Map<Point, List<Integer>> validPositions1, Map<Point, List<Integer>> validPositions2) {
		for(int y = 0; y < 9; y++) {
			for(int x = 0; x < 9; x++) {
				if(validPositions1.get(new Point(y, x)) != null && validPositions2.get(new Point(y, x)) == null) {
					if(validPositions1.get(new Point(y, x)).size() > 0) {
						System.out.println("wow");
					}
				}
				else if(validPositions1.get(new Point(y, x)) == null && validPositions2.get(new Point(y, x)) != null) {
					if(validPositions2.get(new Point(y, x)).size() > 0) {
						System.out.println("wow");
					}
				}
				else if(validPositions1.get(new Point(y, x)) == null && validPositions2.get(new Point(y, x)) == null) {
					
				}
				else if(validPositions1.get(new Point(y, x)).size() != validPositions2.get(new Point(y, x)).size()) {
					System.out.println("1: " + validPositions1.get(new Point(y, x)).size());
					System.out.println("2: " + validPositions2.get(new Point(y, x)).size());
				}
				
			}
		}
	}
	
	public void updateMapPoint(int[][] sudokuArray, int x, int y, int k, Map<Point, List<Integer>> validPositions) {
		validPositions.remove(new Point(y, x));
		// colonne
		callNb++;
				for (int i = 0; i < 9; i++) {
				//	if(sudokuArray[i][x] > 0) continue;
					Point point = new Point(i, x);
					if (validPositions.get(point) != null) {
						validPositions.get(point).remove(new Integer(k));
					}
				}

				// ligne
				for (int i = 0; i < 9; i++) {
				//	if(sudokuArray[y][i] > 0) continue;
					Point point = new Point(y, i);
					if (validPositions.get(point) != null) {
						validPositions.get(point).remove(new Integer(k));
					}
				}
				int rx = (x) / 3 + 1;
				int ry = (y) / 3 + 1;
				for (int i = ry*3-3; i < 3 * ry; i++) {
					for (int j = rx * 3 - 3; j < 3 * rx; j++) {
			//			if(sudokuArray[i][j] > 0) continue;
					/*	System.out.println("The point: y: " + y + ", x: " + x);
						System.out.println("y: " + i + ", x: " + j);*/
						
						Point pointInGrid = new Point(i, j);
						if (validPositions.get(pointInGrid) != null) {
							validPositions.get(pointInGrid).remove(new Integer(k));
						}

					}
				}
	}

	// column
	public Map<Point, List<Integer>> detectHiddenSingles(int[][] sudokuArray, Map<Point, List<Integer>> validPositions) {
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
					Point point = new Point(y, x);
					if (validPositions.get(point) == null)
						continue;
					if (validPositions.get(point).contains(j)) {
						if (numberCol > -1) {
							numberCol = -1;
							break;
						}

						pointCol = point;
						numberCol = j;
					}
				}

				for (int x = 0; x < 9; x++) {
					Point point = new Point(x, y);
					if (validPositions.get(point) == null)
						continue;
					if (validPositions.get(point).contains(j)) {
						if (numberRow > -1) {
							numberRow = -1;
							break;
						}

						pointRow = point;
						numberRow = j;
					}
				}

				for (int x = 0; x < 9; x++) {
					Point point = new Point((y / 3) * 3 + (x / 3), x % 3 + ((y % 3) * 3));
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
					updateMapPoint(sudokuArray, pointCol.y, pointCol.x, numberCol, validPositions);
				}

				if (numberRow != -1) {
					numberDetected++;
					sudokuArray[pointRow.x][pointRow.y] = numberRow;
					updateMapPoint(sudokuArray, pointRow.y, pointRow.x, numberRow, validPositions);
				}

				if (numberBox != -1) {
					numberDetected++;
					sudokuArray[pointBox.x][pointBox.y] = numberBox;
					updateMapPoint(sudokuArray, pointBox.y, pointBox.x, numberBox, validPositions);
				}
			}
		}

		// System.out.println("Col Hidden single detected : " + numberDetected);

		return validPositions;
	}

	public Map<Point, List<Integer>> onlyOnePossibility(int[][] sudokuArray, Map<Point, List<Integer>> validPositions) {
		int numberDetected = 0;
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (validPositions.get(new Point(y, x)) == null)
					continue;
				if (validPositions.get(new Point(y, x)).size() > 1)
					continue;
				
				int number = 0;
				try {
				    number = validPositions.get(new Point(y, x)).get(0);
					sudokuArray[y][x] = number;
				} catch (Exception exception) {
					return null; // backtracking
				}
				updateMapPoint(sudokuArray, x, y, number, validPositions);
				numberDetected++;
			}
		}

		// System.out.println("Col Hidden single detected : " + numberDetected);

		return validPositions;
	}

	public void lockedCandidate(Map<Point, List<Integer>> validPositions) {
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
						if (testedNumber.contains(hypothesisNumber))
							continue;
						testedNumber.add(hypothesisNumber);

						boolean removeOthers = true;
						int rowNb = (point.x / 3) * 3;
						int colNb = (point.y / 3) * 3;
						for (int number = 0; number < 9; number++) {
							if (rowNb + (number % 3) == point.x)
								continue;

							if (validPositions.get(new Point(rowNb + (number % 3), colNb + (number / 3))) == null)
								continue;
							if (validPositions.get(new Point(rowNb + (number % 3), colNb + (number / 3)))
									.contains(hypothesisNumber)) {
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
								if (validPositions.get(new Point(point.x, number)) == null)
									continue;

								validPositions.get(new Point(point.x, number)).remove(new Integer(hypothesisNumber));
							}
						}
					}

				}

			}
		}

		// System.out.println("Removed hypothesis: " + removedHypothesis);
	}
}