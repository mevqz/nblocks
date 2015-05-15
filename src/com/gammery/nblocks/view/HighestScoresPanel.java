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
import com.gammery.nblocks.model.*;
//import nblock.scoremanager.*;
import java.awt.event.*;
import java.util.*;

public class HighestScoresPanel extends JPanel implements ActionListener
{	
	private JComboBox gameModesCombo;
	private JButton btnClose;
	private JTable scoreTable;
	private java.awt.Window parentContainer;

	@Override
	public void finalize() {
		System.out.println("Fuck Yeah!");
	}

	public HighestScoresPanel(java.awt.Window container) {	// recibir ref al component "padre"
		parentContainer = container;
		gameModesCombo = new JComboBox(new GameModeTypeComboModel());
		gameModesCombo.setSelectedIndex(0);
		gameModesCombo.addActionListener(this);
		JLabel gameModeLabel = new JLabel("Game Mode");

		GameModeType gameMode = (GameModeType) gameModesCombo.getSelectedItem();
		ScoreTableModel model = new ScoreTableModel(ScoreDAO.getHighestScores(gameMode));
		scoreTable = new JTable(model);
		
		btnClose = new JButton("Close");
		btnClose.addActionListener(this);
		
		add(gameModeLabel);
		add(gameModesCombo);
		add(new JScrollPane(scoreTable));	// new JScrollPane(scoreTable)
		add(btnClose);
		
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == gameModesCombo) {
			GameModeType gameMode = (GameModeType) gameModesCombo.getSelectedItem();
			List<Score> scores = ScoreDAO.getHighestScores(gameMode);
			System.out.println("scores.length=" + scores.size());
			//ScoreTableModel model = new ScoreTableModel(scores);
			//scoreTable.setModel(model);
			ScoreTableModel model = (ScoreTableModel) scoreTable.getModel();
			model.updateModel(scores);
		} else
			//parentContainer.setVisible(false);
			parentContainer.dispose();
//			System.exit(1);
			// ... hide/close window
	}


	// XXX CLASS FOR TESTING
	public static class Test {
		public static void createAndShowGUI() {
			JFrame f = new JFrame("ADS");
			f.getContentPane().add(new HighestScoresPanel(f));
			f.setVisible(true);
			f.pack();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		public static void main (String [] args) {
			SwingUtilities.invokeLater(	new Runnable() {
				public void run() {	createAndShowGUI();	}
			});
		}
	}
}



