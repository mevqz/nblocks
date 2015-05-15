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

package com.gammery.nblocks;

import javax.swing.*;
import java.awt.*;
import com.gammery.nblocks.model.*;
import com.gammery.nblocks.view.*;
import com.gammery.nblocks.controller.*;
//import nblock.scoremanager.*;
import java.util.concurrent.*;
import java.util.*;
import java.awt.event.*;

public class NBlockApp
{
	private Board board;
	private BoardDrawer bDrawer;
	private NextPiecesPanel pPieces;	// or PreviewPiecesPane // Tetromino Preview Pane[l]
	private NBlockMenuBar menu;
	private GameInfoDisplay infoDisplay;
	private GamePlay2 gamePlay;
	private GameMode gameMode;
	private JFrame frame;
	private boolean gamePaused;
	private GraphicsSettings graphicsSettings;
	private GameModeSettings gameModeSettings;
	private GameplaySettings gameplaySettings;
	private ControlSettings controlSettings;
	private boolean gameModeSettingsSet;
	public static ResourceBundle BUNDLE;
//	private ExecutorService exec = Executors.newSingleThreadExecutor();

	// Este metodo deberia resetear todo: LeftPanel, bDrawer, NextPieces, 
	public void reset() {
		// bDrawer.reset();
		// pPieces.reset();
		// infoDisplay.reset();
		// gamePaused = false;	// XXX ????
	}

	public void randomColoredPieces(boolean randomColored) {
		gameplaySettings.setRandomColoredPieces(randomColored);
	}

	public void showMirrorPiece(boolean showMirror) {
		gameplaySettings.setMirrorPieceEnabled(showMirror);
		if (board != null)
			board.setShowMirror(showMirror);
	}

	public void setKickWall(boolean kWall) {
		gameplaySettings.setKickWallEnabled(kWall);
		if (board != null)
			board.setWallKickEnabled(kWall);
	}

