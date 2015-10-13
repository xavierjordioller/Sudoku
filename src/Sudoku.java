import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;


public class Sudoku {
	Integer[][] sudokuArray = new Integer[9][9];
	Stack<Point> history = new Stack<Point>();
	int currentX = 0;
	int currentY = 0;
	long startTime;
	

	public Sudoku(String path) {
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
		    int y = 0;
			for(String line; (line = br.readLine()) != null; ) {
		    	char[] characters = line.toCharArray();
		    	for(int x = 0; x < characters.length; x++) {
		    		sudokuArray[y][x] = Integer.parseInt(String.valueOf(characters[x]));
		    	}
		    	y++;
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	 startTime = System.nanoTime();
	}
	
	// check if first cell is a 0
	
	public void solve(boolean exist) {
		history.push(new Point(currentX, currentY));
		int i = 1;
		if(exist) {
			i = sudokuArray[currentY][currentX];
			i++;
		}
		for(; i < 10; i++) {
			if(isValid(currentX, currentY, i)) {
				forward(i);
			}
		}
		backtrack();
	}
	
	public void backtrack() {
		sudokuArray[currentY][currentX] = 0;
		if(history.empty()) {
			return; // not able to solve it
		}
		history.pop();
	/*	if(history.size() == 0) {
			System.out.println("hello");
		}*/
		Point previousPoint = history.peek();
		currentX = previousPoint.x;
		currentY = previousPoint.y;
	}
	
	public void forward(int k) {
		
		if(currentX == 8 && currentY == 8)
		{
			long endTime = System.nanoTime();
			double duration = (endTime - startTime) / 1000000000.0;  //divide by 1000000 to get milliseconds.
			System.out.println(duration);
			System.out.println("Found it");
			
		}
		sudokuArray[currentY][currentX] = k;
		Point nextPoint = nextAvailableCell();
		
		if(nextPoint == null) {
			return; // succesful
		}
		currentX = nextPoint.x;
		currentY = nextPoint.y;
		solve(false);
	}
	
	public Point nextAvailableCell() {
		int nextCurrentX = currentX;
		int nextCurrentY = currentY;
		nextCurrentX++;
		
		for(;nextCurrentY < 9;nextCurrentY++) {
			for(nextCurrentX = 0;nextCurrentX < 9;nextCurrentX++) {
				if(sudokuArray[nextCurrentY][nextCurrentX] == 0) return new Point(nextCurrentX, nextCurrentY);
			}
		}
		
		return null;
	}
	
	public boolean isValid(int x, int y, int k) {
		return isValidInColumn(x, k) && isValidInRow(y, k) && isValidInBigCell(x, y, k);
	}
	
	public boolean isValidInColumn(int x, int k) {
		for(int y = 0; y < 9; y++) {
			if(sudokuArray[y][x] == k) return false;
		}
		return true;
	}
	
	public boolean isValidInRow(int y, int k) {
		for(int x = 0; x < 9; x++) {
			if(sudokuArray[y][x] == k) return false;
		}
		return true;
	}
	
	public boolean isValidInBigCell(int x, int y, int k) {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(sudokuArray[((y/3) * 3 ) + i][((x/3) * 3 ) + j] == k) return false;
			}
		}
		return true;
	}
}


/*
long startTime = System.nanoTime();
System.out.println();
long endTime = System.nanoTime();

long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
*/