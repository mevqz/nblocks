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

import com.gammery.nblocks.model.*;
import com.gammery.nblocks.view.*;
import java.util.concurrent.*;
import javax.swing.*;

public class GamePlay2 implements Runnable
{	
	private Thread movePiece;
	private Object lock = new Object();
	private boolean gameOver;
	private volatile boolean gamePaused;
	private volatile boolean hardDrop;
	private	NBlockApp nblocks;
//	private ExecutorService exec;

	public GamePlay2(NBlockApp newNBlock) { 
		nblocks = newNBlock;
	}
	// FIXME Tal vez (Tal vez) se pueda unificar estos dos 
	// metodos y crear un solo pause que cumpla las 2 funciones
	public void pause() {
//		System.out.println("pause()");
		gamePaused = true;
		movePiece.interrupt();
	}
	public void resume() {
//		System.out.println("resume()");
		synchronized (lock) {
			gamePaused = false;
			lock.notifyAll();
		}
	}

	//public void dropLockDown() {
	public void lockDownAtDrop() {
		hardDrop = true;
		movePiece.interrupt();
	}
	
	// TODO Checkear esto
	public void end() {
//		System.out.println("GamePlay.end()");
		gameOver = true;
		movePiece.interrupt();
	}

	public void run()
	{
		try {	
			while (!gameOver) {
				nblocks.drawNewPiece();		// nblocks.nextPiece()
				int millisec = nblocks.getFallTimeInterval();
				do {
					if (gamePaused) {
						synchronized (lock) {
							lock.wait();
						}
					}
					hardDrop = false;
					movePiece = new Thread(new MoveDownPiece(millisec));
					movePiece.start();
					movePiece.join();
				} while (gamePaused);
	//			TimeUnit.MILLISECONDS.sleep(100);
				nblocks.pieceTouchGround(hardDrop);	// nblocks.pieceLockDown
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	private class MoveDownPiece implements Runnable {
		private int millisec;
	
		public MoveDownPiece(int time) {
			millisec = time;
		}

		public void run() {
			try {
				do {
					TimeUnit.MILLISECONDS.sleep(millisec);
				} while (nblocks.movePieceDown());
			} catch (InterruptedException e) { 
				// Esta excepcion seria lanzada si se invoco a lockDownAtDrop (despues de un drop)
				// y el llamado a movePieceDown() es para forzar que la pieza
				// colisione con el "piso" para que se desencadene un lockDown,
				// es decir, que el tetrimino quede fuera del juegoy se proceda 
				// con el siguiente tetrimino inmediatamente.
				nblocks.movePieceDown();
			}
		}
	}
}