	// Crea y muestra la GUI
	public NBlockApp() {
		BUNDLE = ResourceBundle.getBundle("NBlockMessages");
		graphicsSettings = new GraphicsSettings();
		gameModeSettings = new GameModeSettings();	// XXX Deberia usar una factory para esto?
		gameplaySettings = new GameplaySettings();
		controlSettings = new ControlSettings();
	
		infoDisplay = new GameInfoDisplay();
	
		String title = BUNDLE.getString("NBlockAppTitle");
		frame = new JFrame(title);
		int boardWidth = gameModeSettings.getBoardWidth();
		int boardHeight = gameModeSettings.getBoardHeight();
		bDrawer = new FxBoard(graphicsSettings, boardWidth, boardHeight);	
		frame.getContentPane().add((JComponent)bDrawer);
		pPieces = new NextPiecesPanel();
		frame.getContentPane().add(pPieces, BorderLayout.EAST);
		frame.getContentPane().add(infoDisplay, BorderLayout.WEST);

		menu = new NBlockMenuBar(this, gameplaySettings);
		frame.setJMenuBar(menu.getJMenuBar());
	
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);	// este no es el default??
		frame.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent e) { exit(); }
		});
		frame.setIconImage(new ImageIcon("iconito.jpg").getImage());
		frame.pack();	
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) { resume(); }
			public void focusLost(FocusEvent e) { pause(); }
		});

		frame.addKeyListener(new KBCtrl(this, controlSettings));
		frame.setFocusable(true);
	}

	public static void main (String[] args)	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { new NBlockApp(); }
		});
	}

	/* Inicia una nueva partida de juego */
	public void startNewGame() {
		startNewGame(true);
	}

	/* Inicia una nueva partida de juego. Si el argumento playNewGame
	 * es false se vuelve a jugar la misma partida anterior a esta, es
	 * decir, se generan la misma secuencia de tetriminos */
	public void startNewGame(boolean playNewGame) {
		if (gamePlay != null) // game in progress
			endGame();
		
		if (!gameModeSettingsSet) 
			showGameModeSettings();
	
		/* Esto es para forzar al jugador que aplique los settings
		 * del modo de juego para poder empezar la partida */
		if (!gameModeSettingsSet) 
			return;

		menu.setGameModeSettings(false);
		menu.setRandomColoredPieces(false);
		boolean randColor = gameplaySettings.isRandomColoredPieces();
		PieceFactory.getInstance().reset(randColor, playNewGame);
		pPieces.cleanFrames();

		bDrawer.setGraphicsSettings(graphicsSettings, board);
		bDrawer.setBoardWidth(gameModeSettings.getBoardWidth());
		frame.pack();
		bDrawer.startNewGame();
		boolean kickWall = gameplaySettings.isKickWallEnabled();
		boolean showMirrorPiece = gameplaySettings.isMirrorPieceEnabled();
		board = new Board(bDrawer, gameModeSettings.getBoardWidth(), 
			gameModeSettings.getBoardHeight(), showMirrorPiece, 
			gameModeSettings.getInitGarbageLines(), kickWall);
//				String bg = getBackgroundName(1);
//				bDrawer.setBackground(bg);	
		gameMode = GameModeFactory.create(gameModeSettings);

//		infoDisplay.setHiScore(ScoreServiceTest.getHighestScore(gameModeSettings.getGameModeType()).getScore());
		infoDisplay.setMaxLevel(gameMode.getFinalLevel());
//		infoDisplay.setMaxLines(gameMode.linesToLevelUp());
		infoDisplay.updateScoreInfo(gameMode.getScore());

		gamePlay = new GamePlay2(this);
		Thread th = new Thread(gamePlay);
		th.start();			// Usar un executor despues

		// Esto para que era?? por el initGarbageLines???
		SwingUtilities.invokeLater(	new Runnable() { 
			public void run() { bDrawer.updateBoard(board); }
		});
	}

	/* Finaliza la partida actual si es que se esta jugando una */
	public void endGame() {
		//System.out.println("NBlocksApp.endGame() runs in EDT? "
		//	+ SwingUtilities.isEventDispatchThread());
		if (gamePlay != null)	// game in progress
			gamePlay.end();
		
		bDrawer.reset();
		pPieces.cleanFrames();
		//infoDisplay.reset();
		gamePlay = null;
		gamePaused = false;
		board = null;

		// Habilito las opciones del menu que no podian ser modificadas
		// mientras el juego esta corriendo (porque son settings estaticas;
		// no pueden ser cambiadas para una misma partida)
		// FIXME el nombre de estos metodos no son descriptivos...
		menu.setGameModeSettings(true);
		menu.setRandomColoredPieces(true);
	}


	public void showAbout() {
//		JOptionPane.showMessageDialog(frame, "Content..", "About NBlocks", JOptionPane.INFORMATION_MESSAGE);
		AboutDialog.showDialog(frame);
	}

	/* Termina la aplicacion. Si hay un juego en progreso pide confirmacion */
	public void exit() {	// exit(JFrame parentComponent)
	//	System.out.println("NBlocksApp.quit() running on EDT?: " + 
	//	SwingUtilities.isEventDispatchThread());

		if (gamePlay != null) {		// If gameInProgress
			pause();
			String no = BUNDLE.getString("ExitNo");
			String yes = BUNDLE.getString("ExitYes");
			Object[] options = new Object[]{no, yes};
			String titleQuitDialog = BUNDLE.getString("TitleExit");
			String msgConfirmQuit = BUNDLE.getString("ConfirmExitMsg");
			// Todos los JOptionPane.showXXXDialog son bloqueantes pero no jode en este caso
			int optionChosen = JOptionPane.showOptionDialog(frame, msgConfirmQuit, titleQuitDialog, 
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
								null, options, options[0]);
			System.out.println("optionChosen : " + optionChosen);
			if (optionChosen == 1) {
				System.exit(0);
			} else {
				resume();
			}
		} else
			System.exit(0);
	}

	/* Muestra el dialogo de Graphics Settings y aplica cambios si hubo */
	public void showGraphicsSettings() {
	//	System.out.println("NBlocksApp.showPreferences in EDT?: "
	//		+ SwingUtilities.isEventDispatchThread());
		boolean applySettings = GraphicsSettingsDialog.showDialog(frame, graphicsSettings);
		if (applySettings && board != null)
			bDrawer.setGraphicsSettings(graphicsSettings, board);
	}


	// TODO
	/* Muestra el dialogo para cambiar la configuracion de teclas */
	public void showControlSettings() {
		System.out.println("showControlSettings");
/*
		ControlSettings settings = ControlSettingsDialog.showDialog(frame, controlSettings);
		if (settings != null) {
			for (KeyListener kListener : frame.getKeyListeners())
				if (kListener instanceof KBControl) {	// este if y el break no son necesarios...
					frame.removeKeyListener(kListener);
					// TODO o en lugar del remove::
					// ((KBCtrl)kListener).setControlSettings(settings);
					break;
				}
			KBControl kbControl = new KBControl(settings);
			frame.addKeyListener(settings);
		}*/
	}

	/* Muestra el dialogo de Game Mode Settings y aplica cambios si hubo */
	public void showGameModeSettings() {
		boolean applySettings = GameModeSettingsDialog.showDialog(frame, gameModeSettings);
		if (applySettings) {
			gameModeSettingsSet = true;
		}
	}
	
	/* Mueve el tetrimino en juego un slot a la izquierda */
	public void movePieceToLeft() {
		/*System.out.println("Is movePieceToLeft() running on EDT? : " + 
			SwingUtilities.isEventDispatchThread());*/

		if (!gamePaused && board != null) {
			board.moveLeft();
//			exec.execute( new Runnable() { public void run() { board.moveLeft(); } } );
	//		bDrawer.setDrawGrid(true);
		}
	}
	
	/* Mueve el tetrimino en juego un slot a la derecha */
	public void movePieceToRight() {
	//	System.out.println("Is movePieceToRight() running on EDT? : " + 
	//		SwingUtilities.isEventDispatchThread());

		if (!gamePaused && board != null)
			board.moveRight();
//			exec.execute( new Runnable() { public void run() { board.moveRight(); } } );
	}

	/* Mueve el tetrimino en juego lo que mas se pueda a la izquierda */
	public void movePieceToExtremeLeft() {
		/*System.out.println("Is movePieceToExtremeLeft() running on EDT? : " + 
			SwingUtilities.isEventDispatchThread());*/
		if (!gamePaused && board != null)
			board.moveExtremeLeft();		// TODO: se deberia llamar: "moveToExtremeLeft" (con el 'To')
//			exec.execute( new Runnable() { public void run() { board.moveExtremeLeft(); } } );
	}

	/* Mueve el tetrimino en juego lo que mas se pueda a la derecha */
	public void movePieceToExtremeRight() {
		/*System.out.println("Is movePieceToExtremeRight() running on EDT? : " + 
			SwingUtilities.isEventDispatchThread());*/

		if (!gamePaused && board != null)
			board.moveExtremeRight();
//			exec.execute( new Runnable() { public void run() { board.moveExtremeRight(); } } );
	}

	/* Mueve el tetrimino en juego un slot hacia abajo */
	public boolean movePieceDown() {
		/*System.out.println("Is movePieceDown() running on EDT? : " + 
			SwingUtilities.isEventDispatchThread());*/

//		if (!gamePaused && board != null)
//			board.moveDown();
//			exec.execute( new Runnable() { public void run() { board.moveDown(); } } );
												if (!gamePaused && board != null)
													return board.moveDown();
												return false;
	}


	/* Suelta el tetrimino en juego hasta que toque fondo */
	public void dropPiece() {
		/*System.out.println("Is dropPiece() running on EDT? : " + 
			SwingUtilities.isEventDispatchThread());*/

		if (!gamePaused && board != null) {
			board.drop();
//			exec.execute( new Runnable() { public void run() { board.drop(); } } );
			if (gameMode.isLockDownAtDrop())
				gamePlay.lockDownAtDrop();
		}		
	}

	/* Rota el tetrimino en juego en sentido anti-horario (izquierda) */
	public void rotatePieceToLeft() {
		/*System.out.println("Is rotatePiece() running on EDT? : " + 
			SwingUtilities.isEventDispatchThread());*/

		if (!gamePaused && board != null)
			board.rotatePiece();
//			exec.execute( new Runnable() { public void run() { board.moveLeft(); } } );
	}

	/* Rota el tetrimino en juego en sentido horario (derecha) */
	public void rotatePieceToRight() {
		/*System.out.println("Is rotatePiece() running on EDT? : " + 
			SwingUtilities.isEventDispatchThread());*/

		if (!gamePaused && board != null)
			board.rotatePiece();
//			exec.execute( new Runnable() { public void run() { board.moveLeft(); } } );
	}



	// Stateful/Boolean pause 
	/* Cambia el estado de pausa */
	public void changePauseStatus() {
		System.out.println("Is changePauseStatus() running on EDT? : " + 
			SwingUtilities.isEventDispatchThread());

		System.out.println("changePauseStatus() called");
		//TODO Creo que esto no es necesario
		if (gamePlay == null)	// si nunca se invoco a startNewGame
			return;
		if (!gamePaused)
			pause();
		else
			resume();
	}

	// XXX
	// FIXME Creo que este metodo podria llegar a ejecutarse simultaneamente con movePieceDown (desde GamePlay2)
	// XXX
	/* Pausea el juego */
	public void pause() {
		System.out.println("Is pause() running on EDT? : " + 
			SwingUtilities.isEventDispatchThread());

		System.out.print("NBlockApp.pause() called. Executed: ");
		if (!gamePaused && board != null) {			// este if es redundante si viene de changePauseStatus pero no lo es si viene
			//TODO tengo que PAUSEAR el TIMER!!			// directamente de otro lado, por ejemplo: Lost Focus (listener)
			gamePlay.pause();	
			bDrawer.changePauseStatus();
			gamePaused = !gamePaused;
		}
	}

	/* Despausea el juego */
	public void resume() {
		System.out.println("Is resume() running on EDT? : " + 
			SwingUtilities.isEventDispatchThread());

		System.out.println("NBlockApp.resume() called.");
		if (gamePaused && board != null) {
			//TODO tengo que DESPAUSEar el timer!!!!
			bDrawer.changePauseStatus();
			gamePlay.resume();
			gamePaused = !gamePaused;
		}
	}

	// TODO 
	/* Muestra el dialogo de los mejores puntajes */
	public void showBestScores() {
		HighestScoresDialog.showDialog(frame, gameModeSettings.getGameModeType());
/*	
		String title = "Highest Scores";
		JDialog dialog = new JDialog(frame, title, true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setContentPane(new HighestScoresPanel(dialog));
		dialog.pack();
		dialog.setVisible(true);*/
	}

	// Muesta en un Dialog los stats, [save,] invoca a showBestScores
	private void rankPlayer() {
		Score score = gameMode.getScore();
//		ScorePanel panel = new ScorePanel(score);
		PlayerScoreDialog.showDialog(frame, score);
//		JDialog dialog = new JDialog...
//		dialog.setContentPane(panel);
//		...
//		TODO Aca tengo que verificar si se esta jugando con default settings o no para saber si salvo o no
		if (areDefaultSettings(gameModeSettings)) {
			System.out.println("deafultSettings");
			//PlayerScoreDialog.showDialog(frame, score);
			if (ScoreDAO.trySave(score))
				System.out.println("Score SAVED!!!");
		}
		showBestScores();
	}

	private boolean areDefaultSettings(GameModeSettings settings) {
		GameMode defaultGameMode = GameModeFactory.create(settings.getGameModeType());	
		// .. comparar cada settings y retornar lo q corresponda...
			return true;
	}

	private void gameWon() {
		gamePlay.end();
		String winMsg = "You win!"; // BUNDLE.getString("WinMsg");
		//TODO invokeAndWait: 
			bDrawer.gameWon();
		rankPlayer();
		endGame();
	}
