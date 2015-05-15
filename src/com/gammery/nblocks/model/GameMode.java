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

import com.gammery.nblocks.view.NextPiecesPanel;
//import nblock.scoremanager.*;
//import nblock.model.GameplaySettings;

public abstract class GameMode {
	//backToBackBonus	(( TODO esto deberia estar en las subclases))
	private Score score;
	private boolean lockDownAtDrop;
	private int curLvlLinesCleared = 0;
	// El propósito de estos métodos es proveer una "interfaz" (privada) a las subclases solamente
	protected int getSlowestFallTimeInterval() {
		return 1600;
	}	// slowestFall;

	protected int getFastestFallTimeInterval() {
		return 200;
	} // fastestFall;
	
	protected void incrementLevel() {
	score.incrementLevel();
	}
	
	protected int getCurrentLevel() {
		return score.getLevel();
	}
	
	protected void addLinesCleared(int newValue) {
	score.addLinesCleared(newValue);
	}
	
	protected int getLinesCleared() { 
		return score.getLinesCleared(); 
	}
	
	protected boolean updateLinesClearedAndLevel(int linesCleared) {
		curLvlLinesCleared += linesCleared;
		addLinesCleared(linesCleared);
		//if (getLinesCleared() != 0 && ((getLinesCleared() % linesToLevelUp()) == 0)) {
		if (curLvlLinesCleared >= linesToLevelUp()) {
			System.out.println("fallInterval:: " + getFallTimeInterval());
			curLvlLinesCleared = curLvlLinesCleared - linesToLevelUp();
			incrementLevel();
			System.out.println("        LevelUP!:: " + getLinesCleared() + " % " + linesToLevelUp());
			return true;
		}
		return false;
	}
	///////////////////////////////////////////////////////7

	public GameMode(GameModeType gmt, int startLevel) {
		//score = nblock.view.SarazaService.createScore(gmt, startLevel);
		score = ScoreDAO.createScore(gmt, startLevel);
	}

	public Score getScore() { 
//		score.setRank(nblock.view.SarazaService.getRank(score));
		/*score.setRank(*/ScoreDAO.setRankingPosition(score)/*)*/;
		return score;
	}

	public int getFallTimeInterval() {
		int slowestFall = getSlowestFallTimeInterval();
		int fastestFall = getFastestFallTimeInterval();
		int step = (slowestFall - fastestFall) / (getFinalLevel()-1); //(getCurrentLevel()-1);
		int tmp = step * (getCurrentLevel()-1);
		return slowestFall - tmp;
	}
	
	public int getNextPiecesPreview() {
		return NextPiecesPanel.MAX_PANELS;
	}

	public int getBoardWidth() {
		return 10;
	}


	public int popUpLines() {	// linesToPopUp
		return 0;	// if (getCurrentLevel() > 5 && getCurrentLevel() % 3) return 4;
	}
	// O podria retornar -1
	public int getLevelTimeout() { return 0; }	// max Time per level // getLevelTimeOver()
	public int getGameTimeout() { return 0; }	// getTotalTimeOver()

	public void setLockDownAtDrop(boolean lockDown) {
		lockDownAtDrop = lockDown;
	}	
	public boolean isLockDownAtDrop() { 
		return lockDownAtDrop;
	}

	public abstract boolean goalReached();	// || matchWinned()
	public abstract int getFinalLevel();
	// boolean para indicar si hubo level up // OR retornar el score que se hizo con la jugada
	public abstract boolean pieceTouchGround(boolean hardDrop, int dropHigh, int linesCleared);
	public abstract int linesToLevelUp(); //tambien solo seria util para mostrar info del gamemode
	//public abstract int leftLinesToLevelUp(); //tambien solo seria util para mostrar info del gamemode
	// lineClearsToLevelUp	
	//protected int linesClearedToLevelUp() { 
//	protected int clearLinesToLevelUp() { 	// esto es confuso, las que faltan o las necesarias???
//		return 10;
//	}
}


