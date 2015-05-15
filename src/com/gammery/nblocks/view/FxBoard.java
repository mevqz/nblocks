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

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.color.*;
import javax.swing.*;
import com.gammery.nblocks.model.*;
import com.gammery.nblocks.*;

import java.awt.geom.*;

import javax.imageio.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
// XXX change o switch????

import javax.sound.sampled.*;

public class FxBoard extends JComponent implements BoardDrawer
{
	private Clip clip;
	private BufferedImage imgBackground;
	private BufferedImage imgBoardPieces;
	private BufferedImage imgGrid;
	private int pxGrid;				// ancho y alto de una celda
	private int gridsVertical;		// cantidad de celdas a lo largo
	private int gridsHorizontal;	// cantidad de celdas a lo ancho
	private int pxBoardWidth;
	private int pxBoardHeight;

	private boolean gameStarted;
	private boolean gamePaused;
	private boolean gameOver;	// FIXME Con una sola de estas es suficiente
	private boolean gameWon;	// Borrar gameOver o gameWon

	private Piece piece;	// current tetrimino
	private BlockType bTypePiece;	
	private BlockType bTypeMirrorPiece;
	private boolean drawMirror;
	private boolean slidePiece = true;
	private float transparency = 0.25f;

	private boolean drawGrid;
	private Thread slidePieceDown;

	// TODO implementar este metodo
	// Se supone que realice alguna animacion o algo...
	private Color[] bgColors; {
	 bgColors = new Color[]{ 
		new Color(90, 0, 90), // magenta dark
		new Color(0, 60, 15),  // verde dark
		new Color(0, 22, 112) // blue dark
	 };
	};
	int idx = 0; 
	public void levelUp() {
//		Color[] cc = { Color.RED, Color.MAGENTA };
		Color color = bgColors[idx++ % bgColors.length];
		imgBackground = createBackground("logo.png", color);
		repaint();
	}

	// TODO ESTE metodo deberia llamar a updateGraphics, o tal vez se deberia llamar setGraphics
	public FxBoard(GraphicsSettings settings, Dimension size) {
		this(settings, (int)size.getWidth(), (int)size.getHeight());
	}
//	public FxBoard(GraphicsSettings settings, int cHorizontal, int cVertical) {
//		this(cHorizontal, cVertical, settings.getPxGridSize(),settings.getPieceBlockType(), 
//			settings.getMirrorPieceBlockType(),	settings.isGridEnabled()
//		);
//	}
	
	public void startNewGame() {
		reset();
		gameStarted = true;
		repaint();
	}

/*
	public void startCountdown() {
		final int
		for (int i = 3; i > 0; i--) {
			try {
				final int count = i;
				Runnable countdown = new Runnable() {
					public void run() {
						try {
							drawString(CENTER, Integer.toString(count));
							TimeUnit.SECONDS.sleep(1);
						} catch (Exception e) { e.printStackTrace(); }
					}
				}
				Thread th = new Thread(countdown);
				th.start();
				th.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
*/	

	public FxBoard(GraphicsSettings settings, int hBlocks, int vBlocks) {
		try {
			clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(
				NBlockApp.class.getResourceAsStream("clearLine.wav"));
			clip.open(inputStream);
		} catch (Exception e) {
			System.out.println("asd:" + e.getMessage());
		}

		pxGrid = settings.getPxGridSize();
		gridsHorizontal = hBlocks;
		gridsVertical = vBlocks;
		pxBoardWidth = hBlocks * pxGrid;
		pxBoardHeight = vBlocks * pxGrid;	

		setGraphicsSettings(settings, null);
		reset();
	}

	// Establece y actualiza la configuracion grafica. El arg Board
	// es necesario por si se cambia el block type para las piezas
	// una vez comenzado el juego. Si el juego no ha comenzado no hay
	// problemas si es argumento es NULL
	public void setGraphicsSettings(GraphicsSettings settings, Board board) {
		pxGrid = settings.getPxGridSize();
		drawGrid(settings.isGridEnabled());	// enable/switch
		slideAnimation(settings.isSlideAnimationEnabled());	// enable/turnOn
		if (settings.isBackgroundImageEnabled()) {
			String imageName = NBlockApp.BUNDLE.getString("BackgroundImage");
			setBackground(imageName);
		} else
			setBackground("");
		setPieceBlockType(settings.getPieceBlockType(), board);
		setMirrorPieceBlockType(settings.getMirrorPieceBlockType());
	}
	