/*
	// A ver... El mismo timer tiene que actualizar el Panel e invocar a timeUpLost
	public void timeUpReset(int seconds) {

	}
*/

	private void gameLost() {
		gamePlay.end();
//		gamePlay = null;
		//TODO invokeAndWait: 
			bDrawer.gameLost();
//		board = null;
		rankPlayer();
		endGame();
	}

//	public void nextTetrimino()
	public void drawNewPiece() {	// public void newPiece
		infoDisplay.setMaxLines(gameMode.linesToLevelUp());

		// TODO esto no hace falta que este aca si ya esta en pieceTouchGround()
		if (gameMode.goalReached()) {
			gameWon();
			return;
		}
		if (gameMode.popUpLines() > 0)
			board.riseUpBoard(gameMode.popUpLines());
		
		final PieceFactory pFactory = PieceFactory.getInstance();
		// en lugar de drawResult: drawOut, blockOut, lockOut
		boolean drawResult = board.drawNewPiece(pFactory.next()); // board.newPiece
		if (drawResult) {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() { 
					//int listSize = pPieces.getFramesUsed();		// nQueue.size()
					int nextPieces = gameModeSettings.getNextPiecesPreview();
					pPieces.displayNextPiece(pFactory.next(nextPieces));
				}
			});
		} else if (gamePlay != null) {
			//gamePlay.end();	// No es necesario llamar a bDrawer.gameLost();
			//rankPlayer();
			gameLost();
		}
	}

	public void pieceTouchGround(boolean hardDrop)
	{
//		try {
//			SwingUtilities.invokeAndWait( new Runnable() {
//				public void run() {
					int linesCleared = 0;
					if (board != null)
						linesCleared = board.clearLines();
					boolean levelUp = gameMode.pieceTouchGround(true, 0, linesCleared);
					if (gameMode.goalReached()) {
						gameWon();
						return;
					}
					if (levelUp) 
						bDrawer.levelUp(); // updatePanel

					if (linesCleared > 0) {
						infoDisplay.updateScoreInfo(gameMode.getScore());
					}
//				}								//TODO GameMod podria updatear el PanelLeft
//			});
//		} catch (Exception e) {
//			System.out.println("lineCompleted INTERRUPTEDDDDDDD");
//			e.printStackTrace();
//		}
/*
		if (board == null) {
			System.out.println("Useful CHECK!!");
			return;
		}
		int[] linesCleared = board.clearLines();
		int dropHigh = 0;
		if (board.lastPieceWasDropped())
			dropHigh = board.getDropHigh();
		boolean dropped;
		int score = gameMode.calculateScore(linesCleared.length, dropped, dropHigh, ...);

		if (linesCleared == null)
			return;

		SwingUtilities.invokeAndWait(
			new Runnable() { public void run() {
				bDrawer.clearLines(lines, lines.length, score);
			}
		});
*/
//		if (gameMode.riseUpBoard() > 0) 
//			board.riseUpBoard(gameMode.riseUpBoard());
	}

	public int getFallTimeInterval() {
		return gameMode.getFallTimeInterval();
	}








	///////////////////////////////////////////////////
	//TODO LO QUE SIGUE ACA ES TESTING O EXPERIMENTAL//
	///////////////////////////////////////////////////


	public String getBackgroundName(int level) {
		return "game.jpg";
		//return "logo.png";
	}
