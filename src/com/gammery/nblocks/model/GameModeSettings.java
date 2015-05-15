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

public class GameModeSettings
{	
	private GameModeType gameModeType;
	private int startLevel;
	private int initGarbageLines;	// trashLines
	private int nextPiecesPreview;
	private boolean lockDownAtDrop;
	private int boardWidth = 10;

	public void setGameModeType(GameModeType newValue) {
		gameModeType = newValue;
	}
	public GameModeType getGameModeType() { return gameModeType; }
	
	public void setStartLevel(int newValue) {
		startLevel = newValue;
	}
	public int getStartLevel() { return startLevel; }

	public void setInitGarbageLines(int newValue) {
		initGarbageLines = newValue;
	}
	public int getInitGarbageLines() { return initGarbageLines; }

	public void setNextPiecesPreview(int newValue) {
		nextPiecesPreview = newValue;
	}
	public int getNextPiecesPreview() { return nextPiecesPreview; }

	public void setLockDownAtDrop(boolean newValue) {
		lockDownAtDrop = true;
	}
	public boolean isLockDownAtDrop() { return lockDownAtDrop; }

	public void setBoardWidth(int newValue) {
		boardWidth = newValue;
	}
	public int getBoardWidth() {
		return boardWidth;
	}

	// XXX SHOULD BE FINAL
	public int getBoardHeight() { return 20; }
}
