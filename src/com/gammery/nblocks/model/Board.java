/* 
 * NBlocks, Copyright (C) 2011  Matías E. Vazquez (matiasevqz@gmail.com)  
      
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License  
 * as published by the Free Software Foundation; either version 2  
 * of the License, or (at your option) any later version.  
      
 * This program is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU General Public License for more details.  
      
 * You should have received a copy of the GNU General Public License  
 * along with this program; if not, write to the Free Software  
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.  
 */

package com.gammery.nblocks.model;

import java.util.Random;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
										import javax.swing.*;
										import java.util.concurrent.*;
import com.gammery.nblocks.view.BoardDrawer;
import com.gammery.nblocks.view.BlockFactory;
import com.gammery.nblocks.view.BlockType;
	

//TODO
//Probablemente el KICK WALL deberia funcionar con el 'techo' del board. Aunque 
//tal vez sea mejor que ignore el limite superior y se "dibuje" la pieza 
//parcialmente (ver como es esto en otros tetris)
//Alguna implementaciones de tetris no dibujan la pieza pegada al techo por lo 
//que tiene espacio para moverse hacia arriba...

//Si canMoveDonw recibe el argumento entonces para calcular el mirror 
//podria crear un clone y bajarlo hasta lo que mas se pueda y queda mas legible.	
//Lo mismo para el drop()...

//El metodo draw se deberia llamar lockDown
//riseUpBoard se deberia llamar liftUpLines

//Esta clase no debe asignar trabajos al EDT. Eso le concierne a la implementacion
//de BoardDrawer

//Hay demasiados ctors que nunca voy a usar...


/*
 * Esta clase maneja la logica del desplazamiento y colisiones de los tetriminos
 * que se ubican en el tablero. Tambien mantiene el estado logico del tablero, es
 * decir, los bloques que ya se encuentran en el espacio del juego. Esta clase es
 * el backend de la implementacion de BoardDrawer.
 */

public class Board
{
	private volatile Piece currentPiece;
	private Color[][] board;
	private BoardDrawer bDrawer;
	private boolean showMirror;
	private boolean wallKickEnabled;

/*
	public Board(BoardDrawer bd, GameplaySettings settings) {
		this(bd, settings.getBoardWidth(), settings.getBoardHeight(),
			settings.isMirrorPieceEnabled(), settings.getInitGarbageLines(),
			settings.isKickWallEnabled());
	}
*/
	public Board(BoardDrawer bd, int width, int height)	{
		this(bd, width, height, false, 0);
	}
	public Board(BoardDrawer bd, int width, int height, boolean mirror)	{
		this(bd, width, height, mirror, 0);
	}
	public Board(BoardDrawer bd, int width, int height, boolean mirror, int trashLines)	{
		this(bd, width, height, mirror, 0, true);
	}
	public Board(BoardDrawer bd, int width, int height, boolean mirror, int trashLines, boolean wallKick)	{
		bDrawer = bd;
		board = new Color[height][width];
		showMirror = mirror;
		wallKickEnabled = wallKick;

		// board[][] tiene que tener todos sus slots "borrados", esto es, que sean iguales a null.
		// Como por defecto es asi no tengo que hacer nada, pero si algun dia cambio el
		// valor que identifica a un slot vacio (null) debo tener en cuenta eso.
		// En ese caso podria usar el metodo fill de la clase Array
		if (trashLines > 0)
			generateGarbageLines(trashLines);
	}

	// XXX Creo que no es necesario el synchronized
	synchronized
	public void setWallKickEnabled(boolean newValue) {
		wallKickEnabled = newValue;
	}

