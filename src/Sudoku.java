import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Sudoku {
	Integer[][] sudokuArray = new Integer[9][9];

	Map<Point, List<Integer>> validPositions = new HashMap<Point, List<Integer>>();
	Stack<Point> history = new Stack<Point>();
	int currentX = 2;
	int currentY = 0;
	long startTime;
	
	public void printMap() {
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		for(int i = 0; i < 9; i++) {
			if(i == 3 || i == 6) {
				System.out.println();
			}
			for(int j = 0; j < 9; j++) {
				if(j == 3 || j == 6) {
					System.out.print(" ");
				}
				System.out.print(String.valueOf(sudokuArray[i][j]));
			}
			System.out.println();
		}
	/*	try {
		//	System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
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
		history.push(new Point(currentX, currentY));
		/*
		 * int i = 1; if (exist) { i = sudokuArray[currentY][currentX]; i++; }
		 */
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
		
		if (history.size() == 1) {
		//	System.out.println("hello");
		}

		Point previousPoint = history.peek();
		currentX = previousPoint.x;
		currentY = previousPoint.y;
		
		int temp = sudokuArray[currentY][currentX];
		sudokuArray[currentY][currentX] = 0;
		
		updateMap(false, temp);
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
		updateMap(true, k);
		sudokuArray[currentY][currentX] = k;
		Point nextPoint = nextAvailableCell();

		if (nextPoint == null) {
			return; // succesful
		}
		currentX = nextPoint.x;
		currentY = nextPoint.y;
		//printMap();
		solve(false);
	}

	public Point nextAvailableCell() {
		int nextCurrentX = currentX;
		int nextCurrentY = currentY;
		nextCurrentX++;

		for (; nextCurrentY < 9; nextCurrentY++) {
			for (nextCurrentX = 0; nextCurrentX < 9; nextCurrentX++) {
				if (sudokuArray[nextCurrentY][nextCurrentX] == 0)
					return new Point(nextCurrentX, nextCurrentY);
			}
		}

		return null;
	}

	public boolean isValid(int x, int y, int k) {
		return isValidInColumn(x, k) && isValidInRow(y, k)
				&& isValidInBigCell(x, y, k);
	}

	public boolean isValidInColumn(int x, int k) {
		for (int y = 0; y < 9; y++) {
			if (sudokuArray[y][x] == k)
				return false;
		}
		return true;
	}

	public boolean isValidInRow(int y, int k) {
		for (int x = 0; x < 9; x++) {
			if (sudokuArray[y][x] == k)
				return false;
		}
		return true;
	}

	public boolean isValidInBigCell(int x, int y, int k) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (sudokuArray[((y / 3) * 3) + i][((x / 3) * 3) + j] == k)
					return false;
			}
		}
		return true;
	}

	public void updateMap(boolean forward, int k) {
		if (forward) {
			// colonne
			for (int i = (currentY + 1); i < 9; i++) {
				if (validPositions.containsKey(new Point(i, currentX))) {
					validPositions.get(new Point(i, currentX)).remove(
							new Integer(k));
				}
			}

			// ligne
			for (int i = (currentX + 1); i < 9; i++) {
				if (validPositions.containsKey(new Point(currentY, i))) {
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
		} else {
			for (int i = (currentY + 1); i < 9; i++) {
				if (validPositions.containsKey(new Point(i, currentX))) {
					if(isValid(currentX,i,k))
					validPositions.get(new Point(i, currentX)).add(
							new Integer(k));
				}
			}

			for (int i = (currentX + 1); i < 9; i++) {
				if (validPositions.containsKey(new Point(currentY, i))) {
					if(isValid(i,currentY,k))
					validPositions.get(new Point(currentY, i)).add(
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
						if (!validPositions.get(pointInGrid).contains(
								new Integer(k)))
							if(isValid(j,i,k))
							validPositions.get(pointInGrid).add(new Integer(k));
					

					}

				}
				j = (rx - 1) * 3;
			}
		}
	}
}

/*
 * long startTime = System.nanoTime(); System.out.println(); long endTime =
 * System.nanoTime();
 * 
 * long duration = (endTime - startTime); //divide by 1000000 to get
 * milliseconds.
 */