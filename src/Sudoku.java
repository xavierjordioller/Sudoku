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
	
	int[] bitToDecimal = {
			1,
			2,
			4,
			8,
			16,
			32,
			64,
			128,
			256
	};

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

	public int[][] generateValidPosition(int[][] sudokuArray) {
		int[][] validPositions = new int[9][9];
		callNb++;
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (sudokuArray[y][x] != 0)
					continue;
				for (int i = 1; i <= 9; i++) {
					if (isValid(sudokuArray, x, y, i)) {
						validPositions[y][x] |= bitToDecimal[i-1];
					}

				}
			}
		}
		return validPositions;
	}
	
	public int getByte(int number, int position) {
		// credit to http://stackoverflow.com/questions/9354860/how-to-get-the-value-of-a-bit-at-a-certain-position-from-a-byte
		return (number >> (position -1)) & 1;
	}

	// check if first cell is a 0

	public void solve(int[][] sudokuArray, boolean exist, int[][] validPositions) {
		history.push(new Point(currentY, currentX));
		nbPlaced++;
		//lockedCandidate(validPositions); // will need to write the code for this to work ...
		validPositions = detectHiddenSingles(sudokuArray, validPositions);
		validPositions = onlyOnePossibility(sudokuArray, validPositions);
		
		if (validPositions != null) {
			if (validPositions[currentY][currentX] != 0) {
				if (validPositions[currentY][currentX] > 0) {
					// may want to change that later on
					for(int i = 1; i <= 9; i++) {
						if(getByte(validPositions[currentY][currentX], i) == 1) {
							forward(copyArray(sudokuArray), i, copyArray(validPositions));
						}
					}
				}
			} else {
				if (sudokuArray[currentY][currentX] > 0)
					forward(copyArray(sudokuArray), -1, copyArray(validPositions));
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

	public void forward(int[][] sudokuArray, int k, int[][] validPositions) {


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
		solve(sudokuArray, false, validPositions);
	}

	public void startTimer() {
		startTime = System.nanoTime();
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

	public void updateMapForward(int k, int[][] validPositions) {
		// remove credit to http://stackoverflow.com/questions/15387482/remove-bits-from-number
		
		// colonne
		for (int i = (currentY + 1); i < 9; i++) {
			validPositions[i][currentX] &= ~bitToDecimal[k-1];
		}

		// ligne
		for (int i = (currentX + 1); i < 9; i++) {
			validPositions[currentY][i] &= ~bitToDecimal[k-1];
		}
		int rx = (currentX) / 3 + 1;
		int ry = (currentY) / 3 + 1;
		int j = currentX + 1;
		for (int i = currentY; i < 3 * ry; i++) {
			for (; j < 3 * rx; j++) {
				validPositions[i][j] &= ~bitToDecimal[k-1];
			}
			j = (rx - 1) * 3;
		}
	}
	
	public void updateMapPoint(int[][] sudokuArray, int x, int y, int k, int[][] validPositions) {
		validPositions[y][x] = 0;
		// colonne
		callNb++;
				for (int i = 0; i < 9; i++) {
					validPositions[i][x] &= ~bitToDecimal[k-1];
				}

				// ligne
				for (int i = 0; i < 9; i++) {
					validPositions[y][i] &= ~bitToDecimal[k-1];
				}
				int rx = (x) / 3 + 1;
				int ry = (y) / 3 + 1;
				for (int i = ry*3-3; i < 3 * ry; i++) {
					for (int j = rx * 3 - 3; j < 3 * rx; j++) {
						validPositions[i][j] &= ~bitToDecimal[k-1];
					}
				}
	}

	// column
	public int[][] detectHiddenSingles(int[][] sudokuArray, int[][] validPositions) {
		int numberDetected = 0;
		for (int j = 1; j <= 9; j++) {
			for (int y = 0; y < 9; y++) {
				int x1 = 0;
				int x2 = 0;
				int x3 = 0;
				
				int y1 = 0;
				int y2 = 0;
				int y3 = 0;
				int numberCol = -1;

				int numberRow = -1;

				int numberBox = -1;

				for (int x = 0; x < 9; x++) {
					if(validPositions[y][x] == 0) continue;
					if (getByte(validPositions[y][x], j) == 1) {
						if (numberCol > -1) {
							numberCol = -1;
							break;
						}
						x1 = x;
						y1 = y;
						//pointCol = point;
						numberCol = j;
					}
				}

				for (int x = 0; x < 9; x++) {
					if (validPositions[x][y] == 0)
						continue;
					if (getByte(validPositions[x][y], j) == 1) {
						if (numberRow > -1) {
							numberRow = -1;
							break;
						}
						x2 = y;
						y2 = x;
						//pointRow = point;
						numberRow = j;
					}
				}

				for (int x = 0; x < 9; x++) {
					int i = x % 3 + ((y % 3) * 3); //x
					int k = (y / 3) * 3 + (x / 3); // y
					if (validPositions[k][i] == 0)
						continue;
					if (getByte(validPositions[k][i], j) == 1) {
						if (numberBox > -1) {
							numberBox = -1;
							break;
						}
						x3 = i;
						y3 = k;
						//pointBox = point;
						numberBox = j;
					}
				}

				if (numberCol != -1) {
					numberDetected++;
					sudokuArray[y1][x1] = numberCol;
					updateMapPoint(sudokuArray, x1, y1, numberCol, validPositions);
				}

				if (numberRow != -1) {
					numberDetected++;
					sudokuArray[y2][x2] = numberRow;
					updateMapPoint(sudokuArray, x2, y2, numberRow, validPositions);
				}

				if (numberBox != -1) {
					numberDetected++;
					sudokuArray[y3][x3] = numberBox;
					updateMapPoint(sudokuArray, x3, y3, numberBox, validPositions);
				}
			}
		}

		// System.out.println("Col Hidden single detected : " + numberDetected);

		return validPositions;
	}

	public int[][] onlyOnePossibility(int[][] sudokuArray, int[][] validPositions) {
		int numberDetected = 0;
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (validPositions[y][x] == 0)
					continue;
				if (!onlyOneBit(validPositions[y][x]))
					continue;
				
				int number = 0;
				try {
					// dumb way
					for(int i = 1; i <= 9; i++) {
						if(getByte(validPositions[y][x], i) == 1) {
							number = i;
							break;
						}
					}
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
	
	public boolean onlyOneBit (int number)
	{
		return number == (number & -number);
	}

	public void lockedCandidate(int[][] validPositions) {
		// line

		// it works in the box line and column

		// this technique removes hypothesis ( could be use later on with
		// finding hidden singles or nakes singles

		int removedHypothesis = 0;

		for (int i = 0; i < 27; i++) {
			List<Integer> testedNumber = new ArrayList<Integer>();
			for (int k = 0; k < 3; k++) {
				int x = (i % 3) * 3 + k;
				int y = i / 3;
				if (validPositions[y][x] > 0) {
					for(int t = 1; t <= 9; i++) {
						if(getByte(validPositions[currentY][currentX], t) == 1) {
							int hypothesisNumber = t;
						if (testedNumber.contains(hypothesisNumber))
							continue;
						testedNumber.add(hypothesisNumber);

						boolean removeOthers = true;
						
						int rowNb = (y / 3) * 3;
						int colNb = (x / 3) * 3;
						for (int number = 0; number < 9; number++) {
							if (rowNb + (number % 3) == y)
								continue;
							int newX = colNb + (number / 3);
							int newY = rowNb + (number % 3);
							if (validPositions[newY][newX] == 0)
								continue;
							if (getByte(validPositions[newY][newX], hypothesisNumber) == 1) {
								removeOthers = false;
								break;
							}
						}

						// number x is only possible in this row...
						if (removeOthers) {
							removedHypothesis++;
							for (int number = 0; number < 9; number++) {

								if (x / 3 == number / 3)
									continue;
								if (validPositions[y][number] == 0)
									continue;

								validPositions[y][number]&= ~bitToDecimal[hypothesisNumber-1];
							}
						}
					}
				}

				}

			}
		}

		// System.out.println("Removed hypothesis: " + removedHypothesis);
	}
}