import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

	int[] bitToDecimal = { 1, 2, 4, 8, 16, 32, 64, 128, 256 };

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
						validPositions[y][x] |= bitToDecimal[i - 1];
					}

				}
			}
		}
		return validPositions;
	}

	public int getByte(int number, int position) {
		// credit to
		// http://stackoverflow.com/questions/9354860/how-to-get-the-value-of-a-bit-at-a-certain-position-from-a-byte
		return (number >> (position - 1)) & 1;
	}

	// check if first cell is a 0

	public void solve(int[][] sudokuArray, boolean exist, int[][] validPositions) {
		history.push(new Point(currentY, currentX));
		nbPlaced++;
		// lockedCandidate(validPositions); // will need to write the code for
		// this to work ...
		boolean changed = true;
		boolean redo = true;

			while (changed) {
				changed = detectHiddenSingles(sudokuArray, validPositions);
				boolean tempChanged = onlyOnePossibility(sudokuArray, validPositions);
				changed = (changed || tempChanged);
				if(changed) {
					redo = true;
				}
				if(!changed && !redo) {
					break;
				}
				if(!changed && redo) {
					redo = false;
					changed = true;
					lockedPair(validPositions);
					lockedCandidate(validPositions);
				}
				
			}
		

		if (validPositions != null) {
			if (validPositions[currentY][currentX] != 0) {
				if (validPositions[currentY][currentX] > 0) {
					// may want to change that later on
					for (int i = 1; i <= 9; i++) {
						if (getByte(validPositions[currentY][currentX], i) == 1) {
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

		if (history.size() == 1) {
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
			/*
			 * validPositions.clear(); validPositions =
			 * generateValidPosition(sudokuArray);
			 */
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
		// remove credit to
		// http://stackoverflow.com/questions/15387482/remove-bits-from-number

		// colonne
		for (int i = (currentY + 1); i < 9; i++) {
			validPositions[i][currentX] &= ~bitToDecimal[k - 1];
		}

		// ligne
		for (int i = (currentX + 1); i < 9; i++) {
			validPositions[currentY][i] &= ~bitToDecimal[k - 1];
		}
		int rx = (currentX) / 3 + 1;
		int ry = (currentY) / 3 + 1;
		int j = currentX + 1;
		for (int i = currentY; i < 3 * ry; i++) {
			for (; j < 3 * rx; j++) {
				validPositions[i][j] &= ~bitToDecimal[k - 1];
			}
			j = (rx - 1) * 3;
		}
	}

	public void updateMapPoint(int[][] sudokuArray, int x, int y, int k, int[][] validPositions) {
		validPositions[y][x] = 0;
		// colonne
		callNb++;
		for (int i = 0; i < 9; i++) {
			validPositions[i][x] &= ~bitToDecimal[k - 1];
		}

		// ligne
		for (int i = 0; i < 9; i++) {
			validPositions[y][i] &= ~bitToDecimal[k - 1];
		}
		int rx = (x) / 3 + 1;
		int ry = (y) / 3 + 1;
		for (int i = ry * 3 - 3; i < 3 * ry; i++) {
			for (int j = rx * 3 - 3; j < 3 * rx; j++) {
				validPositions[i][j] &= ~bitToDecimal[k - 1];
			}
		}
	}

	// column
	public boolean detectHiddenSingles(int[][] sudokuArray, int[][] validPositions) {
		boolean changed = false;
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
					if (validPositions[y][x] == 0)
						continue;
					if (getByte(validPositions[y][x], j) == 1) {
						if (numberCol > -1) {
							numberCol = -1;
							break;
						}
						x1 = x;
						y1 = y;
						// pointCol = point;
						numberCol = j;
					}
				}

				if(numberCol == -1) {
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
							// pointRow = point;
							numberRow = j;
						}
					}
				}
				
				if(numberCol == -1 && numberCol == -1) {
					for (int x = 0; x < 9; x++) {
						int i = x % 3 + ((y % 3) * 3); // x
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
							// pointBox = point;
							numberBox = j;
						}
					}
				}
				if (numberCol != -1) {
					numberDetected++;
					sudokuArray[y1][x1] = numberCol;
					updateMapPoint(sudokuArray, x1, y1, numberCol, validPositions);
					changed = true;
				}

				if (numberRow != -1) {
					numberDetected++;
					sudokuArray[y2][x2] = numberRow;
					updateMapPoint(sudokuArray, x2, y2, numberRow, validPositions);
					changed = true;
				}

				if (numberBox != -1) {
					numberDetected++;
					sudokuArray[y3][x3] = numberBox;
					updateMapPoint(sudokuArray, x3, y3, numberBox, validPositions);
					changed = true;
				}
			}
		}

		// System.out.println("Col Hidden single detected : " + numberDetected);

		return changed;
	}

	public boolean onlyOnePossibility(int[][] sudokuArray, int[][] validPositions) {
		boolean changed = false;
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
					for (int i = 1; i <= 9; i++) {
						if (getByte(validPositions[y][x], i) == 1) {
							number = i;
							break;
						}
					}
					sudokuArray[y][x] = number;
				} catch (Exception exception) {
					return false; // backtracking
				}
				updateMapPoint(sudokuArray, x, y, number, validPositions);
				numberDetected++;
				changed = true;
			}
		}

		// System.out.println("Col Hidden single detected : " + numberDetected);

		return changed;
	}

	public boolean onlyOneBit(int number) {
		return number == (number & -number);
	}

	public boolean lockedPair(int[][] validPositions) { // only when we don't
														// have any options (
														// need to find if it it
														// work )
		// won't happen often;

		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				int numberOfBits = numberOfSetBits(validPositions[y][x]);
				if (numberOfBits != 2)
					continue; // naked pair :)
				int numberToSearchFor = validPositions[y][x];
				int j = x + 1;
				for (int k = ((y / 3) * 3) + (y % 3); k < ((y / 3) * 3) + 3; k++) {
					for (; j < (x / 3) * 3 + 3; j++) {
						if (validPositions[k][j] == numberToSearchFor) {
							updateMapLockedPair(x, j, y, k, validPositions);
						}
					}
					j = (x / 3) * 3;
				}
			}
		}

		return true;
	}

	public void lockedCandidate(int[][] validPositions) {
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				for(int i = 1; i <= 9; i++) {
					boolean isOkRow = true;
					boolean isOkColumn = true;
					boolean exit = false;
					
					if(getByte(validPositions[y][x], i) == 1) {
						for (int k = ((y / 3) * 3); k < ((y / 3) * 3) + 3; k++) {
							for (int j = (x / 3) * 3; j < (x / 3) * 3 + 3; j++) {
								if(x == j && y == k) continue;
								if (getByte(validPositions[k][j], i) == 1) {
									if(k == y) {
										isOkColumn = false;
									} else if(j == x) {
										isOkRow = false;
									} else {
										isOkColumn = false;
										isOkRow = false;
										exit = true;
										break;
									}
									
								}
							}
							
							if(exit) break;
						}
						
						if(isOkRow || isOkColumn) {
							updateMapLockedCandidate(i, isOkRow, x, y, bitToDecimal[i-1],  validPositions);
						}
					}
				}
			}
		}
	}
	
	public void updateMapLockedCandidate(int k, boolean isOkRow, int x, int y, int number, int[][] validPositions) {
		if(isOkRow) {
			for(int i = 0; i < 9; i++) {
				int rx = i / 3;
			
				if((rx * 3) + (rx%3) < ((x/3) * 3) || (rx * 3) + (rx%3) >= ((x/3) * 3 + 3)) {
					if(getByte(validPositions[y][i], k) == 1) {
						validPositions[y][i] &= ~number;
					}
					
				}
			}
		} else {
			for(int i = 0; i < 9; i++) {
				int ry = i / 3;
				if((ry * 3) + (ry%3) < ((y/3) * 3) || (ry * 3) + (ry%3) >= ((y/3) * 3 + 3)) {
					if(getByte(validPositions[i][x], k) == 1) {
						validPositions[i][x] &= ~number;
					}
					
				}
			}
		}
	}

	public void updateMapLockedPair(int x1, int x2, int y1, int y2, int[][] validPositions) {
		int numberToRemove = validPositions[y1][x1];
		if (x1 == x2) { // same column
			for (int y = 0; y < 9; y++) {
				if (y == y1 || y == y2)
					continue;

				validPositions[y][x1] &= ~numberToRemove;
			}
		} else if (y1 == y2) { // same row
			for (int x = 0; x < 9; x++) {
				if (x == x1 || x == x2)
					continue;

				validPositions[y1][x] &= ~numberToRemove;
			}
		}

		int rx = (x1 / 3) * 3;
		int ry = (y1 / 3) * 3;
		for (int i = ry; i < ry + 3; i++) {
			for (int j = rx; j < rx + 3; j++) {
				if ((x1 == j && y1 == i) || (x2 == j && y2 == i))
					continue;

				validPositions[i][j] &= ~numberToRemove;
			}
		}
		// same house always
	}

	public int numberOfSetBits(int i) {
		i = i - ((i >>> 1) & 0x55555555);
		i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
		return (((i + (i >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24;
	}

}