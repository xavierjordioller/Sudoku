
public class StartUp {

	public static void main(String[] args) {
		long millis = System.currentTimeMillis();
		
		Sudoku sudoku = new Sudoku("C://Users//xavie//Desktop//Montreal//ÉTS//LOG320_SDA//TP2//sudoku1.sud");
		sudoku.solve();
		
		System.out.println((System.currentTimeMillis() - millis)/1000.0) ;
		
		System.out.println(sudoku);
	}

}