class Sprint extends GameMode
{
	public Sprint(int startLevel) {
		super(GameModeType.SPRINT, startLevel);
		System.out.println("Sprint!");	
		setLockDownAtDrop(true);
	}

	public boolean goalReached() {
		if (getCurrentLevel() > getFinalLevel())
			return true;
		return false;
		// OR:: if(getLinesCleared() >= linesToLevelUp()) return true; return false;
	 }
	public int getFinalLevel() { return 1; }
	public boolean pieceTouchGround(boolean hardDrop, int dropHigh, int linesCleared) {
		boolean levelUp = updateLinesClearedAndLevel(linesCleared);
		return levelUp;
	}
	public int linesToLevelUp() { return 532; }

	// podria ir decrementando de una base (ie. 900) por cada 4 linesClear
	@Override
	public int getFallTimeInterval() {
		return 250;
		/* int step = 50; int base = 1000;
		 * int tmp = (getLinesCleared() / (linesToLevelUp() / 10)) * step;
		 * return (base - tmp);
		*/
	}
}

class Marathon extends GameMode
{
//	private boolean lockDownAtDrop;

	public Marathon(int startLevel) {
		super(GameModeType.MARATHON, startLevel);
		setLockDownAtDrop(true);
	}

//	public boolean lockDownAtDrop() { return lockDownAtDrop; }
	public boolean goalReached() {
		if (getCurrentLevel() > getFinalLevel())
			return true;
		return false;
		// OR:: if(getLinesCleared() >= linesToLevelUp()) return true; return false;
	 }
	public int getFinalLevel() { return 6; }
	public boolean pieceTouchGround(boolean hardDrop, int dropHigh, int linesCleared) {
		boolean levelUp = updateLinesClearedAndLevel(linesCleared);
		return levelUp;
	}
	public int linesToLevelUp() { return 8; }
	// podria ir decrementando de una base (ie. 900) por cada 4 linesClear
	@Override
	public int getFallTimeInterval() {
		return super.getFallTimeInterval();
		/* int step = 50; int base = 1000;
		 * int tmp = (getLinesCleared() / (linesToLevelUp() / 10)) * step;
		 * return (base - tmp);
		*/
	}
}
























/*
class Marathon implements GameMode
{
	private final int goalLevel = 15;

	public Marathon() { this(1); }
	public Marathon(int startLevel) {
		if (startLevel < 1 || startLevel > goalLevel)
			startLevel = 1;
		currentLevel = startLevel;
	}

	// esto deberia estar "arriba" en la jerarquia pero como goalLevel cambia segun las distintas implementaciones entonces CREO que es necesario usar un get()
	public boolean goalReachedl() { 		// || goalLevel
		if (getCurrentLevel() < goalLevel)
			return false;
		return true;
	 }

	public int getGoalLevel() { return 15; }
	public int getLinesPerLevel() { return 10; }
	public boolean lockdownAtDrop() { return true; }

	// este metodo tiene que actualizar el currentLevel
	public int calculateScore(boolean hardDrop, int dropHigh, int linesCleared) {
		// para el calculo se va a basar en el actual level
		// Tambien podria recibir el un boolean indicando un T-Spin
	}

	public int popUpLines() {
		return 0;
		// Si nunca tiene que subir nada entonces tiene que devolver -1 o 0
		// Para los gamemodes que si usan riseUp entonces deben usar el criterio
		// del currentLevel
	}
}
*/
// El Score debe guardar el GameMode jugado tambien, y si tengo un GameMode Race, el tiempo tambien.


/* getTimeoutToMoveDown
 * getFallSpeedTime
 * getTimeoutToDrop
 */


// Modes
// Marathon: 15 niveles, 10 linea por nivel, timeout (no se todavia)
// Sprint: 1 nivel, el objetivo es limpiar 40 lineas en el menor tiempo posible
// Master: auto drop sin lock down, timeout per level, 10 niveles.
// Race: 1 nivel, el objetivo es limpiar la mayor cantidad de lineas en 120 segundos
// Survive: sin Mirror (ghostpiece), + Master + pieza  invisible cuando toka el piso
// ALgun que otro modo con rise per level