	public FxBoard(int gridH, int gridV, int pxGrid) {
		this(gridH, gridV, pxGrid, BlockType.GRADIENT_BLOCK, BlockType.ROUND_BLOCK, false);
	}


	public void setBoardWidth(int blocks) {	// horizontalBlocks
		gridsHorizontal = blocks;
		pxBoardWidth = blocks * pxGrid;
		if (drawGrid)
			imgGrid = createGrid(pxBoardWidth, pxBoardHeight, gridsHorizontal, gridsVertical);
	}

	public FxBoard(int gridH, int gridV, int pxGrid, BlockType pBlockType, BlockType mBlockType, boolean dGrid) {
		if (pxGrid < 6)
			pxGrid = 6;

		gridsHorizontal = gridH;
		gridsVertical = gridV;
		pxBoardWidth = gridH * pxGrid;
		pxBoardHeight = gridV * pxGrid;	

		this.pxGrid = pxGrid;

		bTypePiece = pBlockType;
		bTypeMirrorPiece = mBlockType;
		drawGrid = dGrid;

		if (drawGrid)
			imgGrid = createGrid(pxBoardWidth, pxBoardHeight, gridsHorizontal, gridsVertical);

		reset();	// TODO reset deberia llamar a initScreen???
//		initScreen();
	}

	private void initScreen() {
		if (gameStarted == false) {
			String imageName = NBlockApp.BUNDLE.getString("InitScreenImage");
			setBackground(imageName);
		}
	}

	public void setBackground(String filename) {
		imgBackground = createBackground(filename, Color.BLACK);
		repaint();
	}

	private BufferedImage createGrid(int bW, int bH, int gH, int gV) {
		System.out.println("createGrid");
		BufferedImage grid = new BufferedImage(bW, bH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) grid.getGraphics();
		g2d.setColor(Color.GRAY);
		g2d.setStroke(new BasicStroke(1.0f));

		int x1, y1, x2, y2;
		for (int i = 0; i < gV; i++) {
			for (int j = 0; j < gH; j++) {
				x1 = pxGrid * j;
				y1 = pxGrid * i;
				x2 = x1 + pxGrid;
				y2 = y1 + pxGrid;
				g2d.draw(new Rectangle2D.Float(x1, y1, x2, y2));
			}	
		}
		return grid;
	}

	private void drawGrid(boolean newValue) {
		if (drawGrid == newValue)
			return;
		drawGrid = newValue;
		if (drawGrid)
			imgGrid = createGrid(pxBoardWidth, pxBoardHeight, gridsHorizontal, gridsVertical);
		repaint();
	}

