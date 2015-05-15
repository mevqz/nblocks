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

public enum GameModeType {
		// TODO averiguar si puedo usar Bundles para los argumentos ...
	// Tal vez en este mismo enum pueda agregar las descripciones de cada modo como otro argumento mas


	SPRINT("Sprint"),
	MARATHON("Marathon");
//	CHALLENGE("Challenge"),
//	RACER("Racer"),
//	MASTER("Master");

	private String gameModeName;
	private GameModeType(String name) {
		gameModeName = name;
	}
	public String toString() {
		return gameModeName;
	}
}
