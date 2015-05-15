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

package com.gammery.nblocks.view;

import com.gammery.nblocks.model.*;

public interface BoardDrawer
{	
	public void setBoardWidth(int width);

	// Dibuja y establece un nuevo tetrimino en el renderer. Hasta 
	// que se invoque a pieceTouchGround todos los comandos (move, 
	// drop, etc.) hacen referencia al tetrimino 'p'.
	public void drawNewPiece(Piece p);
	public void movePieceHorizontallyTo(int x, int y);
	public void movePieceDownTo(int x, int y);
	public void dropPieceTo(int x, int y);	// este metodo permitiria agregar un sonido para los drop
	public void rotatePiece();
	public void clearLines(int lines[], int rowsErased);
	public void drawMirrorPiece(int posX, int posY);
	public void hideMirrorPiece(int posX, int posY);	//FIXME		// tiene sentido eso???

	// El tetrimino que actualmente esta en juego deja de estarlo
	// y se dibuja permanentemente en el tablero grafico
	public void pieceTouchGround(int posX, int posY);
	public void changePauseStatus();
	public void gameLost();
	public void gameWon();
	public void reset();

	// Invocacion opcional: se debe invocar despues de subir un level
	public void levelUp();

	// Debe ser invocado antes de iniciar una nueva partida
	// Probablemente pueda generar una animacion: "3, 2, 1, GO!" (Bloqueante)
	public void startNewGame();

	// XXX este no se si es necesario... Lo estoy usando en Board.generateGarbageLines pero creo que al pedo
	public void updateBoard(Board board);

	// Cambia el background del tablero (decidi no usarlo mas)
//	public void setBackground(String img);
	public void setGraphicsSettings(GraphicsSettings settings, Board board);
}
