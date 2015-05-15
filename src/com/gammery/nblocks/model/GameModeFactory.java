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

/*
 *	Tambien seria posible usar una sola clase GAME MODE
 *	Y en el switch case dependiendo del tipo, setear el behavior
 *
 *
 * */


public class GameModeFactory
{
/*	private static final GameModeFactory gmf = new GameModeFactory();
	private GameModeFactory() { }

	public static GameModeFactory getInstance() {
		return gmf;
	}
*/
	public static GameMode create(GameModeSettings settings) {
		return create(settings.getGameModeType(), 
			settings.getStartLevel(), settings.isLockDownAtDrop());
	} 

	public static GameMode create(GameModeType type) {
		return create(type, 1);
	}

	// FIXME Este creo que puede ser PRIVATE
	private static GameMode create(GameModeType type, int startLevel, boolean lockDown) {
		GameMode gameMode = create(type, startLevel);
		gameMode.setLockDownAtDrop(lockDown);
		return gameMode;
	}

	//public GameMode create(GameModeType gmt, int startLevel, boolean lockDown) {
	public static GameMode create(GameModeType type, int startLevel) {
		GameMode gameMode = null;
		type = (type == null) ? GameModeType.values()[0] : type;
		switch (type) {
			case SPRINT: gameMode = new Sprint(startLevel);
				break;
			case MARATHON: gameMode = new Marathon(startLevel);
				break;
//			case CHALLENGE: gameMode = new Challenge();
//				break;
//			case RACER: gameMode = new Racer();
//				break;
//			case MASTER: gameMode = new Master();
//				break;

		}
		return gameMode;
	}
}