	private void slideAnimation(boolean newValue) {
		slidePiece = newValue;
	}

/*
	private BufferedImage loadBackgroundImage(String filename) {
		BufferedImage bg = null;
		try { 
			bg = ImageIO.read(new File(filename));
			// TODO
			// escalar imagen (agrandarla para no tener q escalarla en cada paintComponent)
		} catch (Exception e) {
			bg = new BufferedImage(pxBoardWidth, pxBoardHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D) bg.getGraphics();
			g2d.setPaint(new GradientPaint(pxBoardWidth/2, 0, Color.GRAY.darker(), //Color.BLACK.brighter(), 
				pxBoardWidth/2, pxBoardHeight, Color.BLACK));
			g2d.fillRect(0, 0, pxBoardWidth, pxBoardHeight);
		} finally {
			return bg;
		}	
	}
*/
/*
	private BufferedImage createBackground(BackgroundType bgType, int width, int height) {
		BufferedImage bg = null;
		Color color = bgColors[idx++ % bgColors.length];
		switch (bgType) {
			case BLACK_GRADIENT:
				bg = createBlackGradientBG(color, width, height);
				break;
			case TEXTURE:
				bg = createTextureBG(color, width, height);
				break;
			case PLAIN_BLACK:
				bg = createPlainBlackBG(width, height);
				break;
		}
		return bg;		
	}
*/
	//private BufferedImage createBlackGradientBG(String imageName, Color color) {
	private BufferedImage createBackground(String imageName, Color color) {
		BufferedImage boardBG = null; 
		try { 
			boardBG = new BufferedImage(pxBoardWidth, pxBoardHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D) boardBG.getGraphics();
			g2d.setPaint(new GradientPaint(pxBoardWidth/2, 0, new Color(30,30,30),//color, //Color.GRAY.brighter(), darker() 
				pxBoardWidth/2, pxBoardHeight/3, new Color(36,36,36))); //Color.BLACK));
			g2d.fillRect(0, 0, pxBoardWidth, pxBoardHeight);
			// Image load & draw
			// BufferedImage image = ImageIO.read(new File(imageName));
			BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource(imageName));

			int posX = (pxBoardWidth - image.getWidth()) / 2;
			int posY = (pxBoardHeight - image.getHeight()) / 3;
			g2d.drawImage(image, posX, posY, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return boardBG;
		}	
	}



/*
	private BufferedImage createGridImage(String img, int bW, int bH, int gH, int gV)
	{
		BufferedImage bg = null;
		Graphics2D g2d = null;
		try { 
			bg = ImageIO.read(new File(img));
			g2d = (Graphics2D) bg.getGraphics();
			// TODO
			// escalar imagen (agrandarla para no tener q escalarla en cada paintComponent)

		} catch (Exception e) {
			bg = new BufferedImage(bW, bH, BufferedImage.TYPE_INT_RGB);
			g2d = (Graphics2D) bg.getGraphics();
			//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2d.setPaint(new GradientPaint(pxBoardWidth/2, 0, Color.BLACK.brighter(), 
				pxBoardWidth/2, pxBoardHeight, Color.BLACK));
//			g2d.setColor(Color.BLACK);	//GRAY
			g2d.fillRect(0, 0, pxBoardWidth, pxBoardHeight);
//			g2d.setColor(Color.BLACK);	//YELLOW
//			g2d.setStroke(new BasicStroke(outLine));
//			g2d.drawRect((int)outLine / 2, (int)outLine / 2, pxBoardWidth-((int)outLine*1), pxBoardHeight-((int)outLine*1));

		} finally {
			//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2d.setColor(Color.GRAY);
			g2d.setStroke(new BasicStroke(1.0f));

			int x1, y1, x2, y2;
			for (int i = 0; i < gV; i++) {
				for (int j = 0; j < gH; j++) {
					x1 = pxGridWidth * j;
					y1 = pxGridHeight * i;
					x2 = (pxGridWidth * j) + pxGridWidth;
					y2 = (pxGridHeight * i) + pxGridHeight;
					g2d.draw(new Rectangle2D.Float(x1, y1, x2, y2));
				}	
			}
		}
	
		return bg;
	}
*/
	@Override
	public Dimension getPreferredSize()	{
		return new Dimension(pxBoardWidth, pxBoardHeight);
	}

	//TODO: remover despues, es para testing nomas
	@Override
	public void update(Graphics g) {
		System.out.println("\t\t\t\tupdate--------------------------_>     <<XXXX>");
		// luego de probar si se imprime el Sout deberia de invocar a super.update(g);
	}

	//TODO Son necesarios los parametros???
	//No solo porque en teoria ya los tengo sino porq podrian estar en piece...
	public void hideMirrorPiece(int posX, int posY)	{
		if (drawMirror) { 
			if (piece != null)
				repaint(posX * pxGrid, posY * pxGrid, 
					piece.getBlockWidth() * pxGrid, piece.getBlockHeight() * pxGrid);
		}
		drawMirror = false;
	}

