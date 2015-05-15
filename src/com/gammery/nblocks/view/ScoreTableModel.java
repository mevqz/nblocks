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

import javax.swing.*;
import javax.swing.table.*;
//import nblock.model.*;
import com.gammery.nblocks.model.Score;
//import java.awt.event.*;
import java.util.*;


class ScoreTableModel extends AbstractTableModel
{
	// Name		Score	Level	Lines	// podria usar tooltips para mostrar mas info
	private List<Score> scores;	
	private String[] columnNames;

	public ScoreTableModel(List<Score> sc) {
		scores = sc;
		// TODO usar un Resource Bundle
		columnNames = new String[]{ "Name", "Score", "Level", "Lines" };
	}

	public void updateModel(List<Score> sc) {
		scores = sc;
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	} 	

	@Override	
	public int getRowCount() {
		return scores.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length; 
	}

	@Override
	public Object getValueAt(int row, int col) {
		Score score = scores.get(row);
		Object value = null;
		switch (col) {
			case 0: value = score.getPlayerName(); break;
			case 1: value = score.getScore(); break;
			case 2: value = score.getLevel(); break;
			case 3: value = score.getLinesCleared(); break;
		}
		return value;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
