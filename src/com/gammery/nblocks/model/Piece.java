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

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import com.gammery.nblocks.view.BlockType;
import com.gammery.nblocks.view.BlockFactory;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import javax.swing.*;

public class Piece implements Cloneable
{
	/* XXX
	 * Esto que viene aca es experimental
	 * (para poder probar FxBoard)
	 */
	
	private int pxPosX;
	private int pxPosY;

	public void setPixelPositionX(int newValue) {
		pxPosX = newValue;
	}
	public int getPixelPositionX() {
		return pxPosX;
	}

	public void setPixelPositionY(int newValue) {
		pxPosY = newValue;
	}
	public int getPixelPositionY() {
		return pxPosY;
	}

	private int pxMirrorPosX;
	private int pxMirrorPosY;
		
	public void setMirrorPixelPositionX(int newValue) {
		pxMirrorPosX = newValue;
	}
	public int getMirrorPixelPositionX() {
		return pxMirrorPosX;
	}

	public void setMirrorPixelPositionY(int newValue) {
		pxMirrorPosY = newValue;
	}
	public int getMirrorPixelPositionY() {
		return pxMirrorPosY;
	}

	private BufferedImage imgMirrorPiece;
	private BlockType bTypeMirrorPiece;

	/////////////////////////////////////





	private int posY = -1;
	private int offset = -1;
	private int[][] block;

	private int topRowTouchable = -1;
	private int bottomRowTouchable = -1;
	private int leftColumnTouchable = -1;
	private int rightColumnTouchable = -1;

	// para la parte grafica
	private Color color;
	private BufferedImage imgPiece;
	private BlockType blockType;

	public void printInfo() {
		System.out.println("posY: " + posY);
		System.out.println("posX: " + offset);
		
		System.out.println("topRow: " + topRowTouchable);
		System.out.println("bottomRow: " + bottomRowTouchable);
		System.out.println("leftColumn: " + leftColumnTouchable);
		System.out.println("RightColumn: " + rightColumnTouchable);
	}
	
	public Piece(Color c) {
		color = c;
	}