	@Override
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(imgBackground, 0, 0, null);
		if (gameStarted) {
			if (drawGrid)
				g2d.drawImage(imgGrid, 0, 0, null);
			g2d.drawImage(imgBoardPieces, 0, 0, null);
			if (gameOver) {
				drawGameOver(g2d);
			} else if (gameWon) {
				drawGameWon(g2d);
			} else if (piece != null) {	// piece != null redudante??
				piece.paintMySelf(g2d, bTypePiece, pxGrid);		// pxBlock
				if (drawMirror) {
					piece.paintMirrorPiece(g2d, bTypeMirrorPiece, pxGrid, transparency);
				}
				if (gamePaused)	// gameRunning???o
					drawGamePaused(g2d);//, "GAME PAUSED!");	drawString(...);
			}
		}
	}
	
	private void drawGamePaused(Graphics g) {
		String msg = NBlockApp.BUNDLE.getString("GamePaused");
		drawString(g, msg);
	}
	
	private void drawGameOver(Graphics g) {
		String msg = NBlockApp.BUNDLE.getString("GameOver");
		drawString(g, msg);
	}

	private void drawGameWon(Graphics g) {
		String msg = NBlockApp.BUNDLE.getString("GameWon");
		drawString(g, msg);
	}

	public void drawNewPiece(Piece p) {
		piece = p;
		drawPieceInPos(piece.getPosX() * pxGrid, piece.getPosY() * pxGrid);
	}

	// TODO: Estos requieren un repaint seguramente! Checkear eso
	// De ser asi hacer un repaint usando los pxPiecePos
	private void setPieceBlockType(BlockType bType, Board board) {
		if (bTypePiece == bType)
			return;
		bTypePiece = bType;
		if (gameStarted)
			updateBoard(board);
	}

	// TODO: Estos requieren un repaint seguramente! Checkear eso
	// De ser asi hacer un repaint usando los pxMirrorPos
	private void setMirrorPieceBlockType(BlockType bType) {
		//TODO Si requiere repaint entonces agregar un: if (mirrorPieceBlockType == bType) return;
		// Solo hacer repaint si drawMirror == true
			
		bTypeMirrorPiece = bType;
	}

	// XXX Deberia checkear si board == null??
	public void updateBoard(Board board) {
		BufferedImage imgBoard = new BufferedImage(pxBoardWidth, pxBoardHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) imgBoard.createGraphics();
		board.paintMySelf(g2d, bTypePiece, pxGrid, pxGrid);
		imgBoardPieces = imgBoard;
		repaint();
		//repaint(0, 0, pxBoardWidth, pxBoardHeight);
//FIXME
// paintMySelf podria devolver las filas afectadas para no repaintear todo
	}

	private void drawPieceInPos(int pxPosX, int pxPosY) {
		if (!SwingUtilities.isEventDispatchThread())
			System.out.println("FxBoard.drawPieceInPos(): BUG!!!!!!!!!!!");

		//FIXME Esto es un hack muy feo para evitar un nullpointexception q se produce cuando la piece toca el piso 
		//cuando se esta en modo slidePiece. Probablemente lo incorrecto sea setear piece = null en todos los lugares
		//en este caso seria en pieceTouchGround, pero eso lo tengo que evaluar. Si decido quedarme con este if tambien 
		//tengo que incluir lo mismo en drawMirrorPiece u otro semejante...
		if (piece == null)
			return;

		repaint(piece.getPixelPositionX(), piece.getPixelPositionY(), 
			pxGrid * piece.getBlockWidth(), pxGrid * piece.getBlockHeight());
		piece.setPixelPositionX(pxPosX);
		piece.setPixelPositionY(pxPosY);
		repaint(piece.getPixelPositionX(), piece.getPixelPositionY(), 
			pxGrid * piece.getBlockWidth(), pxGrid * piece.getBlockHeight());
	}

	public void movePieceDownTo(int posX, final int posY) {		//FIXME Hice final a posY temporalmente... leer coment mas abajo...
		if (slidePieceDown != null) {
			slidePieceDown.interrupt();
			slidePieceDown = null;
		}
		if (piece == null)
			return;
	
		if (slidePiece && ((pxGrid % 6) == 0)) {		//TODO cambiar el "2" por pxDown
			Runnable runSlide = new Runnable() {		//(this, pxDown, msWait);
				public void run() {
					try {
						final int pxDown = 6;		// ALT name: pxDisplacement
						for (int i = 1; i <= pxGrid / pxDown; i++) {
							TimeUnit.MILLISECONDS.sleep(18);
							final int idx = i;
							SwingUtilities.invokeLater( new Runnable() {
								public void run() {	//FIXME aca tiro un nullpointer en piece
									// TODO la resta a posY no era necesaria, pero le resto 0 para recordar donde era
									// pero luego REMOVER la resta de cero de (posY-0)
									int piecePxPosX = piece.getPixelPositionX();
									drawPieceInPos(piecePxPosX, ((posY-0) * pxGrid) + (idx*pxDown));
									// en lugar de "posY-1" deberia usar "FxBoard.this.posY"
								}	
							});
						}
					}
					catch (InterruptedException e) { }
				}
			};
			slidePieceDown = new Thread(runSlide);
			slidePieceDown.start();
		} else {
			drawPieceInPos(posX * pxGrid, posY * pxGrid);
		}		
	}

	public void movePieceHorizontallyTo(int posX, int posY) {
		if (slidePieceDown != null) {	
			slidePieceDown.interrupt();	
			slidePieceDown = null;
		}

		drawPieceInPos(posX * pxGrid, posY * pxGrid);
	}
	
	// Este metodo no debe llamar a pieceTouchGround ya que todo depende del lockDown
	public void dropPieceTo(int posX, int posY) {
		if (slidePieceDown != null) {	
			slidePieceDown.interrupt();	
			slidePieceDown = null;
		}

		drawPieceInPos(posX * pxGrid, posY * pxGrid);
	}


	//TODO: para el kolumn, en el board podria usar una matriz de objectos Block.
	//cada Block tendria metodos como paintMySelf
	//TODO
	//estaria wen hacer una version de este board que tenga una implementacion distinta para el manejo
	//de las lineas: usar un arreglo de BufferedImage en lugar del imgBoardPiece. De esa manera
	//es mas facil agregar efectos a esas lineas, borrarlas, etc. Borrarlas es solo cuestion de sacarlas de la lista

	private int getConsecutives(int[] rows, int index) {
		if (index >= rows.length)
			return -1;
	
		int i = index;
		int consecutives = 1;
		while ((i+1 < rows.length) && (rows[i] == rows[i+1]+1)) {
			consecutives++;
			i++;
		}

		return consecutives;
	}
	
	public void clearLines(int rows[], int rowsErased) {
		if (SwingUtilities.isEventDispatchThread())
			System.out.println(getClass().getSimpleName() + "clearLines() runs in EDT!!!");

		// audio
		try {
			if (clip.isRunning()) {
				clip.stop();
				clip.setMicrosecondPosition(0);
			}
			clip.start();
		} catch (Exception e) {
			System.out.println("Clip error");
			System.out.println(e.getMessage());
		} 

		// obtengo las filas que no van a ser borradas
		int[] drows = invertArray(rows, rowsErased, gridsVertical);

		int index = 0;
		int consecutives;

		// creo una "nueva imgBoardPieces" que va a tener las filas que no van a ser borradas y
		// las filas que si van a ser borradas van a tner el background correspondiente
		// (despues uso esta imagen para ir alternando y lograr un efecto de "titileo"
		BufferedImage boardFlashed = new BufferedImage(imgBoardPieces.getWidth(), imgBoardPieces.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics bfg = boardFlashed.createGraphics();

		// dibujo los fragmentes de imgBackground correspondientes a las filas que son borradas
		// en la nueva imagen
		while ((consecutives = getConsecutives(rows, index)) != -1) {
			int y1 = (rows[index+consecutives-1] * pxGrid);
			int y2 = (rows[index] * pxGrid) + pxGrid;
			BufferedImage img = imgBackground.getSubimage(0, y1, imgBackground.getWidth(), y2-y1);
			bfg.drawImage(img, 0, y1, null);

			index += consecutives;
		}

		// agrego a una lista los fragmentos (filas) de imgBoardPieces que no van
		// a ser borradas y tambien las dibujo en la "nueva imgBoardPieces"
		ArrayList<BufferedImage> boardFragments = new ArrayList<BufferedImage>();
		index = 0;
		while ((consecutives = getConsecutives(drows, index)) != -1) {
			int y1 = (drows[index+consecutives-1] * pxGrid);
			int y2 = (drows[index] * pxGrid) + pxGrid;
			BufferedImage img = imgBoardPieces.getSubimage(0, y1, imgBoardPieces.getWidth(), y2-y1);
			bfg.drawImage(img, 0, y1, null);
			boardFragments.add(img);
			index += consecutives;
		}

		Thread flasher = new Thread(new FlashLinesAnimation(100, 5, boardFlashed, imgBoardPieces));
		flasher.start();
		try {
			flasher.join();
		} catch (Exception e) {
			System.out.println("Se atrapo Exception!");
			e.printStackTrace();
		}
				
		// TODO
		// Si quiero agregar un slide tiene que ser en este punto

		// creo una nueva imgBoardPieces con las filas borradas y la cambio por la original
		BufferedImage newPieces = new BufferedImage(imgBoardPieces.getWidth(), imgBoardPieces.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics pg = newPieces.createGraphics();

		int y1 = newPieces.getHeight();
		for (BufferedImage frag : boardFragments) {
			y1 -= frag.getHeight();
			pg.drawImage(frag, 0, y1, null);
		}

		imgBoardPieces = newPieces;
		//repaint(0, 0, 0, rows[0] * pxGridHeight);
		repaint();
	}

	/*
	 * Retorna el complemento para las filas que indica la matriz 'rows',
	 * siendo 'limit' el limite de filas en el conjunto. rowsErased indica
	 * la cantidad de filas que contiene la matriz rows.
	 * */
	// XXX Se podria lograr esto mismo usando un Set con lo que se ahorraria
	// codigo y se disminuiria la complejidad, pero esto funca bien :)
	private int[] invertArray(int[] rows, int rowsErased, int limit) {
		int[] all = new int[limit];
		for (int i = 0; i < limit; i++)
			all[i] = limit-1-i;
	
		for (int i = 0; i < limit; i++) {
			for (int j = 0; j < rowsErased; j++) {
				if (all[i] == rows[j])
					all[i] = -1;
			}
		}

		int[] r = new int[limit - rowsErased];
		int x = 0;
		for (int i = 0; i < limit; i++) {
			if (all[i] == -1) {
				continue;
			}
			r[x++] = all[i];
		}

		return r;
	}
/*
			//modificar imgPieceBoard  y hacer un repaint de
			//la fila que esta mas abajo hacia arriba
			//asi me ahorro repaintear una parte (la de mas abajo)

		Color[] cc = { Color.RED, Color.BLUE, Color.GREEN};

		for (int i = 0; i < rowsErased; i++) {
			//g.drawImage(lineFlashed, 0, rows[i] * pxGridHeight, null);
			g.setColor(cc[i % cc.length]);
			g.fillRect(0, rows[i] * pxGridHeight, imgBoardPieces.getWidth(), pxGridHeight);
		}
*/

	// TODO Arreglar el NullPointerException
	public void drawMirrorPiece(int pxPosX, int pxPosY)	{
		drawMirror = true;
		// FIXME He obtenido NullPointer exceptions en esta linea porque piece es == a null...
		// Tengo que pensar porq llega a ocurrir esto y despues ver como lo soluciono...
		repaint(piece.getMirrorPixelPositionX(), piece.getMirrorPixelPositionY(), 
			pxGrid * piece.getBlockWidth(), pxGrid * piece.getBlockHeight());
		//FIXME		Creo que aca deberia setear drawMirror = true y en la primer linea deberia setearlo a false!!
		piece.setMirrorPixelPositionX(pxPosX * pxGrid);
		piece.setMirrorPixelPositionY(pxPosY * pxGrid);
		repaint(piece.getMirrorPixelPositionX(), piece.getMirrorPixelPositionY(), 
			pxGrid * piece.getBlockWidth(), pxGrid * piece.getBlockHeight());
	}

	//	se puede aprovechar para agregar un sonido
	public void pieceTouchGround(int posX, int posY) {
		drawPieceInPos(posX * pxGrid, posY * pxGrid);
		Graphics2D g2d = (Graphics2D) imgBoardPieces.getGraphics();
		if (piece == null)
			System.out.println("Piece es NULLLLLLLLLLLLLLLLLLLLLLL");
		else
			piece.paintMySelf(g2d, bTypePiece, pxGrid, pxGrid);

/*		Runnable outlineAnimation = new Runnable() {
			public void run() {
				PieceOutline outline = new PieceOutline(piece, pxPosX, pxPosY, pxGridWidth, pxGridHeight);
				for (int i = 0; i < 40; i++) {
					TimeUnit.MILLISECONDS.sleep(18);
					repaint(outline.getPosX(), outline.getPosY(), outline
	
			}
		}*/

		piece = null;
		//FIXME
		//deberia hacer un repaint? para que se actualice la parte nueva de imgBoardPieces?
		//repaint(posX * pxGridWidth, pxPosY * pxGridHeight, 4 * pxGridWidth, 4 * pxGridHeight);
		//toy harcodeando el 4...
	}

	public void changePauseStatus() {
		// Agregar un efecto Blur al board mientras se esta en Pausa
		//imgBoardPieces = convertToGrayscale(imgBoardPieces);

		gamePaused = !gamePaused;
		repaint();
	}
	
	private void drawString(Graphics g, String str) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform at = g2d.getTransform();
		Font font = new Font("SansSerif", Font.BOLD, 36);
		g2d.setFont(font);
		g2d.setColor(Color.WHITE);	
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
			RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.90f));
	
		FontMetrics metrics = g2d.getFontMetrics(font);
		int height = metrics.getHeight();
		int width = metrics.stringWidth(str);

		int posX = (pxBoardWidth - width) / 2;
		int posY = (pxBoardHeight - height) / 2;

		g2d.drawString(str, posX, posY);
		at.setTransform(at);
	}

	public void gameLost() {
		imgBoardPieces = convertToGrayscale(imgBoardPieces);
		gameOver = true;
		System.out.println("after convertToGray");
		repaint();
	}

	public void gameWon() {
		gameWon = true;
		repaint();
	}


	// TODO
	// Si en algun momento uso los valores previos de pxPosXXX entonces tengo que setearlos como negativos aca...
	public void reset() {
		piece = null;
		gameStarted = false;
		gamePaused = false;
		gameOver = false;
		gameWon = false;
		imgBoardPieces = new BufferedImage(pxBoardWidth, pxBoardHeight, BufferedImage.TYPE_INT_ARGB);
		initScreen();		
	}

