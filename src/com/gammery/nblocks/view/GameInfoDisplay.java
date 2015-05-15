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

// Puedo evitar el "extends JPanel" si uso un JPanel interno y proveeo un getJPanel
package com.gammery.nblocks.view;

import javax.swing.*;
import java.awt.*;
import com.gammery.nblocks.model.*;

public class GameInfoDisplay extends JPanel
{
//	private PieceFrame pHoldPiece;
	private JPanel pGameInfo;
	
	private JLabel hiScore;
	private JLabel myScore;
	private JLabel myLevel;
	private JLabel myLines;
	private int maxLines;
	private int maxLevel;

	public GameInfoDisplay() {
		this(Color.BLACK, 120);
	}

	public GameInfoDisplay(Color bgColor, int pxSize) {
		super(new BorderLayout());//GridLayout(5, 2));
		hiScore = new JLabel("");
		myScore = new JLabel("");
		myLevel = new JLabel("");
		myLines = new JLabel("");

		add(Box.createVerticalGlue());
		JPanel panel = new JPanel(new GridLayout(5, 2));
		
		panel.add(new JLabel("Hi-Score: "));
		panel.add(hiScore);
		panel.add(new JLabel("Score: "));
		panel.add(myScore);
		panel.add(new JLabel("Level: "));
		panel.add(myLevel);
		panel.add(new JLabel("Lines: "));
		panel.add(myLines);
		add(panel, BorderLayout.SOUTH);

		reset();
	}

	public void reset() {
		System.out.println("reset de GameInfoDisplay");
		hiScore.setText("--");
		myScore.setText("--");	
		myLevel.setText("--");	
		myLines.setText("--");
	}


	public void setHighestScore(long points) {
		final String txtScore = String.valueOf(points);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { hiScore.setText(txtScore); }
		});
	}

	
	

	public void updateScoreInfo(Score score) {	
		final String txtScore = String.valueOf(score.getScore());
		final String txtLevel = String.valueOf(score.getLevel());
		final String txtLines = String.valueOf(score.getLinesCleared());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				myScore.setText(txtScore);
				myLevel.setText(txtLevel + " / " + maxLevel);
				myLines.setText(txtLines + " / " + maxLines);
			}
		});
	}

	public void setMaxLevel(int newValue) {
		maxLevel = newValue;
	}

	public void setMaxLines(int newValue) {
		maxLines = newValue;
	}
	
	public static void main (String [] args)
	{
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					JFrame f = new JFrame("");
					f.setContentPane(new GameInfoDisplay());
					f.pack();
					f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					f.setVisible(true);
				}
			}
		);
	}

/*	public void setHoldPiece(Piece piece) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {	holdPiece.drawPiece(piece); }
		});
	}*/
	
}