	//FIXME: Deberia usar el metodo System.arraycopy()
	//Pero creo que no sirve para multiples dimensiones
	//TODO: testear si funca directamente sino utilizar 4 veces
	public Object clone() {
		Object obj = null;
		try {
			obj = super.clone();
			int[][] b = new int[4][4];
			for (int i = 0; i < b.length; i++) {
				for (int j = 0; j < b[0].length; j++)
					b[i][j] = block[i][j];
			}
			((Piece)obj).setBlock(b);
//			((Piece)obj).setBottomRowTouchable();
//			((Piece)obj).setLeftColumnTouchable();
//			((Piece)obj).setRightColumnTouchable();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public void setBlock(int[][] block)	{
		this.block = block;
		// Los metodos estan planteados para hacer uso de Lazy
		// Initialization, por lo que no deberia hacer esto aca
		// setBottomRowTouchable();
		// setLeftColumnTouchable();
		// setRightColumnTouchable();
	}

	public int getBlockHeight() {
		return block.length;
	}

	public int getBlockWidth() {
		return block[0].length;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int y) {
		posY = y;
	}

	public int getPosX() {
		return offset;
	}

	public void setPosX(int x) {
		offset = x;
	}

	public boolean isTangible(int x, int y)	{
		if (x >= block.length || y >= block[0].length)	// TODO Este bound-check lo deberia implementar en todos lo metodos de este tipo (ie:isSlotOcuppy)
			return false;								// Aunque por otro lado, este check me dificultaria encontrar bugs en otros lados
														// porque oculta los NullPointerExceptions
		return (block[x][y] == 0 ? false : true);
	}

	// estos metodos usan lazy initialization por lo que no tengo que
	// molestarme en llamar al metodo set primero... Puedo direc-
	// tamente llamar a estos gets
	public int getBottomRowTouchable() {
		if (bottomRowTouchable != -1)
			return bottomRowTouchable;
	
		setBottomRowTouchable();
		return bottomRowTouchable;
	}

	private void setBottomRowTouchable() {
		// Aca busco la primer fila (desde abajo) que tenga un 1.
		// El  nro de fila queda seteado en bottomRowTouchable.
		int blockWidth = getBlockWidth();
		int blockHeight = getBlockHeight();

		int rowTouchable = 0;
		out:
		for (int i = blockHeight-1; i >= 0; i--){
			for (int j = 0; j < blockWidth; j++){
				if (isTangible(i, j)){
					rowTouchable = i;
					break out;
				}
			}
		}

		bottomRowTouchable = rowTouchable;
	}


	public int getTopRowTouchable() {
		if (topRowTouchable != -1)
			return topRowTouchable;
	
		setTopRowTouchable();
		return topRowTouchable;
	}

	private void setTopRowTouchable() {
		// Aca busco la primer fila (desde arriba) que tenga un 1.
		// El nro de fila queda seteado en topRowTouchable.
		int blockWidth = getBlockWidth();
		int blockHeight = getBlockHeight();

		int rowTouchable = 0;
		out:
		for (int i = 0; i < blockHeight; i++){
			for (int j = 0; j < blockWidth; j++){
				if (isTangible(i, j)){
					rowTouchable = i;
					break out;
				}
			}
		}

		topRowTouchable = rowTouchable;
	}



	public int getLeftColumnTouchable() {
		if (leftColumnTouchable != -1)
			return leftColumnTouchable;

		setLeftColumnTouchable();
		return leftColumnTouchable;
	}

	private void setLeftColumnTouchable() {
		// Aca busco la primer columna (de izq a dcha) que tenga un 1
		// El  nro de columna queda seteado en leftColumnTouchable.
		int blockWidth = getBlockWidth();
		int blockHeight = getBlockHeight();

		int colTouchable = 0;
		out:
		for (int i = 0; i < blockWidth; i++){
			for (int j = 0; j < blockHeight; j++){
				if (isTangible(j, i)){
					colTouchable = i;
					break out;
				}
			}
		}
	
		leftColumnTouchable = colTouchable;
	}

	public int getRightColumnTouchable() {
		if (rightColumnTouchable != -1)
			return rightColumnTouchable;

		setRightColumnTouchable();
		return rightColumnTouchable;
	}

	private void setRightColumnTouchable() {
		// Aca busco la primer columna (de dcha a izq) que tenga un 1
		// El  nro de columna queda seteado en rightColumnTouchable.
		int blockWidth = getBlockWidth();
		int blockHeight = getBlockHeight();

		int colTouchable = 0;
		out:
		for (int i = blockWidth-1; i >= 0; i--){
			for (int j = 0; j < blockHeight; j++){
				if (isTangible(j, i)){
					colTouchable = i;
					break out;
				}
			}
		}

		rightColumnTouchable = colTouchable;
	}

	// Pegarle una ojeada a esto... tengo sueño, pero me parece que se puede optimizar!
	public void rotate() {
		int blockWidth = getBlockWidth();
		int blockHeight = getBlockHeight();
		// tal vez no sea necesario.
		int[][] tmpBlock = new int[blockHeight][blockWidth];	

		for (int i = 0; i < blockHeight; i++){
			for (int j = 0; j < blockWidth; j++){
				tmpBlock[j][blockHeight-i-1] = block[i][j];
			}
		}

		for (int i = 0; i < blockHeight; i++){
			for (int j = 0; j < blockWidth; j++){
				block[i][j] = tmpBlock[i][j];
			}
		}

		setTopRowTouchable();
		setBottomRowTouchable();
		setLeftColumnTouchable();
		setRightColumnTouchable();

		if (imgPiece != null) {
			// TODO
			// Usar rotation con affinetransformation para mejorar la performance
			imgPiece = makePiece(blockType, imgPiece.getWidth() / getBlockWidth(), imgPiece.getHeight() / getBlockHeight());
		}



		if (imgMirrorPiece != null) {
			// TODO
			// Usar rotation con affinetransformation para mejorar la performance
			imgMirrorPiece = makePiece(bTypeMirrorPiece, imgMirrorPiece.getWidth() / getBlockWidth(), imgMirrorPiece.getHeight() / getBlockHeight());
		}


	}	
/*
	// Pegarle una ojeada a esto... tengo sueño, pero me parece que se puede optimizar!
	public void rotateRight()
	{
		int blockWidth = getBlockWidth();
		int blockHeight = getBlockHeight();
		// tal vez no sea necesario
		int[][] tmpBlock = new int[blockHeight][blockWidth];	

		for (int i = 0; i < blockHeight; i++){
			for (int j = 0; j < blockWidth; j++){
				tmpBlock[blockHeight-j-1][i] = block[i][j];
			}
		}

		for (int i = 0; i < blockHeight; i++){
			for (int j = 0; j < blockWidth; j++){
				block[i][j] = tmpBlock[i][j];
			}
		}

		setBottomRowTouchable();
		setLeftColumnTouchable();
		setRightColumnTouchable();
	}	
*/
	public void printBlock() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print(" " + block[i][j] + " ");
			}
			System.out.println("");
		}
	}

	public Color getColor() {
		return color;
	}

	public void paintMirrorPiece(Graphics g, BlockType bType, int pxBlockSize, float transparency) {//Composite composite) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform at = g2d.getTransform();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
		g2d.translate(getMirrorPixelPositionX(), getMirrorPixelPositionY());
		if (imgMirrorPiece == null || bTypeMirrorPiece != bType || imgMirrorPiece.getWidth() / getBlockWidth() != pxBlockSize) {
			bTypeMirrorPiece = bType;
			imgMirrorPiece = makePiece(bTypeMirrorPiece, pxBlockSize);
		}

		g2d.drawImage(imgMirrorPiece, 0, 0, null);
		g2d.setTransform(at);
	}


	public void paintMySelf(Graphics g, BlockType bType, int pxBlockSize) {
		paintMySelf(g, bType, pxBlockSize, pxBlockSize);
	}


	// Este esquema no soporta resizes de las imagenes pieces... 
	public void paintMySelf(Graphics g, BlockType bType, int width, int height)
	{	//TODO: tal vez podria usar una interfaz Painter o algo asi que declare paintMySelf
		// y con eso hago que J2DBoard solo interactue con un Painter evitando que modifique cosas que no debe (como rotar una Piece o algo parecido)
		int blockSize = width;
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform at = g2d.getTransform();
		g2d.translate(getPixelPositionX(), getPixelPositionY());
		//FIXME Solo verifico el width (el height no) pero se supone que las celdas son cuadradas
		if (imgPiece == null || blockType != bType || imgPiece.getWidth() / getBlockWidth() != blockSize) {
			blockType = bType;
//			imgPiece = makePiece(blockType, width, height);
			imgPiece = makePiece(blockType, blockSize);
		}

		g2d.drawImage(imgPiece, 0, 0, null);
		g2d.setTransform(at);
	}

	private BufferedImage makePiece(BlockType bType, int pxBlockSize) {
		return makePiece(bType, pxBlockSize, pxBlockSize);
	}

	private BufferedImage makePiece(BlockType bType, int width, int height) {
		BufferedImage imgBlock = BlockFactory.getImageBlock(getColor(), bType, width, height);
		BufferedImage imgPiece = new BufferedImage(width * getBlockWidth(), height * getBlockHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) imgPiece.getGraphics();

		for (int i = 0; i < getBlockHeight(); i++) {
			for (int j = 0; j < getBlockWidth(); j++)
				if (isTangible(i, j)) {
					g2d.drawImage(imgBlock, j * width, i * height, width, height, null);
				}
		}

		return imgPiece;
	}
}