	synchronized 
	public void setShowMirror(boolean value)
	{
		showMirror = value;
		if (currentPiece == null)
			return;
//		final int posX = currentPiece.getPosX();		NO SON NECESARIOS (puedo sacarlos de currentPiece)
//		final int posY = currentPiece.getPosY();
//		if (showMirror)
//			SwingUtilities.invokeLater(
//				new Runnable() {	
//					public void run() {		// Fusionar los invokeLater MEPA que podria dar resultados incorrectos porq 'showMirror' podria llegar a cambiar
//						bDrawer.drawMirrorPiece(currentPiece.getPosX(), calculateMirrorPosY());	// cuando este run se ejecute...
//					}
//				}
//			);
//		else
//			SwingUtilities.invokeLater(
//				new Runnable() {
//					public void run() {
//						bDrawer.hideMirrorPiece(currentPiece.getPosX(), calculateMirrorPosY());
//					}
//				}
//			);
		if (showMirror)
			bDrawer.drawMirrorPiece(currentPiece.getPosX(), calculateMirrorPosY());
		else
			bDrawer.hideMirrorPiece(currentPiece.getPosX(), calculateMirrorPosY());
	}

	// esto se puede optimizar si asigno:
	// i = posY + bottomRowTouchable 
	//
	// esto se puede optimizar mucho mas si bajo todo
	// en un paso, pero no siempre se va a poder... igual se puede aplicar
	//
	//NOTA: por otro lado, yo solo deberia verificar que una linea este completa
	//solo 4 veces (o lo que este ocupando la pieza verticalmente) ...
//	synchronized		// XXX No se si es necesario...
//	Esto metodo ya no es synchronized porq si adquiere el lock y se invoca a un moveXXX 
//	la invocacion queda encolada y como consecuencia deja el EDT trabado


	/*
	 * Busca y elimina las lineas horizontales completas (y a medida que lo hace 
	 * desplaza hacia abajo todos los bloques que se encontraban arriba de la linea
	 * eliminada). Retorna la cantidad de lineas eliminadas.
	 */
	
	public int clearLines() {
		if (currentPiece != null) {
			System.out.println("         ------ ERRROR!!!!!!!!!!!!! (currentPiece != null)");
		}

//		System.out.println("Board.clearLines is EDT? : " + SwingUtilities.isEventDispatchThread());
		int counter;
		// se puede usar un arreglo mas chico si calculo el bottomRow con el TopRow
		// pero tengo que hacer mas calculos y el codigo se hace menos legible
		//int rowsToErase[] = new int[currentPiece.getBlockHeight()];
		//FIXME: Arreglar este 4 harcodeado!!! (no puedo usar currentPiece porq es null en este momento)
		//int rowsToErase[] = new int[4];
		int rowsToErase[] = new int[getBoardHeight()];
		int rowsErased = 0;
		int boardHeight = getBoardHeight();
		int boardWidth = getBoardWidth();

		
		for (int row = boardHeight-1; row >= 0; row--) {
			for (int col = counter = 0; col < boardWidth; col++) {
				if (isSlotOcuppy(row, col)) {
					if (++counter == boardWidth) {
						rowsToErase[rowsErased++] = row;
					}
				}	
			}
		}
		for (int i = 0; i < rowsErased; i++)
			clearLine(rowsToErase[i]+i);

		if (rowsErased > 0) {
//			System.out.println("rowsErased::" + rowsErased);
				bDrawer.clearLines(rowsToErase, rowsErased);		//XXX
//			final int[] fRowsToErase = new int[rowsToErase.length];
//			System.arraycopy(rowsToErase, 0, fRowsToErase, 0, rowsToErase.length);
//			final int fRowsErased = rowsErased;
//			try {
//				SwingUtilities.invokeAndWait( new Runnable() {
//					public void run() { bDrawer.clearLines(fRowsToErase, fRowsErased); }
//				});
//			} catch (Exception e) { }
		}
		return rowsErased;
	}

	// esto creo que se puede optimizar si en lugar de barrer por
	// columnas, barro por filas. Cuando encuentro una fila q este
	// con puros 0's me detengo.
	// Pero para "optimizar" deberia agregar una comparacion y un flag
	// y ahi creo que termina siendo mas ineficiente...
	//synchronized
	private void clearLine(int rowToErase)
	{
		int boardWidth = getBoardWidth();
		for (int col = 0; col < boardWidth; col++){
			for (int row = rowToErase; row > 0; row--){
				setBoardSlot(getBoardSlot(row-1, col), row, col);
			}
			eraseBoardSlot(0, col);	// esto es porq la primer fila (top) no puede tener otro valor que 0
		}							// al menos en este momento, 
	}