/*
	// Este metodo vuela despues.. es para testing
	public void riseUp(int lines) {
		if (!gamePaused && board != null) {
			board.riseUpBoard(lines);
		}
	}
*/
/*
	// XXX Metodo para debugear
	public void printBoard() {
		System.out.println("Printing Board");
		if (board != null)
			board.drawBoard();
		System.out.println("\n\n");
	}

	// faltan los null checks
	private Runnable runMovePieceDown = new Runnable() {
		public void run() { board.moveDown(); }
	};
	private Runnable runMovePieceLeft = new Runnable() {
		public void run() { board.moveLeft(); }
	};
	private Runnable runMovePieceRight = new Runnable() {
		public void run() { board.moveRight(); }
	};
	private Runnable runMovePieceToExtremeLeft = new Runnable() {
		public void run() { board.moveExtremeLeft(); }
	};
	private Runnable runMovePieceToExtremeRight = new Runnable() {
		public void run() { board.moveExtremeRight(); }
	};
	// falta el if gameMode...
	private Runnable runDropPiece = new Runnable() {
		public void run() { board.drop(); }
	};
	private Runnable runRotatePiece = new Runnable() {
		public void run() { board.rotatePiece(); }
	};

	public void startDemoGame() {
		// TODO
		// crear un algoritmo sencillo en el que ubique a la pieza 
		// de forma que el upperBound sea el mas bajo posible
		// probando todas los movimientos posibles (incluyendo rotates)
		// Eligiendo entre las mejores opciones (las que no dejen o dejen
		// menos huecos debajo; las que borre (mas) lineas, etc)	
	}*/


