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

public class GameModeSettingsDialog
{
	private static JDialog dialog;
	private static JComboBox boxGameMode;
//	private static JTextArea txtGameMode;
	private static JSpinner startLevelSpinner;
	private static JSpinner boardWidthSpinner;
	private static JSpinner initGarbageLinesSpinner;
	private static JSpinner nextPiecesSpinner;
	private static JCheckBox checkLockDown;
	private static JButton btnSetDefault;	// FIXME Este no es necesario que sea un atributo

	static class Test {
		static GameModeSettings gs = new GameModeSettings();
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
							boolean newSettings = showDialog(f, gs); 
							if (newSettings)
								System.out.println("Aplicar cambios");
							else
								System.out.println("No aplicar un carajo"); 
						}	
					});
					f.add(b);
					f.setVisible(true);
				}
			});
		}
	}

	private static boolean applyChanges;			// probablemente 
	public static boolean showDialog(JFrame frame, GameModeSettings settings)
	{
		applyChanges = false;
//		nonDefault = false;		// una ALT seria que los set del DTO enciendan una flag interna q indiq esto 
		if (dialog == null) {
			initDialogAndComponents(frame); 
			dialog.setContentPane(createContentPane(settings));	
			dialog.pack();
			dialog.setResizable(false);
			updateSettingsValues(GameModeType.values()[0]); 
			//updateSettingsValues((GameModeType)boxGameMode.getSelectedItem()); 
			//updateSettingsValues(settings.getGameModeType()); 
		} 
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		return applyChanges;
	}

	
	// TODO
	// Es que deberia tener un create default y otro custom:
	// GameModeFactory.create(GameplaySettings)		--> CUSTOM
	// GameModeFactory.create(GameModeType)			--> DEFAULT
	// El que recibe el GameplaySettings puede ser reemplazados por los 3 valores que saco del mismo

	
	public static void updateSettingsValues(GameModeType gameModeType) {
//		GameplaySettings gSettings = GameModeFactory.create(gameModeType).getGameplaySettings();	

		boxGameMode.setSelectedItem(gameModeType);
		GameMode gameMode = GameModeFactory.create(gameModeType);

		int finalLevel = gameMode.getFinalLevel();
		SpinnerModel startLevelModel = new SpinnerNumberModel(1, 1, finalLevel, 1);
		startLevelSpinner.setModel(startLevelModel);	// TODO esto deberia desencadenar un update mepa

		int boardWidth = gameMode.getBoardWidth();
		SpinnerModel boardWidthModel = new SpinnerNumberModel(boardWidth, 8, 14, 2);
		boardWidthSpinner.setModel(boardWidthModel);	// TODO esto deberia desencadenar un update mepa

		// El maximum (12) esta harcodeado pero por otro lado la altura es fija y no cambia
		int initGarbageLines = gameMode.popUpLines();
		SpinnerModel garbageLinesModel = new SpinnerNumberModel(initGarbageLines, 0, 12, 1);
		initGarbageLinesSpinner.setModel(garbageLinesModel);

		int piecesPreview = gameMode.getNextPiecesPreview();
		int maxPanels = NextPiecesPanel.MAX_PANELS;
		SpinnerModel nextPiecesModel = new SpinnerNumberModel(piecesPreview, 
			0, maxPanels, 1);
		nextPiecesSpinner.setModel(nextPiecesModel);

		checkLockDown.setSelected(gameMode.isLockDownAtDrop());


		//TODO Deberia usar un GameplaySettingFactory en lugar de gameMode.getGameplaySettings() ???
	}

	private static void initDialogAndComponents(JFrame frame) {
		String title = "Gamemode Settings";	// TODO resource bundle...
		dialog = new JDialog(frame, title, true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		boxGameMode = new JComboBox(new GameModeTypeComboModel());
		boxGameMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSettingsValues((GameModeType)boxGameMode.getSelectedItem());
			}
		});

		startLevelSpinner = new JSpinner();
//		((JTextField)startLevelSpinner.getEditor()).setColumns(4);
		boardWidthSpinner = new JSpinner();
		initGarbageLinesSpinner = new JSpinner();
//		((JTextField)initGarbageLinesSpinner.getEditor()).setColumns(4);
		nextPiecesSpinner = new JSpinner();