	private int getBoardHeight() {
		return board.length;
	}

	private int getBoardWidth() {
		return board[0].length;
	}

	private void setBoardSlot(Color color, int row, int col) {
		board[row][col] = color;
	}

	private void eraseBoardSlot(int row, int col) {
		board[row][col] = null;
	}

	private Color getBoardSlot(int row, int col) {
		return board[row][col];
	}

	private boolean isSlotOcuppy(int row, int col) { //isBoardSlotOcuppy
		return (board[row][col] == null ? false : true);
	}	

	private void centerPiecePosition(Piece p) {
		p.setPosX((getBoardWidth() / 2) - (p.getBlockWidth() / 2));
		p.setPosY(p.getTopRowTouchable() * (-1));
	}

	synchronized			//XXX no se si es ncesario
	public boolean drawNewPiece(Piece piece)
	{
		currentPiece = piece;
		centerPiecePosition(currentPiece);

		if (canBeDrawn(currentPiece)) {
			SwingUtilities.invokeLater(	new Runnable() {
				public void run() { bDrawer.drawNewPiece(currentPiece); }
			});
			if (showMirror)
				SwingUtilities.invokeLater(	new Runnable() {
						public void run() { bDrawer.drawMirrorPiece(currentPiece.getPosX(), calculateMirrorPosY()); }
				});
			return true;
		}

//		SwingUtilities.invokeLater(	new Runnable() {
//			public void run() { bDrawer.gameLost(); }
//		});
		return false;
	}

	/*
	 * Lockea (lock down) el tetrimino 'piece' en el tablero de manera 
	 * permanente en la posicion actual. Retorna true si la posicion
	 * actual no colisiona con otro block, es decir, si puede
	 * ser "dibujada". false en caso contrario.
	 */

	private boolean draw(Piece piece) {
		int blockHeight = piece.getBlockHeight();
		int blockWidth = piece.getBlockWidth();
		int posY = piece.getPosY();
		int posX = piece.getPosX();

		if (!canBeDrawn(piece))
			return false;
			
		for (int i = 0; i < blockHeight; i++){
			for (int j = 0; j < blockWidth; j++){
				if (piece.isTangible(i, j)){
					// el Board original hace un validacion extra aca
					// que creo no es necesaria, ya que lo valida arriba
					setBoardSlot(piece.getColor(), i+posY, j+posX);
				}
			}
		}
		return true;
	}

	/*
	 * Intenta mover el actual tetrimino en juego un lugar hacia abajo.
	 * Si se pudo mover retorna true, caso contrario false.
	 */

	synchronized
	public boolean moveDown() {
		if (currentPiece == null) {
			System.out.println("Board.moveDown(): returns null (currentPiece == null)");
			return false;
		}
		
		final int fposX = currentPiece.getPosX();
		final int fposY = currentPiece.getPosY();

		if (canMoveDown(currentPiece)){
			currentPiece.setPosY(currentPiece.getPosY() + 1); // currentPiece no es thread-safe pero siempre y haya acceso concurrente todo va funcar bien
			if (SwingUtilities.isEventDispatchThread())
				bDrawer.movePieceDownTo(fposX, fposY);
			else
				SwingUtilities.invokeLater(	new Runnable() {
					public void run() { bDrawer.movePieceDownTo(fposX, fposY); }			//XXX ATENTION: paso coord viejas
				});
			return true;
		}

		pieceTouchGround();
		return false;
// ALT OR:
/*
		if (SwingUtilities.isEventDispatchThread())
			System.out.println(">>>>>>>>>>>>>>>>>moveDown in EDT?? " + SwingUtilities.isEventDispatchThread());
		if (SwingUtilities.isEventDispatchThread())
			bDrawer.pieceTouchGround(fposX, fposY);
		else {
			try {
				SwingUtilities.invokeAndWait(	new Runnable() {
					public void run() { bDrawer.pieceTouchGround(fposX, fposY); }
				});
			} catch (Exception e) { }
		}

		draw(currentPiece);
		currentPiece = null;

		return false;*/
	}

