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

//TODO
//arreglar el cleanPanel (porq borra el fondo tambien)
//arreglar el PieceFactory, porq algo esta mal en los metodos
//de esa clase

// agregar un metodo setHighScore

package com.gammery.nblocks.view;

import javax.swing.*;
import java.awt.*;
import com.gammery.nblocks.model.*;
//import java.util.*;

public class NextPiecesPanel extends JPanel
{
	private PieceFrame[] fNextPieces;

	private int framesUsed;
	private int frameWidth;
	private int frameHeight;	
	private Color backgroundColor;
	private BlockType blockType;
	public static final int MAX_PANELS = 5;

	public NextPiecesPanel() {
		this(Color.BLACK, MAX_PANELS, 120, 120);
	}

	//TODO Podria recibir un Color[] para usar distintos colores para cada frame
	public NextPiecesPanel(Color bgColor, int frames, int fWidth, int fHeight) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		frameWidth = fWidth;
		frameHeight = fHeight;
		backgroundColor = bgColor;
		framesUsed = (frames < 1) ? 1 : frames;
		fNextPieces = new PieceFrame[frames];				//PanelPiece[]

		fNextPieces[0] = new PieceFrame(BlockType.GRADIENT_BLOCK, Color.RED, backgroundColor, frameWidth, frameHeight);
		add(fNextPieces[0]);
		for (int i = 1; i < fNextPieces.length; i++) {
			//add(Box.createRigidArea(new Dimension(0, 8)));
			fNextPieces[i] = new PieceFrame(frameWidth, frameHeight);
			add(fNextPieces[i]);
		}
		framesUsed = frames;		
	}

	public void setBlockType(BlockType bType) {
		blockType = bType;
		for (int i = 0; i < fNextPieces.length; i++) {	// tiene que ser a todos por mas que no se esten usando en este momento
			fNextPieces[i].setBlockType(blockType);		// para que cuando se habiliten otros tengan todos el mismo bType
		}
	}
/*
	public void setFrameColors(Color frameColor, Color bgColor) {
		// this.frameColor = frameColor;
		// this.bgColor = bgColor;
		for (int i = 0; i < framesUsed; i++) {
			fNextPieces[i].setFrameColors(frameColor, bgColor);
		}
	}
*/
	public int getFramesUsed() {
		return framesUsed;
	}

	public void setFramesUsed(int newFramesUsed) {
		if (newFramesUsed > fNextPieces.length)
			newFramesUsed = fNextPieces.length;
		else if (newFramesUsed < 0) 
			newFramesUsed = 0;
	
		if (newFramesUsed < framesUsed) {
			for (int i = 0; (newFramesUsed + i) < framesUsed; i++)
				fNextPieces[newFramesUsed + i].drawPiece(null);
		}

		framesUsed = newFramesUsed;
//		repaint();			// XXX 		ES NECESARIO?????
	}


					//FIXME		Esto tiene que verificar el length del array o a framesUsed???????????????????
														
	// es mejor usar foreach aca y Collection, o sea este metdo
	//public void displayNextPiece(Collection<Piece> nextPieces)	// nextPieces -> pieces
	public void displayNextPiece(java.util.List<Piece> pieces)
	{
//		int i = 0;
//		for (Piece piece : nextPieces) {
//			if (i >= fNextPieces.length) 
//				break;
//			fNextPieces[i++].drawPiece(piece, blockType);
//		}
		for (int i = 0; i < framesUsed; i++) {
			fNextPieces[i].drawPiece(pieces.get(i));
		}
	}
/*
 *  Alternativa usando iterator
	public void displayNextPiece(Iterator<Piece> it)
	{
		int i = 0;
		while (i++ < fNextPiece.length && it.hasNext()) {
			fNextPiece[i++].drawPiece(it.next());
		}
	}
*/	
	public void cleanFrames()
	{
		for (int i = 0; i < fNextPieces.length; i++) {
			fNextPieces[i].clean();
		}
	}
	
	// Testing method
	public static void main(String args[])
	{
		NextPiecesPanel gp = new NextPiecesPanel();
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.setLayout(new FlowLayout());
		f.add(gp);
		f.setSize(800, 800);

		PieceFactory pFactory = PieceFactory.getInstance();
		
		try {
			for (int i = 0; i < 16; i++) {
				int framesUsed = gp.getFramesUsed();
				gp.displayNextPiece(pFactory.next(framesUsed));
				Thread.sleep(800);
				pFactory.next();
				gp.cleanFrames();
				Thread.sleep(800);
			}
		}
		catch (Exception e) { }
	}
}
