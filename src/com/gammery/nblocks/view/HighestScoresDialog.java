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
import java.awt.event.*;
import java.awt.*;
import java.awt.event.*;
//import nblock.scoremanager.*;

import com.gammery.nblocks.model.*;

public class HighestScoresDialog
{
	private static JDialog dialog;

	private static JComboBox boxGameMode;
	private static JTable tableScore;
	private static JButton btnClose;

	static class Test {
		public static void main(String args[]) {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					final JFrame f = new JFrame("Test");
					f.setSize(200,200);
					f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					f.setLocationRelativeTo(null);
					JButton b = new JButton("TEST");
					b.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) { showDialog(f, null); }	
					});
					f.add(b);
					f.setVisible(true);
				}
			});
		}
	}

	public static void showDialog(JFrame frame, GameModeType selection) {	// final arg
		if (dialog == null) {
			initDialogAndComponents(frame); 
			dialog.setContentPane(createContentPane());	
			dialog.pack();
			dialog.setResizable(false);
		} 
		if (selection == null)
			boxGameMode.setSelectedIndex(0);
		else
			boxGameMode.setSelectedItem(selection);
//		updateTableModel();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	private static void initDialogAndComponents(JFrame frame) {
		String title = "Highest Scores";	// TODO resource bundle...
		dialog = new JDialog(frame, title, true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		boxGameMode = new JComboBox(new GameModeTypeComboModel());
		boxGameMode.setSelectedIndex(0);
		GameModeType gameMode = (GameModeType) boxGameMode.getSelectedItem();
		ScoreTableModel model = new ScoreTableModel(ScoreDAO.getHighestScores(gameMode));
		tableScore = new JTable(model);	
		TableColumn column = null;
		column = tableScore.getColumnModel().getColumn(0);
		column.setPreferredWidth(100);
/*		column = tableScore.getColumnModel().getColumn(1);
		column.setPreferredWidth(50);
		column = tableScore.getColumnModel().getColumn(2);
		column.setPreferredWidth(50);
		column = tableScore.getColumnModel().getColumn(3);
		column.setPreferredWidth(50);*/

		String strClose = "Close";	//TODO bundle
		btnClose = new JButton(strClose);

		boxGameMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//updateTableModel();
				GameModeType gmt = (GameModeType) boxGameMode.getSelectedItem();
				java.util.List<Score> highestScores = ScoreDAO.getHighestScores(gmt);
				ScoreTableModel model = (ScoreTableModel) tableScore.getModel();
				model.updateModel(highestScores);
			}
		});
		btnClose.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) { dialog.dispose(); }	// FIXME dialog no es null en este momento?
		});
	}

	private static JPanel createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());

		JPanel gameModePanel = new JPanel();
		gameModePanel.setLayout(new BoxLayout(gameModePanel, BoxLayout.LINE_AXIS));
		gameModePanel.add(new JLabel("Game mode: "));
		gameModePanel.add(boxGameMode);

		contentPane.add(gameModePanel, BorderLayout.NORTH);
		contentPane.add(new JScrollPane(tableScore));

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(btnClose);
	
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		return contentPane;
	}
}
