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

package com.gammery.nblocks.controller;

import java.awt.event.*;
//import java.util.concurrent.*;
import com.gammery.nblocks.model.Board;
import com.gammery.nblocks.NBlockApp;

public class KBCtrl extends KeyAdapter // implements KeyListener
{
//	private ExecutorService exec;
	private NBlockApp nblock;
	private ControlSettings control;
//	{ exec = Executors.newSingleThreadExecutor(); }
		
//	public KBControl(NBlockApp nblock) {
//		this(nblock, new ControlSettings());
//	}
	public KBCtrl(NBlockApp nblock, ControlSettings settings) {
		this.nblock = nblock;
		control = settings;
	}

	/*
	 * Anteriormente yo hacia casi todos los llamado usando el newSingleThreadExecutor.
	 * Ahora creo que ya no es necesario, ademas creo que le compete a NBlockApp 
	 * ese detalle (creo). 
	 * Rescato un detalle nomas: cuando hacia esto se me ocurrio crear y mantener ref
	 * a Runnables para cada tipo de llamado y luego dentro de cada if/else hacia:
	 * exec.execute(runMovePieceToLeft);
	 * Lo cual es mas eficiente ya que no estoy creando un objeto nuevo cada vez
	 * que se aprieta una tecla. runMovePieceToLeft es obviamente un Runnable que invoca
	 * a nblock.movePieceToLeft()
	 * */

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
	
		if (keyCode == control.keyMoveLeft) {
			nblock.movePieceToLeft();
		} else if (keyCode == control.keyMoveExtremeLeft) {
			nblock.movePieceToExtremeLeft();
		} else if (keyCode == control.keyMoveRight) {
			nblock.movePieceToRight();
		} else if (keyCode == control.keyMoveExtremeRight) {
			nblock.movePieceToExtremeRight();
		} else if (keyCode == control.keyMoveDown) {
			nblock.movePieceDown();
		} else if (keyCode == control.keyDrop) {
			nblock.dropPiece();
		} else if (keyCode == control.keyRotateLeft) {
			nblock.rotatePieceToLeft();
		} else if (keyCode == control.keyRotateRight) {
			nblock.rotatePieceToRight();
		} else if (keyCode == control.keyPause) {
			//exec.execute(new Runnable() { 
				/*public void run() {*/	nblock.changePauseStatus();// }
			//});
		} else if (keyCode == KeyEvent.VK_N) {
			nblock.startNewGame();
		}
	}
	//public void keyTyped(KeyEvent e){}
	//public void keyReleased(KeyEvent e){}
}	
