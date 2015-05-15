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
import java.awt.event.*;
import java.awt.*;

import com.gammery.nblocks.model.*;
import com.gammery.nblocks.*;
//import nblock.scoremanager.*;

public class PlayerScoreDialog
{
	static class Test {
//		static Score sc = new Score();//ScoreServiceTest.createScore(GameModeType.values()[0], 1);
//		sc.setPlayerName("nocturne");
//		sc.setRank(3);
		public static void main(String args[]) {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					final JFrame f = new JFrame("Test");
					f.setSize(200,200);
					f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					f.setLocationRelativeTo(null);
					JButton b = new JButton("TEST");
					b.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) { 
							Score sc = new Score();
							sc.setPlayerName("nocturne");
							sc.setRank(-1);
							sc.setGameModeType(GameModeType.values()[0]);
							showDialog(f, sc); 
						}	
					});
					f.add(b);
					f.setVisible(true);
				}
			});
		}
	}


	private static JDialog dialog;
	private static JTextField playerName;
	public static void showDialog(JFrame frame, Score score) {
		String title = "Player Score";	// TODO resource bundle...
		dialog = new JDialog(frame, title, true);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.setContentPane(createContentPane(score));	
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	private static JPanel createContentPane(final Score score) {
		JPanel panel = new JPanel(new GridLayout(6, 2));

		String txtRank = "Rank: ";
		panel.add(new JLabel(txtRank));
		String rankPosition = (score.getRank() < 0) ? "Not Ranked" : String.valueOf(score.getRank());
		panel.add(new JLabel("#" + rankPosition));//score.getRank()));
		
		String txtScore = "Score: ";// + score.getScore();
		panel.add(new JLabel(txtScore));
		panel.add(new JLabel(String.valueOf(score.getScore())));

		String txtLevel = "Level: ";
		panel.add(new JLabel(txtLevel));
		panel.add(new JLabel(String.valueOf(score.getLevel())));

		String txtLines = "Lines: ";
		panel.add(new JLabel(txtLines));
		panel.add(new JLabel(String.valueOf(score.getLinesCleared())));

		String txtGameMode = "Game mode: ";
		panel.add(new JLabel(txtGameMode));
		panel.add(new JLabel(score.getGameModeType().toString()));

		ActionListener actionSaveName = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (score.getRank() < 0)
					dialog.dispose();
				if (playerName.getText() != "") {
					score.setPlayerName(playerName.getText());
					dialog.dispose();
				} else
					JOptionPane.showMessageDialog(null, "Enter your name!");	
		}};

		String txtName = "Player name: ";
		panel.add(new JLabel(txtName));
		playerName = new JTextField(16);
		playerName.setText(score.getPlayerName());
		playerName.addActionListener(actionSaveName);
		//fieldName.setFocus...
		panel.add(playerName);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(panel);
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.LINE_AXIS));
		bottom.add(Box.createHorizontalGlue());
		JButton btnAccept = new JButton("Accept");
		if (score.getRank() < 0)
			playerName.setEnabled(false);
//		else {
			btnAccept.addActionListener(actionSaveName);
	//				if (score.getRank() != -1) {	
						//if (e.getSource().getText() != "") {// verificar mejor...
	//					if (e.getActionCommand() != "") {// verificar mejor...
						//	score.setPlayerName(e.getSource().getText());
	//						score.setPlayerName(e.getActionCommand());
	//						dialog.dispose();
	//					} else
	//						JOptionPane.showMessageDialog(null, "Enter your name!");	
	//				}
//			});
//		}

		bottom.add(btnAccept);	
		bottom.setBorder(BorderFactory.createEmptyBorder(16, 4, 4, 4));
		contentPane.add(bottom, BorderLayout.SOUTH);
	
		return contentPane;
	}

	private static void savePlayerName() {
	}
}
