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
import java.awt.*;
import java.awt.event.*;

public class AboutDialog 
{	
	private static JDialog dialog;
	public static void showDialog(JFrame frame)
	{
		if (dialog == null)
			initDialogAndComponents(frame);

		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}
	
	private static void initDialogAndComponents(JFrame frame) {
		String title = "About NBlocks";
		dialog = new JDialog(frame, title, true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		dialog.setContentPane(createContentPane());
		dialog.pack();
		dialog.setResizable(false);
	}

	private static JPanel createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());

		JTabbedPane	tabs = new JTabbedPane();
		String appName = "NBlocks"; //ResourceBundle...
		tabs.addTab(appName, createMainTab());
		String license = "License"; //ResourceBundle...
		tabs.addTab(license, createLicenseTab());
	
		String accept = "Accept"; //ResourceBundle...
		JButton btnAccept = new JButton(accept);
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dialog.dispose(); }
		});
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.LINE_AXIS));
		btnPanel.add(Box.createHorizontalGlue());
		btnPanel.add(btnAccept);
		btnPanel.setBorder(BorderFactory.createEmptyBorder(14, 4, 4, 4));

		contentPane.add(tabs, BorderLayout.CENTER);
		contentPane.add(btnPanel, BorderLayout.SOUTH);
		return contentPane;
	}

	private static JPanel createMainTab() {
		JPanel panel = new JPanel();
		//ImageIcon img = createImageIcon();
		ImageIcon img = new ImageIcon("avatar.jpg");
		String str = "NBlocks v1.0b";
		JLabel label = new JLabel(str, img, JLabel.CENTER);	
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setHorizontalTextPosition(JLabel.CENTER);
		panel.add(label);
		panel.setPreferredSize(new Dimension(320,192));
		return panel;
	}

	private static JPanel createLicenseTab() {
		JPanel panel = new JPanel();
		JTextArea textArea = new JTextArea(licenseDescription + "\n\n" + licenseDescription, 20, 40);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		panel.add(new JScrollPane(textArea));
		return panel;
	}

    /** Returns an ImageIcon, or null if the path was invalid. */
 /*   private static ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = LabelDemo.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
*/

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
						public void actionPerformed(ActionEvent e) { 
							showDialog(f); 
						}	
					});
					f.add(b);
					f.setVisible(true);
				}
			});
		}
	}

	private static String licenseDescription = 
		"Quadrapassel es software libre; puede redistribuirlo y/o modificarlo bajo " 
		+ "los términos de la Licencia Pública General GNU tal como la publica la "
		+ "Free Software Foundation; ya sea en la versión 2 de la Licencia, como "
		+ "(a su elección) cualquier otra versión posterior.\n\n"
		+ "Quadrapassel se distribuye con el ánimo de que le será útil, pero SIN "
		+ "NINGUNA GARANTÍA; sin incluso la garantía implícita de MERCANTILIDAD "
		+ "o IDONEIDAD PARA UN PROPÓSITO DETERMINADO. Vea la Licencia Pública General "
		+ "de GNU para más detalles.\n\n"
		+ "Debería haber recibido una copia de la Licencia Pública General GNU junto "
		+ "con Quadrapassel; si no, escriba a la Free Software Foundation, Inc., 51 "
		+ "Franklin Street, Fifth Floor, Boston, MA 02110-1301 EE. UU.\n";

}
