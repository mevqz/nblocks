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

import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;


public class Score implements Serializable
{
	private static final int PLAYER_NAME_LENGTH = 32;
//	public static final int RECORD_LENGTH = 296; //248; //PLAYER_NAME_LENGTH + 28 + 8; (el total seria 239 + ... // 272
	public static final int RECORD_LENGTH = 335; //248; //PLAYER_NAME_LENGTH + 28 + 8; (el total seria 239 + ... // 272
	private transient int rank;
	private int score;
	private int linesCleared;
	private int level;
	private String playerName;
	//private Date date = Calendar.getInstance().getTime();
	private long date = Calendar.getInstance().getTime().getTime();
	private GameModeType gameModeType;
	private long totalTime;		//TODO Agregar SETTERS // Es para llevar el control del tiempo

	public Score() { }

	public void setRank(int newValue) {
		rank = newValue;
	}
	public int getRank() {
		return rank;
	}

	public int getScore() { return score; }
	public void setScore(int sc) { score = sc; }

	public int getLinesCleared() { 	return linesCleared; }
	public void setLinesCleared(int ln) { linesCleared = ln; }
	
	public void addScore(int points) {	
		setScore(getScore() + points);
	}

	public void addLinesCleared(int lines) {
		setLinesCleared(getLinesCleared() + lines);
	}

	public void incrementLevel() {
		setLevel(getLevel() + 1);
	}


	public int getLevel() { return level; }
	public void setLevel(int lvl) { level = lvl; }

	public String getPlayerName() { return playerName.trim(); }
	public void setPlayerName(String n) {
		StringBuilder sb = new StringBuilder(n);	// tal vez usando string.format se abarque los dos casos
		sb.setLength(PLAYER_NAME_LENGTH);
		playerName = sb.toString();
		//System.out.println("Score.setPlayerName()::playerName.length:"+playerName.length());
//		while (n.length() < PLAYER_NAME_LENGTH)
//			sb.append(" ");							// averiguar si length cuenta los \0'
//		if (n.length() > PLAYER_NAME_LENGTH)
//			n = n.substring(0, PLAYER_NAME_LENGTH);	// FIXME -1 ??
	
//		playerName = n;
	}

	public long getDate() {	return date; }	// or getTime

	public void setGameModeType(GameModeType newValue) {
		gameModeType = newValue;
	}
	public GameModeType getGameModeType() {
		return gameModeType;
	}

//	@Override
//	public boolean equals(Object object) {
//		Score score = (Score) object;
//		if (score.getScore() == this.score)/* &&
//			score.getPlayerName() == playerName &&
//			score.getDate() == date && 
//			score.getLevel() == level &&
///			score.getGameModeType() == gameModeType &&
//			score.getLinesCleared() == linesCleared)*/
//				return true;	
//		return false;
//	}
}