	// OR lockDownCurrentPiece
	private void pieceTouchGround() {
		draw(currentPiece);
		final int fposX = currentPiece.getPosX();
		final int fposY = currentPiece.getPosY();
		if (SwingUtilities.isEventDispatchThread()) {
			System.out.println("Board.pieceTouchGround() inside EDT");
			bDrawer.pieceTouchGround(fposX, fposY);
		} else {
			try {		// FIXME aca se origino una exception
				SwingUtilities.invokeAndWait( new Runnable() {
					public void run() { bDrawer.pieceTouchGround(fposX, fposY); }
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		currentPiece = null;
	}

	/*
	 * Verifica si es posible mover el tetrimino 'piece' un lugar
	 * hacia abajo. Retorna true si es posible.
	 */

	private boolean canMoveDown(Piece piece) {
		int bottomRow = currentPiece.getBottomRowTouchable();
		int posY = currentPiece.getPosY();
		int posX = currentPiece.getPosX();
		
		if (posY + bottomRow + 1 >= getBoardHeight())
			return false;

		// Se recorre toda la matriz por columnas (de abajo a arriba)
		// empezando por la ultima fila que contiene un '1' 
		// (bottomRow). Cuando encuentra un '1' en una columna, 
		// verifica que la celda que se encuentra un casillero mas 
		// abajo en board no sea un '1'. Si hay un '1', entonces 
		// no puede bajar.
		int blockWidth = currentPiece.getBlockWidth();

		for (int i = 0; i < blockWidth; i++){
			for (int j = bottomRow; j >= 0; j--){
				if (currentPiece.isTangible(j, i)){
					if (isSlotOcuppy(j+posY+1, i+posX)){
						return false;
					}
					break;
				}
			}	
		} 

		return true;	
	}	

	/*
	 * Intenta mover el actual tetrimino en juego un lugar hacia la izquierda.
	 * Si es posible moverla, actualiza las coordenadas del tetrimino y notifica
	 * a la implementacion de BoardDrawer para que haga el renderizado.
	 */

	synchronized
	public void moveLeft() {
		if (currentPiece == null) {
			System.out.println("Board.moveLeft(): returns null (currentPiece == null)");
			return;
		}

		if (canMoveLeft(currentPiece)) {
			currentPiece.setPosX(currentPiece.getPosX() - 1);
			bDrawer.movePieceHorizontallyTo(currentPiece.getPosX(), currentPiece.getPosY());
			if (showMirror)
				bDrawer.drawMirrorPiece(currentPiece.getPosX(), calculateMirrorPosY());
		}
	}	

	private boolean canMoveLeft(Piece piece) {
		int leftColumn = piece.getLeftColumnTouchable();
		int posY = piece.getPosY();
		int posX = piece.getPosX();
		
		if (posX + leftColumn - 1 < 0)
			return false;

		// Se recorre toda la matriz por filas (de izq a dcha)
		// empezando por la primer columna que tiene un '1' (que contenga un bloque)
		// (leftColumn). Cuando encuentra un '1' en una fila, 
		// verifica que la celda que se encuentra un casillero a 
		// la izquierda en board no sea un '1'. Si hay un '1', 
		// entonces no puede moverse.
		int blockHeight = piece.getBlockHeight();
		int blockWidth = piece.getBlockWidth();

		// FIXME:
		// j deberia ser inicializada con leftColumn!!!!!!!!!!
		for (int i = 0; i < blockHeight; i++){
			for (int j = 0; j < blockWidth; j++){
				if (piece.isTangible(i, j)){
					if (isSlotOcuppy(i+posY, j+posX-1)){
						return false;
					}
					break;
				}
			}	
		} 

		return true;	
	}	

	/*
	 * Intenta mover el actual tetrimino en juego un lugar hacia la derecha.
	 * Si es posible moverla, actualiza las coordenadas del tetrimino y notifica
	 * a la implementacion de BoardDrawer para que haga el renderizado.
	 */

	synchronized
	public void moveRight() {
		if (currentPiece == null) {
			System.out.println("Board.moveRight(): returns null (currentPiece == null)");
			return;
		}

		if (canMoveRight(currentPiece)) {
			currentPiece.setPosX(currentPiece.getPosX() + 1);
			bDrawer.movePieceHorizontallyTo(currentPiece.getPosX(), currentPiece.getPosY());
			if (showMirror)
				bDrawer.drawMirrorPiece(currentPiece.getPosX(), calculateMirrorPosY());
		}
	}	

	private boolean canMoveRight(Piece piece) {
		int rightColumn = piece.getRightColumnTouchable();
		int posY = piece.getPosY();
		int posX = piece.getPosX();
		
		if (posX + rightColumn + 1 >= getBoardWidth())
			return false;

		// Se recorre toda la matriz por filas (de dcha a izq)
		// empezando por la ultima columna que contiene un '1' 
		// (rightColumn). Cuando encuentra un '1' en una fila, 
		// verifica que la celda que se encuentra un casillero a 
		// la derecha en board no sea un '1'. Si hay un '1', 
		// entonces no puede moverse.
		int blockHeight = piece.getBlockHeight();
		int blockWidth = piece.getBlockWidth();

		for (int i = 0; i < blockHeight; i++){
			for (int j = rightColumn; j >= 0; j--){
				if (piece.isTangible(i, j)){
					if (isSlotOcuppy(i+posY, j+posX+1)){
						return false;
					}
					break;
				}
			}
		}

		return true;	
	}	
/*
	// borra "piece" de la matriz board
	private void erase(Piece piece)
	{
		// Convierto los 1's de block que corresponden en posicion
		// con los 1's de board a 0's.
		
		int blockHeight = piece.getBlockHeight();
		int blockWidth = piece.getBlockWidth();
		int posY = piece.getPosY();
		int posX = piece.getPosX();

		for (int i = 0; i < blockHeight; i++){
			for (int j = 0; j < blockWidth; j++){
				if (piece.isTangible(i, j)){
					if (isSlotOcuppy(i+posY, j+posX)){
						eraseBoardSlot(i+posY, j+posX);
					}
				}
			}
		}
	}
*/

	/*
	 * Deja que el tetrimino caiga lo que mas pueda. Actualiza las 
	 * coordenadas del tetrimino y notifica a la implementacion de 
	 * BoardDrawer para que haga el renderizado.
	 */

	synchronized
	public void drop() {
		if (currentPiece == null) {
			System.out.println("Board.drop(): returns null (currentPiece == null)");
			return;
		}

		while (canMoveDown(currentPiece))
			currentPiece.setPosY(currentPiece.getPosY() + 1);

		bDrawer.dropPieceTo(currentPiece.getPosX(), currentPiece.getPosY());

		/*moveDown();*/	//	pieceTouchGround();
		// Este llamado no debe estar aca!!! Ya que quiero permitir drops sin lockDown
//		bDrawer.pieceTouchGround(currentPiece.getPosX(), currentPiece.getPosY());

													//FIXME Esto mismo esta en moveDown pero deberia estar unificado esto
//		draw(currentPiece);
//		currentPiece = null;
	}


	// podria usar un clon pero creo que no es necesario ya que todo ocurre secuencialmente
	private int calculateMirrorPosY() {
		// Muevo "virtualmente" la pieza en cada iteracion
		// hasta que no pueda bajar mas.
		int oldPosY = currentPiece.getPosY();
		while (canMoveDown(currentPiece))
			currentPiece.setPosY(currentPiece.getPosY() + 1);

		int newPosY = currentPiece.getPosY();

		currentPiece.setPosY(oldPosY);
		return newPosY;
	}

	// TODO
	// Deberia verificar el limite de bottom?
	private boolean canBeDrawn(Piece piece) {
		int blockHeight = piece.getBlockHeight();
		int blockWidth = piece.getBlockWidth();
		int posY = piece.getPosY();
		int posX = piece.getPosX();

		int leftColumn = piece.getLeftColumnTouchable();
		int rightColumn = piece.getRightColumnTouchable();
		int topColumn = piece.getTopRowTouchable();
		if ((posX + leftColumn < 0) || (posX + rightColumn >= getBoardWidth()) || (posY + topColumn < 0)) {
			System.out.println("Retorno false!!");
			return false;
		}

		for (int i = blockHeight-1; i >= 0; i--){
			for (int j = 0; j < blockWidth; j++){
				if (piece.isTangible(i, j)){
					if (i+posY < getBoardHeight() && j+posX < getBoardWidth()){
						if (isSlotOcuppy(i+posY, j+posX))
							return false;
					}
					else
						return false;
				}
			}
		}
		return true;
	}

	// Si funciona la clonacion, puedo borrar el rotateRight y usar solamente
	// el rotateLeft solamente en Piece
	synchronized
	public void rotatePiece() {
		if (currentPiece == null) {
			System.out.println("Board.rotatePiece(): returns null (currentPiece == null)");
			return;
		}

		boolean wallKicked = false;
		int wallKickedOff = 0;
		boolean pieceRotated = false;
		Piece pieceClone = (Piece)currentPiece.clone();
		pieceClone.rotate();
																		
		if (canBeDrawn(pieceClone)) {
			pieceRotated = true;
//			currentPiece.rotate();
		} else if (wallKickEnabled) {
			if (canMoveLeft(pieceClone)) {
				do {
					pieceClone.setPosX(pieceClone.getPosX() - 1);
					if (canBeDrawn(pieceClone)) {
						System.out.println("Kicked to Left!");
	//					currentPiece = pieceClone;
						pieceRotated = true;
						wallKickedOff = pieceClone.getPosX();
						wallKicked = true;
						// ALT: rotar, setar attrb
						break;
					}
				} while (canMoveLeft(pieceClone));
			} else if (canMoveRight(pieceClone)) {
				do {
					pieceClone.setPosX(pieceClone.getPosX() + 1);
					if (canBeDrawn(pieceClone)) {
						System.out.println("Kicked to Right!");
	//					currentPiece = pieceClone;
						pieceRotated = true;
						wallKickedOff = pieceClone.getPosX();
						wallKicked = true;
						// ALT: rotar, setar attrb
						break;
					}
				} while (canMoveRight(pieceClone));
			}
		}

		// para no tener que llamar a bDrawer de forma repetida
		if (pieceRotated) {
			if (wallKicked) 
				currentPiece.setPosX(wallKickedOff);
			currentPiece.rotate();
			bDrawer.rotatePiece();
			if (showMirror)
				bDrawer.drawMirrorPiece(currentPiece.getPosX(), calculateMirrorPosY());
			return;
		}

//		msg("Clone--");
//		pieceClone.printInfo();
//		pieceClone.printBlock();		
		
//		msg("\nCurrentPiece\n");
//		currentPiece.printInfo();
//		currentPiece.printBlock();		
//		msg("\n\n\n\n");
	}	

	/*
	 * Imprime en stdout el estado logico de la matriz
	 * que representa al board. Lo use al principio del
	 * desarrollo para testear y debugear.
	 */

	synchronized
	public void drawBoard() {
		System.out.println("\n\n\n\n");
		for (int i = 0; i < getBoardHeight(); i++){
			for (int j = 0; j < getBoardWidth(); j++)
				System.out.print(" " + (isSlotOcuppy(i, j)? "X":"0") + " ");
			System.out.println("");
		}
	}

//	NOTA: Es private porque la invocacion directa a este metodo una vez
//	iniciada la partida haria que todo se vaya al joraca.
//	Solo debe ser llamdo por el ctor o por riseUpBoard()
//  FIXME: El BoardDrawer no se actualiza correctamente cuando se llama en el ctor
//	Esto creo que se soluciona si llamo a updateBoard en riseUpBoard y no en este... Cuando
//	se llama desde el ctor todavia no se termina de construir
	private void generateGarbageLines(int lines) {//generateRandomTrashLines(int lines)
		// Just a precaution
		if (lines > getBoardHeight() / 2)
			lines = getBoardHeight() / 2;

		Random rand = new Random();
		PieceFactory pFactory = PieceFactory.getInstance();
		for (int row = 0; row < lines; row++) {
			for (int col = 0; col < getBoardWidth(); col++) {
				if (rand.nextBoolean()) {
					setBoardSlot(pFactory.getRandomColor(), getBoardHeight() - 1 - row, col);
				}
				else
					eraseBoardSlot(getBoardHeight() - 1 - row, col);
			}
			// Esto es un fix para prevenir que una linea quede completa
			eraseBoardSlot(getBoardHeight() - 1 - row, rand.nextInt(getBoardWidth()));
		}	
		bDrawer.updateBoard(this);
	}
	
	/*
	 * Eleva todos los bloques del board tantas lineas
	 * como lo indique el argumento 'lines'.
 	*/
	synchronized		//XXX Creo que no es necesario
	public void riseUpBoard(int lines)
	{
		/* Se cuentan las lineas libres que hay desde arriba
		 * Para saber cual es la maxima cantidad de lineas que 
		 * se pueden elevar los bloques	*/
		int topLinesAvailables = 0;
		out:
		for (int row = 0; row < getBoardHeight(); row++) {
			for (int col = 0; col < getBoardWidth(); col++) {
				if (isSlotOcuppy(row, col))	// or isSlotFree
					break out;
			}
			topLinesAvailables++;
		}

		if (topLinesAvailables < lines)
			lines = topLinesAvailables;

		/* Se sube cada bloque a 'lines' mas arriba */
		for (int col = 0; col < getBoardWidth(); col++) {
			for (int row = topLinesAvailables; row < getBoardHeight(); row++) {
				setBoardSlot(getBoardSlot(row, col), row-lines, col);
			}
		}

		generateGarbageLines(lines);	
	}

	/*
	 * Actualiza la grafica del board usando el BlockType que 
	 * se recibe como argumento para dibujar los bloques
	 * */

	synchronized //XXX Creo que si es necesario por el isSlotOcuppy pero no estoy sure
	public void paintMySelf(Graphics2D g2d, BlockType blockType, int blockWidth, int blockHeight) {
		for (int row = 0; row < getBoardHeight(); row++) {
			for (int col = 0; col < getBoardWidth(); col++) {
				if (isSlotOcuppy(row, col)) {
					BufferedImage imgBlock = BlockFactory.getImageBlock(getBoardSlot(row, col), blockType, blockWidth, blockHeight);
					g2d.drawImage(imgBlock, col * blockWidth, row * blockHeight, blockWidth, blockHeight, null);
				}
			}
		}
	}

	synchronized
	public void moveExtremeLeft() {
		if (currentPiece == null) {
			System.out.println("Board.moveExtremeLeft(): returns null (currentPiece == null)");
			return;
		}

		while (canMoveLeft(currentPiece))
			currentPiece.setPosX(currentPiece.getPosX() - 1);

		bDrawer.movePieceHorizontallyTo(currentPiece.getPosX(), currentPiece.getPosY());
		// o tambien
		//bDrawer.drawPiece(currentPiece);
		if (showMirror)
			bDrawer.drawMirrorPiece(currentPiece.getPosX(), calculateMirrorPosY());
	}

	synchronized
	public void moveExtremeRight() {
		if (currentPiece == null) {
			System.out.println("Board.moveExtremeRight(): returns null (currentPiece == null)");
			return;
		}

		while (canMoveRight(currentPiece))
			currentPiece.setPosX(currentPiece.getPosX() + 1);

		bDrawer.movePieceHorizontallyTo(currentPiece.getPosX(), currentPiece.getPosY());
		// o tambien
		//bDrawer.drawPiece(currentPiece);
		if (showMirror)
			bDrawer.drawMirrorPiece(currentPiece.getPosX(), calculateMirrorPosY());
	}
}
