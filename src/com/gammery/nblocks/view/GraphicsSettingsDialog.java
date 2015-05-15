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

public class GraphicsSettingsDialog
{
	private static JDialog dialog;
	private static JCheckBox checkGrid;
	private static JCheckBox checkSlideAnimation;
	private static JCheckBox checkBackground;
	private static JComboBox boxPieceType;
	private static JComboBox boxMirrorPieceType;

	static class Test {
		static GraphicsSettings gs = new GraphicsSettings();
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

	private static boolean applyChanges;
	public static boolean showDialog(JFrame frame, GraphicsSettings settings)	// final arg
	{
		applyChanges = false;
		if (dialog == null) {
			initDialogComponents(); //initGraphicsDialogComponents(); // initGraphicsSettingsDialog();
			String title = "Graphics Settings";	// TODO resource bundle...
			dialog = new JDialog(frame, title, true);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setContentPane(createContentPane(settings));	
			dialog.pack();
			dialog.setResizable(false);
		} 
		setSettingsValues(settings); //setGraphicsSettingsValues(settings);	// setGraphicsSettingsOptionValues
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		return applyChanges;
		//return newGameSettings;
	}
	
	private static JPanel createGeneralSettingsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(checkGrid);
		panel.add(checkSlideAnimation);
		panel.add(checkBackground);
		String borderTitle = "General settings";	//TODO usar resource bundle
		panel.setBorder(BorderFactory.createTitledBorder(borderTitle));
		return panel;
	}

	private static JPanel createTilesPanel() {
		JPanel panel = new JPanel(new GridLayout(1,2));
//		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		JPanel leftPanel = createPieceFramePanel("Block type", boxPieceType);
		leftPanel.setBorder(BorderFactory.createTitledBorder("Game Piece"));

		JPanel rightPanel = createPieceFramePanel("Block type", boxMirrorPieceType);
		rightPanel.setBorder(BorderFactory.createTitledBorder("Mirror Piece"));

		panel.add(leftPanel);
		panel.add(rightPanel);
		return panel;
	}

	private static JPanel createPieceFramePanel(String labelText, final JComboBox boxBlockType) {
		JPanel panel = new JPanel();
//		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		PieceFactory pFactory = PieceFactory.getInstance();
		final PieceFrame pieceFrame = new PieceFrame(120, 120);
		boxBlockType.setSelectedIndex(0);
		pieceFrame.drawPiece(pFactory.getRandomPiece(), (BlockType)boxBlockType.getSelectedItem());
		boxBlockType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pieceFrame.setBlockType((BlockType)boxBlockType.getSelectedItem());
			}
		});
		JLabel label = new JLabel(labelText);
//		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(label);
		pieceFrame.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(pieceFrame);
//		boxBlockType.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(boxBlockType);
//		int width = Collections.max(Arrays.asList(label.getWidth(), pieceFrame.getWidth(), boxBlockType.getWidth()));
//		int height = Collections.max(Arrays.asList(label.getHeight(), pieceFrame.getHeight(), boxBlockType.getHeight()));
		Dimension size = new Dimension(170, 200);
		panel.setMaximumSize(size);
		panel.setPreferredSize(size);
		panel.setMinimumSize(size);
		String borderTitle = "Piece";
		panel.setBorder(BorderFactory.createTitledBorder(borderTitle));
		return panel;
	}

	private static JPanel createContentPane(final GraphicsSettings settings) {
		JPanel contentPane = new JPanel(new BorderLayout());
		JPanel graphicsSettingsPanel = new JPanel(new BorderLayout());
		graphicsSettingsPanel.add(createGeneralSettingsPanel());
		graphicsSettingsPanel.add(createTilesPanel(), BorderLayout.SOUTH);

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
				settings.setGridEnabled(checkGrid.isSelected());
				settings.setSlideAnimationEnabled(checkSlideAnimation.isSelected());
				settings.setBackgroundImageEnabled(checkBackground.isSelected());
				settings.setPieceBlockType((BlockType)boxPieceType.getSelectedItem());
				settings.setMirrorPieceBlockType((BlockType)boxMirrorPieceType.getSelectedItem());
				applyChanges = true;
				dialog.dispose();
			}
		});
	
		contentPane.add(graphicsSettingsPanel);
		contentPane.add(buttonsPanel, BorderLayout.SOUTH);
		return contentPane;
	}
/*
	private static JPanel createTilesPanel() {
		JPanel panel = new JPanel(...);
		JPanel leftPiecePanel = createPieceFramePanel("Piece", boxPieceType);
		JPanel rightPiecePanel = createPieceFramePanel("Mirror Piece", boxMirrorPieceType);

		boxMirrorPieceType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mirrorPieceFrame.setBlockType((BlockType)boxMirrorPieceType.getSelectedItem());
			}
		});
		
		// add al panel...
		return panel;
	}*/
/*	
	$ java -cp ../../ nblock.view.GraphicsSettingsDialog\$Test createPieceFramePanel
	static class Test {
		static String[] testCase = { "", "", "" };
		public static void main(String args[]) {

			if (""
		}	
	}	
*/

	/*
	private static JPanel createPieceFramePanel(String labelText, final JComboBox boxBlockType) {
		JPanel panel = new JPanel(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		PieceFactory pFactory = PieceFactory.getInstance();
		final PieceFrame pieceFrame = new PieceFrame(200, 200);
		pieceFrame.drawPiece(pFactory.getRandomPiece(), (BlockType)boxBlockType.getSelectedItem());
		boxBlockType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pieceFrame.setBlockType((BlockType)boxBlockType.getSelectedItem());
			}
		});
		JLabel label = new JLabel(labelText);
		panel.add(label);
		panel.add(pieceFrame);
		panel.add(boxBlockType);
		return panel;
	}
	*/

		


//	private static void setGraphicsSettingsValues(GraphicsSettings settings) {
	private static void setSettingsValues(GraphicsSettings settings) {
		checkGrid.setSelected(settings.isGridEnabled());
		checkSlideAnimation.setSelected(settings.isSlideAnimationEnabled());
		checkBackground.setSelected(settings.isBackgroundImageEnabled());
		//TODO Si quiero cambiar las Pieces, lo tengo que hacer aca...
		boxPieceType.setSelectedItem(settings.getPieceBlockType());
		boxMirrorPieceType.setSelectedItem(settings.getMirrorPieceBlockType());
	}

//	private static void initGraphicsDialogComponents() {
	private static void initDialogComponents() {
		String strGrid = "Grid"; // TODO bundle
		checkGrid = new JCheckBox(strGrid);
		String strSlide = "Slide animation"; // TODO bundle
		checkSlideAnimation = new JCheckBox(strSlide);
		String strBackground = "Background image"; // TODO bundle
		checkBackground = new JCheckBox(strBackground);
		boxPieceType = new JComboBox(new BlockTypeComboModel());
		boxMirrorPieceType = new JComboBox(new BlockTypeComboModel());
	}



/*
	if (dialog == null) {
		createDialog();
	}
	//setSettingsValues(settings);
	setComponentsStatus(settings);
	dialog.setVisible(true);
		*/
}
