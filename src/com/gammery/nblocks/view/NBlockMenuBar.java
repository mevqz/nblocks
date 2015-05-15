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

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import com.gammery.nblocks.model.GameplaySettings;
import com.gammery.nblocks.NBlockApp;

	
	//TODO Esto deberia ser un metodo dentro de NBlockApp
	//private JMenuBar createMenuBar(final NBlockApp nblock) { ... }
	//createMenuBar(this);

public class NBlockMenuBar
{
	private NBlockApp nblock;
	private JMenuBar menuBar;
	private JMenuItem itemGameModeSettings;
	private JCheckBoxMenuItem randomColoredItem;

	public void setGameModeSettings(boolean newValue) {
		itemGameModeSettings.setEnabled(newValue);
	}
	
	public void setRandomColoredPieces(boolean newValue) {
		randomColoredItem.setEnabled(newValue);
	}

	public NBlockMenuBar(NBlockApp app, GameplaySettings gameplay) {//boolean kickWallStatus, boolean boolean colorStatus, ) {
		nblock = app;

		// Game Menu
		JMenu gameMenu = new JMenu(NBlockApp.BUNDLE.getString("GameMenu"));	
	
		JMenuItem itemStartNewGame = new JMenuItem(NBlockApp.BUNDLE.getString("NewGame"));
		gameMenu.add(itemStartNewGame);
		itemStartNewGame.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) { nblock.startNewGame(); }
		});

		JMenuItem itemResetGame = new JMenuItem(NBlockApp.BUNDLE.getString("ResetGame"));
		gameMenu.add(itemResetGame);
		itemResetGame.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) { nblock.startNewGame(false); }
		});

		gameMenu.addSeparator();

		JMenuItem itemMultiplayer = new JMenuItem(NBlockApp.BUNDLE.getString("Multiplayer"));
		itemMultiplayer.setEnabled(false);
		gameMenu.add(itemMultiplayer);
		
//		itemPreferences = new JMenuItem(msg.getString("Preferences"));
//		gameMenu.add(itemPreferences);
//		itemPreferences.addActionListener( new ActionListener() {
//			public void actionPerformed(ActionEvent e) { nblock.showPreferences(); }
//		});

		JMenuItem itemBestScores = new JMenuItem(NBlockApp.BUNDLE.getString("BestScores"));
		gameMenu.add(itemBestScores);
		itemBestScores.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) { nblock.showBestScores(); }
		});

		JMenuItem itemDemoGame = new JMenuItem(NBlockApp.BUNDLE.getString("DemoGame"));
		itemDemoGame.setEnabled(false);
		gameMenu.add(itemDemoGame);
//		itemDemoGame.addActionListener( new ActionListener() {
//			public void actionPerformed(ActionEvent e) { nblock.startDemoGame(); }
//		});

		gameMenu.addSeparator();

		JMenuItem itemEndGame = new JMenuItem(NBlockApp.BUNDLE.getString("EndGame"));
		gameMenu.add(itemEndGame);
		itemEndGame.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) { nblock.endGame(); }
		});

		JMenuItem itemExit = new JMenuItem(NBlockApp.BUNDLE.getString("Exit"));
		gameMenu.add(itemExit);
		itemExit.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) { nblock.exit();	}
		});

		// Preferences Menu
		JMenu preferencesMenu = new JMenu(NBlockApp.BUNDLE.getString("Preferences"));

		JMenuItem itemGraphicsSettings = new JMenuItem("Graphics settings");// msg.getString(""));
		preferencesMenu.add(itemGraphicsSettings);
		itemGraphicsSettings.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) { nblock.showGraphicsSettings(); }
		});

		itemGameModeSettings = new JMenuItem("Game mode settings");// msg.getString(""));
		preferencesMenu.add(itemGameModeSettings);
		itemGameModeSettings.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) { nblock.showGameModeSettings(); }
		});

		JMenuItem itemControlSettings = new JMenuItem("Control Settings");
		preferencesMenu.add(itemControlSettings);
		itemControlSettings.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) { nblock.showControlSettings(); }
		});
		itemControlSettings.setEnabled(false);


		// Sub Menu
		JMenu gameplayMenu = new JMenu("Gameplay Settings");

		JCheckBoxMenuItem kickWallItem = new JCheckBoxMenuItem("Kick wall");
		gameplayMenu.add(kickWallItem);
		kickWallItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
				nblock.setKickWall(item.isSelected());
			}
		});
		kickWallItem.setSelected(gameplay.isKickWallEnabled());

		JCheckBoxMenuItem mirrorPieceItem = new JCheckBoxMenuItem("Show Mirror Piece");
		gameplayMenu.add(mirrorPieceItem);
		mirrorPieceItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
				nblock.showMirrorPiece(item.isSelected());
			}
		});
		//mirrorPieceItem.setSelected(true);
		mirrorPieceItem.setSelected(gameplay.isMirrorPieceEnabled());

		randomColoredItem = new JCheckBoxMenuItem("Random Colored Pieces");
		gameplayMenu.add(randomColoredItem);
		randomColoredItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
				nblock.showMirrorPiece(item.isSelected());
			}
		});
		randomColoredItem.setSelected(gameplay.isRandomColoredPieces());

		preferencesMenu.add(gameplayMenu);

		// Help Menu
		JMenu helpMenu = new JMenu(NBlockApp.BUNDLE.getString("HelpMenu"));
	
		JMenuItem itemAbout = new JMenuItem(NBlockApp.BUNDLE.getString("About"));
		helpMenu.add(itemAbout);
		itemAbout.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) { nblock.showAbout(); }
		});

		menuBar = new JMenuBar();
		menuBar.add(gameMenu);
		menuBar.add(preferencesMenu);
		menuBar.add(helpMenu);
	}

	public JMenuBar getJMenuBar() {
		return menuBar;
	}
}
