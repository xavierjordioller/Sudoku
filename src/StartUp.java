public class StartUp {
	public static void main(String[] args) {
		Sudoku sudoku = new Sudoku("C:/Users/AK79880/testing/sudoku.txt");
		sudoku.generateValidPosition();
		sudoku.detectHiddenSingles();
		sudoku.onlyOnePossibility();
		//sudoku.lockedCandidate();
		
		// not sure if it's useful right now to use lockedCandidate since we probably need to rewrite how hypothesis
		// work because it generate it each time.
		sudoku.solve(false);
	}
}
