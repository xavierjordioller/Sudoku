public class StartUp {
	public static void main(String[] args) {
		Sudoku sudoku = new Sudoku();
		sudoku.startTimer();
		/*sudoku.detectHiddenSingles(sudoku.generateValidPosition());
		
		sudoku.lockedCandidate(sudoku.generateValidPosition());
		sudoku.onlyOnePossibility(sudoku.generateValidPosition());*/
		
		
		
		// not sure if it's useful right now to use lockedCandidate since we probably need to rewrite how hypothesis
		// work because it generate it each time.
		int[][] sudokuArray = sudoku.readBoard("C:/Users/Mathieu/Documents/testing/sudoku.txt");
		sudoku.solve(sudokuArray, false, sudoku.generateValidPosition(sudokuArray));
	}
}