//		((JTextField)nextPiecesSpinner.getEditor()).setColumns(4);

		String strLockDown = "Lock Down";
		checkLockDown = new JCheckBox(strLockDown);	

		String strDefault = "Set default";
		btnSetDefault = new JButton(strDefault);
		btnSetDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 2 alternativas:
				/*1)*///settings.setDefaultValues();
				/*2)*///settings = GameModeFactory.create((GameMode)boxGameMode.getSelectedItem()).getGameplaySettings();
	//			setSettingsValues(settings);
				updateSettingsValues((GameModeType)boxGameMode.getSelectedItem());
			}
		});	

	}

	private static JPanel createGameModePanel() {
		JPanel panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
//		JPanel gameModeSelection = new JPanel();
		panel.add(new JLabel("Game mode: "));
//		gameModeSelection.add(new JLabel("Game mode: "));
		panel.add(Box.createHorizontalGlue());			
		panel.add(boxGameMode);
//		gameModeSelection.add(boxGameMode);
//		panel.add(gameModeSelection);
//		panel.add(txtGameMode);
		return panel;
	}
	
	private static JPanel createContentPane(final GameModeSettings settings) {

		JPanel gameplaySettingsPanel = new JPanel(new BorderLayout());
		gameplaySettingsPanel.add(createGameModePanel(), BorderLayout.NORTH);
		gameplaySettingsPanel.add(createGameModeSettingsPanel());

		//JPanel buttonsPanel = createButtonsPanel();
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
		String strCancel = "Cancel"; // TODO resourcebundle
		String strAccept = "Apply"; // TODO resourcebundle
		JButton btnCancel = new JButton(strCancel);
		JButton btnAccept = new JButton(strAccept);
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(btnCancel);
		buttonsPanel.add(btnAccept);
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(14, 4, 4, 4));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dialog.dispose(); }
		});
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settings.setGameModeType((GameModeType)boxGameMode.getSelectedItem());
				settings.setStartLevel((Integer)startLevelSpinner.getValue());
				settings.setBoardWidth((Integer)boardWidthSpinner.getValue());
				settings.setInitGarbageLines((Integer)initGarbageLinesSpinner.getValue());
				settings.setNextPiecesPreview((Integer)nextPiecesSpinner.getValue());
				settings.setLockDownAtDrop(checkLockDown.isSelected());
				applyChanges = true;
				/*Este deberia ser un field static*///GameplaySettings defaultSettings = GameModeFactory.create(settings.
//					getGameModeType()).getGameplaySettings();
//				if (!settings.equals(defaultSettings)) 
//					JOptionPane.showMessageDialog(null, "Non default settings...");
//					nonDefault = true;
				dialog.dispose();
			}
		});
//XXX Dos alternativas para saber del nonDefault... puedo comparar aca y setear nonDefault y hacerlo publico para qe desde
//afuera pueda consultar el valor de este. La otra es mover la logica de comparacion del settings con el default afuera
//de esta clase, es decir en NBlocks despues de showDialog y si es que applyChanges == true

//XXX o deberia crear un metodo que a partir de un GameMode me cree un GameplaySettings o que gameMode tenga un metodo getSetings... el metodo seria lo que yo planteaba de hacer una Factory para el setting
//((ALgo parecido es lo que hace el metodo updateSettings()


//Lo que no me gusta es que en GameMode tengo un metodo showMirror() y tambien en el GameplaySetting... BAH TAL VEZ NO ESTE MAL... El NBlockApp debe consultar al Setting, no al GameMode. Por otro lado puedo evitar el GameMode.showMirror si implemento un metodo GameMode.getGameplaySettings q me devuelva el default para ese gameMode y yo en base a eso lo modifico para un juego personalizado, e implementado equals puedo compararlos facilmente.

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(gameplaySettingsPanel);
		contentPane.add(buttonsPanel, BorderLayout.SOUTH);
		return contentPane;
	}

	private static JPanel createGameModeSettingsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		JPanel midSettingsPanel = new JPanel(new GridLayout(5, 2));

		String txtStartLevel = "Start level: ";
		midSettingsPanel.add(new JLabel(txtStartLevel));
//		spinnerPanel.add(Box.createHorizontalGlue());
		midSettingsPanel.add(startLevelSpinner);

		String txtWidth = "Board width: ";
		midSettingsPanel.add(new JLabel(txtWidth));
		midSettingsPanel.add(boardWidthSpinner);

		String txtInitLines = "Initial garbage lines: ";
		midSettingsPanel.add(new JLabel(txtInitLines));
		midSettingsPanel.add(initGarbageLinesSpinner);

		String txtNext = "Next pieces preview: ";
		midSettingsPanel.add(new JLabel(txtNext));
		midSettingsPanel.add(nextPiecesSpinner);

		midSettingsPanel.add(checkLockDown);

		panel.add(midSettingsPanel);

		JPanel defaultPanel = new JPanel();
		defaultPanel.setLayout(new BoxLayout(defaultPanel, BoxLayout.LINE_AXIS));
		defaultPanel.add(Box.createHorizontalGlue());
		defaultPanel.add(btnSetDefault);
		panel.add(defaultPanel);

		String title = "Game mode settings";
		panel.setBorder(BorderFactory.createTitledBorder(title));

		return panel;
	}
}
