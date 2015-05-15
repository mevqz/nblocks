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

import com.gammery.nblocks.model.*;
import javax.swing.*;

class GameModeTypeComboModel extends AbstractListModel implements ComboBoxModel
{
	private GameModeType[] gameModes = GameModeType.values();
	private GameModeType selection = null;

	public GameModeType getElementAt(int index) {
		return gameModes[index];
	}

	public int getSize() {
    	return gameModes.length;
	}

	public void setSelectedItem(Object item) {
		selection = (GameModeType) item; 
	}

	public GameModeType getSelectedItem() {
		return selection;
	}
}
