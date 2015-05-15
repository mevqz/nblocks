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

public class GraphicsSettings 
{	
	private boolean gridEnabled;
	private boolean slideAnimationEnabled;
	private boolean backgroundImageEnabled;
	private BlockType pieceBlockType;
	private BlockType mirrorPieceBlockType;		// ghost / preview
	private int pxGridSize;
//	private float transparency;
//	private boolean blackBackground;

	public GraphicsSettings() {
		gridEnabled = false;
		slideAnimationEnabled = true;
		backgroundImageEnabled = true;
		pieceBlockType = BlockType.GRADIENT_BLOCK;
		mirrorPieceBlockType = BlockType.ROUND_BLOCK;
		setPxGridSize(36);
	}

	public void setPxGridSize(int newValue) {
		if (newValue < 6)
			pxGridSize = 6;
		else
			pxGridSize = newValue;
	}

	public int getPxGridSize() {
		return pxGridSize;
	}

	public void setGridEnabled(boolean newValue) {
		gridEnabled = newValue;
	}
	public boolean isGridEnabled() {
		return gridEnabled;
	}
	
	public void setSlideAnimationEnabled(boolean newValue) {
		slideAnimationEnabled = newValue;
	}
	public boolean isSlideAnimationEnabled() {	// slide animation
		return slideAnimationEnabled;
	}

	public void setBackgroundImageEnabled(boolean newValue) {
		backgroundImageEnabled = newValue;
	}
	public boolean isBackgroundImageEnabled() {
		return backgroundImageEnabled;
	}
	
	public void setPieceBlockType(BlockType newValue) {
		pieceBlockType = newValue;
	}
	public BlockType getPieceBlockType() {
		return pieceBlockType;
	}
	
	public void setMirrorPieceBlockType(BlockType newValue) {
		mirrorPieceBlockType = newValue;
	}
	public BlockType getMirrorPieceBlockType() {
		return mirrorPieceBlockType;
	}
}
