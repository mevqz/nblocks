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

//FIXME Creo qe tengo imports que no necesito...
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;

import com.gammery.nblocks.model.*;

														//FIXME No prevengo un NULL... para esto deberia tener valores por defecto...



				// TODO Tal vez deberia tener un metodo para setear el tamaño del bloque 
				

//public class PanelPiece extends JComponent
public class PieceFrame extends JComponent
{
	private Piece piece;
	private BlockType blockType;
	private BufferedImage backgroundImage;
	private Color colorBackground;
	private Color colorFrame;
	private int leftMargin;
	private int topMargin;
	private int imgBlockWidth;
	private int imgBlockHeight;
	private int panelWidth;
	private int panelHeight;

	public PieceFrame(int pWidth, int pHeight) {
		this(BlockType.PLAIN_BLOCK, Color.YELLOW, Color.BLACK, pWidth, pHeight);
	}

	public PieceFrame(BlockType bType, Color cFrame, Color cBG, int pWidth, int pHeight)
	{
		blockType = bType;
		colorFrame = cFrame;
		colorBackground = cBG;
		panelWidth = pWidth;
		panelHeight = pHeight;

		backgroundImage = createBackground(colorFrame, colorBackground);
	}

	private BufferedImage createBackground(Color frame, Color background)
	{
		BufferedImage bg = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bg.createGraphics();
		g.setColor(background);
		g.fillRect(0, 0, panelWidth, panelHeight);

		g.setColor(frame);
		float outline = 4.0f;					// FIXME No deberia usar numeros "magicos" (deberia darle un name con una constante)
		g.setStroke(new BasicStroke(outline));
		g.drawRect((int)outline / 2, (int)outline / 2, panelWidth - (int)outline, panelHeight - (int)outline);

		return bg;
	}

	@Override
	public Dimension getPreferredSize()	{
		return new Dimension(panelWidth, panelHeight);
	}

/*	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		if (backgroundImage != null)
			g2d.drawImage(backgroundImage, 0, 0, null);
		else {
			g2d.setColor(bgColor);		// FIXME este else no tiene sentido.....
			g2d.fillRect(0, 0, panelWidth, panelHeight);
		}
		if (piece != null) {
			g2d.translate(leftMargin+6, topMargin+6);				// FIXME NO USAR NUMEROS MAGICOS!!! Q XUXA es el 6???
			piece.paintMySelf(g, blockType, imgBlockWidth, imgBlockHeight);
		}
	}
*/
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(backgroundImage, 0, 0, null);
		if (piece != null) {
			g2d.translate(leftMargin+6, topMargin+6);				// FIXME NO USAR NUMEROS MAGICOS!!! Q XUXA es el 6???
			piece.paintMySelf(g, blockType, imgBlockWidth, imgBlockHeight);
		} else {
			g2d.drawString("NULL", 36, 56);
		}
	}

	public void setBlockType(BlockType bType) {
		blockType = bType;
		repaint();
	}

	// FIXME Para que corno guardo los argumentos??	PORQUE FUNCA MAL???
	public void setFrameColors(Color outLine, Color bg) {
		colorFrame = outLine;					
		colorBackground = bg;
		backgroundImage = createBackground(outLine, colorBackground);
		repaint();
	}

	public void drawPiece(Piece newPiece, BlockType bType) {
		setBlockType(bType);
		drawPiece(newPiece);
	}


	//FIXME
	// ACA ESTOY RECALCULANDO CADA VEZ QUE INVOCO A ESTE METODO AL RE PEDO (incluso si es null).. Los margenes y demas solo se deberian calcular la primera vez....
	public void drawPiece(Piece newPiece) {//	public void drawPiece(Piece p, BlockType bType)
		piece = newPiece;
		leftMargin = -1;
		topMargin = -1;
//		imgBlockWidth = -1;
//		imgBlockHeight = -1;
		if (imgBlockWidth == 0) {
			imgBlockWidth = ((panelWidth - 12) / piece.getBlockWidth());			// FIXME Aca tengo otro nro magico: "12"
			imgBlockHeight = ((panelHeight - 12) / piece.getBlockHeight());
		}
		calculateMargins();

		// setFrameColors invoca a repaint. Este if es para que repaint solo se invoque una vez.
		if (piece == null)
			setFrameColors(Color.GRAY, colorBackground);
		else
			repaint();
	}

	// no hace falta que esto este dentro de un metodo.
	// solo lo uso en drawPiece, asi que lo puedo sacar
	private void calculateMargins()
	{
		int bWidth = piece.getRightColumnTouchable() - piece.getLeftColumnTouchable() + 1;
		if (bWidth % 2 != 0) {
			leftMargin = ((piece.getBlockWidth() - bWidth) * imgBlockWidth) / 2;
			leftMargin -= (piece.getLeftColumnTouchable() * imgBlockWidth);
		}

		int bHeight = piece.getBottomRowTouchable() - piece.getTopRowTouchable() + 1;
		if (bHeight % 2 != 0) {
			topMargin = ((piece.getBlockHeight() - bHeight) * imgBlockHeight) / 2;
			topMargin -= (piece.getTopRowTouchable() * imgBlockHeight);
		}
	}

	public void clean()
	{
		piece = null;
		//imgPiece = null;
		repaint();		// ahh pero si redifini el update no borra
	}					// deberia borrar a mano o no redifinir
/*
	@Override
	public void paint(Graphics g)
	{
		if (panelEmpty)
			return;

		g.setColor(getBackground());
		g.fillRect(0,0, getWidth(), getHeight());
		g.setColor(getForeground());
		
		g.drawImage(imgPiece, 0, 0, null);		
	}
*/	
	// Testing method
	public static void main(String args[])
	{
		PieceFrame b = new PieceFrame(100, 100);
		JFrame f = new JFrame();
		f.setLayout(new FlowLayout());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.setSize(200, 200);
		f.add(b);
		f.getContentPane().setBackground(Color.RED);
		b.drawPiece(new PieceL(Color.RED));
		try {
			for (int i = 0; i < 25; i++) {
				Thread.sleep(500);
				b.drawPiece(new PieceL(Color.RED));
				Thread.sleep(500);
				b.drawPiece(new PieceI(Color.RED));
			}
				Thread.sleep(1000);
				b.clean();
		}
		catch (Exception e) { }
	}
//
//	@Override
//	public void update(Graphics g)
//	{
//		paint(g);
		// no deberia dibujar a menos que ...
		// podria llamar a super.update(g)... Deberia?
//	}
}