/*
 * 1) Usar un array de BufferedImages (para cada una de las lineas)
 *
 * 2) Usar una imagen separada (ARGB) para dibujar la pieza, y al momento de renderizar ahi si la fusiono con el fondo
 i* */


	public void rotatePiece() {
		drawPieceInPos(piece.getPosX() * pxGrid, piece.getPosY() * pxGrid);
		//AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(90), imgPiece.getWidth()/2, imgPiece.getHeight()/2);
		//g2d.transform(at);
	}

	private static BufferedImage convertToGrayscale(BufferedImage source) { 
		 BufferedImageOp op = new ColorConvertOp(
		   ColorSpace.getInstance(ColorSpace.CS_GRAY), null); 
		 return op.filter(source, null);
	}

	//hacer un clipping path antes del for y usar eso (y si no puedo hacer un repaint de la zona afectada solamente)
	class FlashLinesAnimation implements Runnable {
		private int msWait;
		private int loops;
		private BufferedImage flashed;
		private BufferedImage original;
		public FlashLinesAnimation(int ms, int newLoops, BufferedImage newFlashed, BufferedImage newOriginal) {
			msWait = ms;
			loops = newLoops;
			flashed = newFlashed;
			original = newOriginal;
		}
		
		public void run() {
			try {
				// BufferedImage original = imgBackgroundPieces;
				for (int i = 0; i < loops; i++) {
					if (i % 2 == 0) 
						SwingUtilities.invokeLater( new Runnable() { 
							public void run() { imgBoardPieces = flashed; repaint(); }
						});
					else
						SwingUtilities.invokeLater( new Runnable() { 
							public void run() { imgBoardPieces = original; repaint(); }
						});
					TimeUnit.MILLISECONDS.sleep(msWait);
				}
			} catch (Exception e) { }
		}
	}
}


