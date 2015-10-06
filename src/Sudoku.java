import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class Sudoku {
	Integer[][] sudokuArray = new Integer[9][9];

	Stack<Square> history = new Stack<Square>();

	Square currentSquare = new Square(0,0);
	
	int currentK;
	
	public Sudoku(String path) {
		// Lecture du fichier en entree
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
		
		// Initialisation des attributs de classe
		currentK = 1;
		history.push(new Square(currentSquare.getX(),currentSquare.getY()));
	}
	
	private boolean isComplete() {
		Square nextSquare = currentSquare;
		
		int nextCurrentX = currentSquare.getX();
		int nextCurrentY = currentSquare.getY();
		nextCurrentY++;
		
		for(;nextCurrentX < 9;nextCurrentX++) {
			for(nextCurrentY = 0;nextCurrentY < 9;nextCurrentY++) {
				if(sudokuArray[nextCurrentX][nextCurrentY] == 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	public Square getNextSquare() {
		Square nextSquare = currentSquare;
		
		int nextCurrentX = currentSquare.getX();
		int nextCurrentY = currentSquare.getY();
		nextCurrentY++;
		
		for(;nextCurrentX < 9;nextCurrentX++) {
			for(nextCurrentY = 0;nextCurrentY < 9;nextCurrentY++) {
				if(sudokuArray[nextCurrentX][nextCurrentY] == 0) {
					nextSquare.setX(nextCurrentX);
					nextSquare.setY(nextCurrentY);
					return nextSquare;
				}
			}
		}
		return nextSquare;
	}
	
	private void setNextSquare() {
		currentSquare = getNextSquare();
	}
	
	public void setPreviousSquare() {
		currentSquare = history.pop();
	}
	
	public void setPreviousSquareNumber() {
		currentK = sudokuArray[currentSquare.getX()][currentSquare.getY()] + 1;
	}
	
	private boolean isValid() {
		return isValidColumn() && isValidRow() && isValidRegion();
	}
	
	private boolean isValidColumn() {
		for(int x = 0; x < 9; x++) {
			if(sudokuArray[x][currentSquare.getY()] == currentK)
				return false;
		}
		return true;
	}

	private boolean isValidRow() {
		for(int y = 0; y < 9; y++) {
			if(sudokuArray[currentSquare.getX()][y] == currentK)
				return false;
		}
		return true;
	}
	
	private boolean isValidRegion() {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(sudokuArray[((currentSquare.getX()/3) * 3 ) + i][((currentSquare.getY()/3) * 3 ) + j] == currentK)
					return false;
			}
		}
		return true;
	}



	/**
	 * Methode recursive de resolution du Sudoku
	 * 
	 * @return
	 */
	public boolean solve() {
				
		// 1ere condition d'arret : La solution est complete
		if (isComplete()) {
			return true;
		}
		
		// 2eme condition d'arret : On n'a pas de solution
		else if(history.isEmpty()) {
			return false;
		}
		
		// Si aucun k ne peut etre place dans la caise courrante, on reviens en arriere
		else if (currentK > 9){
			sudokuArray[currentSquare.getX()][currentSquare.getY()] = 0;
			setPreviousSquare();
			setPreviousSquareNumber();
			return solve();
		}
		
		// Si currentK peut etre place dans la caise courrante, on passe a la caise suivante
		else if (isValid()) {
			sudokuArray[currentSquare.getX()][currentSquare.getY()] = currentK;
			history.push(new Square(currentSquare.getX(),currentSquare.getY()));
			setNextSquare();
			currentK = 1;
			return solve();
		}
		
		// Si currentK < 9  ne peut pas etre place dans la caise courrante, on essaie avec currentK++
		else {
			currentK++;
			return solve();
		}
		
	}








}
