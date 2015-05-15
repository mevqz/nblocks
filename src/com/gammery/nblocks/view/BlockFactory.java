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

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import com.gammery.nblocks.model.PieceFactory;

//TODO#2: Al iniciar la aplicacion verificar que esten todas las blocksimages,, y para las qe no esten omitirlas y no mostrarlas en el menu


// Creo que cada Tileset (Theme) debe ser una clase, por lo que tendra tantas clases ImageBlocks
// como themes que usen imagenes hayan. Sin embargo, el actual ImageBlock podria seguirse usando
// para generar los otros Tiles.
// Usando este esquema, no necesito el atributo Filename en Piece, solo el blockType

public class BlockFactory
{
	private BlockFactory() {}

	public static BufferedImage getImageBlock(Color color, BlockType bType, int width, int height) {
		BlockCreator bCreator = getBlockCreator(bType);
		return bCreator.createBlock(color, width, height);
	} 

	private static BlockCreator getBlockCreator(BlockType bType) {
		BlockCreator bCreator = null;
		switch (bType) {
			case PLAIN_BLOCK:
				bCreator = new PlainBlock();
				break;
			case ROUND_BLOCK:
				bCreator = new RoundBlock();
				break;
			case GRADIENT_BLOCK:
				bCreator = new GradientBlock();
				break;
			case IMGTUX_BLOCK:
				bCreator = new ImageTuxBlock();
				break;
		}
		return bCreator;
	}

	private interface BlockCreator {
		public BufferedImage createBlock(Color color, int width, int height);
	}

	/* Bloque plano simple */
	private static class PlainBlock implements BlockCreator
	{
		private static Map<Color,BufferedImage> blocks = new HashMap<Color,BufferedImage>();
		public BufferedImage createBlock(Color color, int width, int height) {
			BufferedImage block = blocks.get(color);
			if (block == null || (block.getWidth() != width) || (block.getHeight() != height)) {
				block = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = (Graphics2D)block.createGraphics();
				g2d.setPaint(color);
				g2d.fill(new Rectangle(0, 0, width, height));
				blocks.put(color, block);
			}
			return block;
		}
	}
	
	/* Bloque con efecto degradé */
	private static class GradientBlock implements BlockCreator
	{
		private static Map<Color,BufferedImage> blocks = new HashMap<Color,BufferedImage>();
		public BufferedImage createBlock(Color color, int width, int height) {
			BufferedImage block = blocks.get(color);
			if (block == null || (block.getWidth() != width) || (block.getHeight() != height)) {
				block = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = (Graphics2D)block.createGraphics();
				g2d.setPaint(new GradientPaint(0, 0, getLighterColor(color), width, height, color));
				g2d.fillRect(0, 0, width, height);
				blocks.put(color, block);
			}			
			return block;
		}
		
		private Color getLighterColor(Color color) {
			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();
			return new Color((int)red / 2, (int)green / 2, (int)blue / 2);
		}
	}

	// TODO es la misma implementacion que PlainBlock, pero deberia 
	// dibujar un bloque con las esquinas redondeadas y remarcando el contorno
	private static class RoundBlock implements BlockCreator
	{
		private static Map<Color,BufferedImage> blocks = new HashMap<Color,BufferedImage>();
		public BufferedImage createBlock(Color color, int width, int height) {
			BufferedImage block = blocks.get(color);
			if (block == null || (block.getWidth() != width) || (block.getHeight() != height)) {
				block = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = (Graphics2D)block.createGraphics();
				g2d.setPaint(color);
				g2d.draw(new Rectangle(0, 0, width-1, height-1));
				blocks.put(color, block);
			}
			return block;
		}
	}

	//TODO ACA podria usar un metodo static de XMLService.getImagePath()... Pero si es para eso nomas no vale la pena...
	// Todo depende de si categorizo en carpetas los tilesset

	//Helper Class: solamente la uso para reutilizar codigo, con composicion
	//en todas las clases que implementen a BlockCreator y usen una imagen como src
	private static class ImageBlock implements BlockCreator
	{
		private String filename;
		private BlockCreator bCreatorException;

		//FIXME: No es necesario hacer privado al default-ctor, ya que estoy proveyendo uno con args
		//private ImageBlock() {} // Necesita el filename si o si y para mantener la interfaz igual se lo paso por ctor
		public ImageBlock(String fname, BlockCreator bCreatorEx) {
			filename = fname;
			bCreatorException = bCreatorEx;
		}
		
		public BufferedImage createBlock(Color color, int width, int height) {
			BufferedImage block = null;
			try {
				block = ImageIO.read(new File(filename));
				//NOTA: no hace falta resizear... makePiece se encarga de ese detalle (escala la imagen cuando dibuja)
				// TODO: Si resizeo aca mejoro la performance al evitar escalar en cada draw, por otro lado... voy a estar creando muchas imgs (con los PanelPiece, Board)
			} catch (IOException e) {
				block = bCreatorException.createBlock(color, width, height);
			}
			return block;
		}
	}

	/* Este bloque lo carga a partir de una imagen.
	 * En caso de no encontrar la imagen utiliza otro BlockCreator */
	private static class ImageTuxBlock implements BlockCreator
	{
		private static Map<String,BufferedImage> blocks = new HashMap<String,BufferedImage>();

		public BufferedImage createBlock(Color color, int width, int height) {
			BufferedImage block = null;
			//String filename = "img" + "Tux" + PieceFactory.getInstance().getColorName(color) + ".jpg";
			String filename = "img" + "Tux" + getImageName(color) + ".gif";
			block = blocks.get(filename);

			if (block == null || (block.getWidth() != width) || (block.getHeight() != height)) {
				BlockCreator bCreatorException = getBlockCreator(BlockType.PLAIN_BLOCK);
				// Tal vez el nombre se deba obtener de ....
				BlockCreator bImgCreator = new ImageBlock(filename, bCreatorException);
				block = bImgCreator.createBlock(color, width, height);
				blocks.put(filename, block);
			}
			return block;
		}

		private String getImageName(Color color) {
			String imgName = null;
			if (color == Color.RED)				imgName = "Red";
			else if (color == Color.BLUE) 		imgName = "Blue";
			else if (color == Color.ORANGE)		imgName = "Orange";
			else if (color == Color.YELLOW)		imgName = "Yellow";
			else if (color == Color.GREEN)		imgName = "Green";
			else if (color == Color.CYAN)		imgName = "Cyan";
			else if (color == Color.MAGENTA)	imgName = "Magenta";
			return imgName;
		}
	}
}