//TODO:
//DUDA: las posiciones de la Piece con respecto al J2DBoard deberian estar en Piece??
//ANSW: No, lo que tiene que estar en Piece es la pos con respecto a Board y yo haciendo un get calculo esa pos con px
//Lo que deberia pensar es si mirror Pos (con respecto a Board) deberia estar en Piece
// Es necesario que pxPos este como attrib?

//TODO; nota: esta bien tener pxPos y pxMirrorPos aca... JOSHA





/*
//FIXME: SOLO ES UN MERO BACKUP::!!!! borrar desp
	public void paintComponent(Graphics g)
	{
		//super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		//g2d.drawImage(imgBackground, 0, 0, pxBoardWidth, pxBoardHeight, null);
		g2d.drawImage(imgBackground, 0, 0, null);

		if (gameRunning) {
			//g2d.drawImage(imgBoardPieces, 0, 0, pxBoardWidth, pxBoardHeight, null);
			g2d.drawImage(imgBoardPieces, 0, 0, null);
			if (piece != null) {
				AffineTransform at = g2d.getTransform();
				g2d.translate(pxPosX, pxPosY);
				piece.paintMySelf(g2d, blockType, pxGridWidth, pxGridHeight);
				if (drawMirror) {
					g2d.setTransform(at);
					g2d.translate(pxMirrorPosX, pxMirrorPosY);
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
					piece.paintMySelf(g2d, blockType, pxGridWidth, pxGridHeight);
				}
			}
		} else {
			
		}*/
		/*
 *		Deberia ser...
 *		if (gameStarted) {
 *			if (gameRunning) {
 *				g2d.drawImage(imgBoardPieces, 0, 0, 
 *					imgBoardPieces.getWidth(), imgBoardPieces.getHeight(),
 *					null);
 *				for (GPiece p : objectsToDraw) {
 *					if (p.needUpdate) {
	 *					g2d.drawImage(p.getImage(), p.getPosX(), p.getPosY(),
 *							p.getWidth(), p.getHeight(), null);
 *						p.needUpdate = false;
 *					}
 *				}
 *			} else if (gamePaused) {
 *				drawGamePaused(g2d);
 *			}
 *		}
 * */