/*
	// TODO perfectamente esto podria ser el CTOR de NBlock
	public static void createAndShowGUI(final NBlockApp nblockApp)
	{
		String frameTitle = BUNDLE.getString("NBlockAppTitle");
		JFrame frame = new JFrame(frameTitle);
		//BoardDrawer bDrawer = new NBoard(C_WIDTH, C_HEIGHT, 30);
		BoardDrawer bDrawer = new NBoard(graphicsSettings, gameplaySettings.getBoardDimension());
		frame.getContentPane().add((JComponent)bDrawer, BorderLayout.WEST);
		NextPiecesPanel pPieces = new NextPiecesPanel();
		frame.getContentPane().add(pPieces, BorderLayout.EAST);

		NBlockMenuBar menu = new NBlockMenuBar(nblockApp);
		frame.setJMenuBar(menu.getJMenuBar());
	
		// Creo que DO_NOTHING_ON_CLOSE es el default behavior
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent e) { nblockApp.exit(); }
		});
		frame.setVisible(true);
		frame.pack();
	
		frame.setIconImage(new ImageIcon("iconito.jpg").getImage());
		frame.setLocationRelativeTo(null);

//		((JComponent)bDrawer).setFocusable(true);
//		((JComponent)bDrawer).addFocusListener(
		frame.addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) {
				nblockApp.resume();
			}
			public void focusLost(FocusEvent e) {
				nblockApp.pause();
			}
		});

//		KBControl kbControl = new KBControl(nblockApp);
//		frame.addKeyListener(kbControl);
		frame.addKeyListener(new KBControl(nblockApp));
		frame.setFocusable(true);

		nblockApp.setFrame(frame);
		nblockApp.setBoardDrawer(bDrawer);
		nblockApp.setNextPiecesPanel(pPieces);
		//nblockApp.setJMenu(menu);		// XXX se necesita?????????
		//TODO DUDA: para cambiar el KBControl necesito ahcer un: nblockApp.setController(kbControl); ????

	}*/
/*
	public static void main(String[] args)
	{
		final NBlockApp nblockApp = new NBlockApp();
		SwingUtilities.invokeLater(	new Runnable() {
			public void run() {
				//ALT: new NBlocksApp();	// si es que uso ctor
				createAndShowGUI(nblockApp);
			}
		});		// Acordarme que existe un invokeAndWait
	}
*/



}
