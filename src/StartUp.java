import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class StartUp {
	public static void main(String[] args) {
		// for some reason, the first iteration is really slow...
		// tried to run the solve 1000 times to find out it's really faster
		// than I expected
		Sudoku sudoku = new Sudoku();

		int[][] firstIteration = new int[9][9];

		for (int[] row: firstIteration)
		    Arrays.fill(row, 0);
		
		sudoku.solve(firstIteration, sudoku.generateValidPosition(firstIteration));
		
		
		sudoku.reset();
		int[][] sudokuArray = sudoku.readBoard("C:/Users/Mathieu/Documents/testing/sudoku.txt");
		sudoku.startTimer();
		sudoku.solve(sudokuArray, sudoku.generateValidPosition(sudokuArray));
		BigDecimal duration = new BigDecimal(sudoku.getDuration());
		duration = duration.setScale(6, RoundingMode.CEILING);
		if(sudoku.getFinalMap() == null) {
			System.out.println("There is no solution for this sudoku. It took " + duration.toString() + " seconds");
		} else {
			System.out.println("It took " + duration.toString() + " seconds to resolve this sudoku");
			sudoku.printMap(sudoku.getFinalMap());
		}
		
	}
}