//	}








/*
BACKUP de paintComponent


		if (gameStarted) {
			g2d.drawImage(imgBackground, 0, 0, null);
			if (drawGrid)
				g2d.drawImage(imgGrid, 0, 0, null);
			g2d.drawImage(imgBoardPieces, 0, 0, null);
			if (gameOver) {
				drawString(g2d, "¡¡GAME OVER!!");
			} else if (gamePaused && piece != null) {	// piece != null redudante??
				AffineTransform at = g2d.getTransform();
				g2d.translate(pxPosX, pxPosY);
				piece.paintMySelf(g2d, pieceBlockType, pxGridWidth, pxGridHeight);
				g2d.setTransform(at);
				drawString(g2d, "¡¡GAME PAUSED!!");
			} else if (piece != null) {	// gameRunning???o
				AffineTransform at = g2d.getTransform();
				g2d.translate(pxPosX, pxPosY);
				piece.paintMySelf(g2d, pieceBlockType, pxGridWidth, pxGridHeight);
				if (drawMirror) {
					g2d.setTransform(at);
					//FIXME: deberia hacer otro getTransform??
					g2d.translate(pxMirrorPosX, pxMirrorPosY);
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
					piece.paintMySelf(g2d, mirrorPieceBlockType, pxGridWidth, pxGridHeight);
				}
			}
//			Tengo que usar el Iterator para poder remover
//			for (Drawable d : drawList) {
//				if (d.isDone())
//					drawList.remove(d);
//				else
//					d.drawMe(g2d);	// drawMe deberia hace el traslade, setear el Composite y volver a dejar todo como estaba
//			}
		} else {
			// Es necesaria otra img? no podria reutilizar la imgBackground???
			g2d.drawImage(imgBackground, 0, 0, null);
		}


*/
