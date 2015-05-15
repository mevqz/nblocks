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

public class GameplaySettings
{
	private boolean mirrorPieceEnabled;		// piece lands preview		// showMirror
	private boolean randomColoredPieces;
	private boolean kickWallEnabled;

	public GameplaySettings() {
		mirrorPieceEnabled = true;
		randomColoredPieces = false;
		kickWallEnabled = true;
	}

	public void setMirrorPieceEnabled(boolean newValue) {
		mirrorPieceEnabled = newValue;
	}
	public boolean isMirrorPieceEnabled() {
		return mirrorPieceEnabled;
	}
	
	public void setRandomColoredPieces(boolean newValue) {
		randomColoredPieces = newValue;
	}
	public boolean isRandomColoredPieces() {
		return randomColoredPieces;
	}
	
	public void setKickWallEnabled(boolean newValue) {
		kickWallEnabled = newValue;
	}
	public boolean isKickWallEnabled() {
		return kickWallEnabled;
	}
}
/*
	private static final Dimension classicSize = new Dimension(10, 20);
	private static final Dimension nblocksSize = new Dimension(12, 20);
	private Dimension boardDimension;
*/

//	public int boardWidth;
//	public int boardHeight;


	// Este CTOR paraceria no necesario porq cuando se llama a showDialog lo updatea segun el gmt 
	// pero si nunca se invoca a showDialog entonces estaria mal...
	// SOLUCIONES: Antes de iniciar la primer partida se debe invocar a showGameplayDialog
/*	public GameplaySettings() {
		gameModeType = GameModeType.values()[0];
		startLevel = 1;
		mirrorEnabled = true;
		initGarbageLines = 0;
		setClassicSize(); 
	}
*/




/*


	public void setClassicSize() {		
		boardDimension = new Dimension(classicSize);
	}
	public void setWideSize() {
		boardDimension = new Dimension(nblocksSize);
	}

	public void setUltraWideSize() {
		boardDimension = new Dimension(nblocksSize);
	}

	public Dimension getBoardDimension() {
		return boardDimension;
	}

	public void setMirrorPieceEnabled(boolean newValue) {
		mirrorEnabled = newValue;
	}
	public boolean isMirrorPieceEnabled() {
		return mirrorEnabled;
	}


	public void setKickWallEnabled(boolean newValue) {
		kickWallEnabled = newValue;
	}
	public boolean isKickWallEnabled() {
		return kickWallEnabled;
	}


	public void setRandomColoredPiece(boolean newValue) {
		randomColoredPiece = newValue;
	}	
	public boolean isRandomColoredPiece() {
		return randomColoredPiece;
	}
*/

	// POR COMO QUIERO QUE FUNCIONE todo es necesario que este objeto se pase a GameModeFactory y q el GameMode utilice este objeto
	// Por lo que el metodo create de GameModeFactory debe recibir un GameplaySettings / GameModeSettings ..... ALT no debera recibir tambien un gmt?
	// GameModeFactory.create(gameplaySettings);
	// OR
	// gameMode.setSettings(gameplaySettings)
//}
