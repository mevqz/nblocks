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

package com.gammery.nblocks.controller;

import java.awt.event.KeyEvent;

public class ControlSettings 
{	
	public final int keyMoveLeft;
	public final int keyMoveExtremeLeft;
	public final int keyMoveRight;
	public final int keyMoveExtremeRight;
	public final int keyMoveDown;
	public final int keyDrop;
	public final int keyRotateLeft;
	public final int keyRotateRight;
	public final int keyPause;

	public ControlSettings() {
		this(KeyEvent.VK_LEFT, KeyEvent.VK_DELETE, KeyEvent.VK_RIGHT, KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_DOWN,
			KeyEvent.VK_SPACE, KeyEvent.VK_UP, KeyEvent.VK_END, KeyEvent.VK_P);
	}

	public ControlSettings(int kmLeft, int kmELeft, int kmRight,
		int kmERight, int kmDown, int kDrop, int kRLeft, 
		int kRRight, int kPause)
	{
		keyMoveLeft = kmLeft;
		keyMoveExtremeLeft = kmELeft;
		keyMoveRight = kmRight;
		keyMoveExtremeRight = kmERight;
		keyMoveDown = kmDown;
		keyDrop = kDrop;
		keyRotateLeft = kRLeft;
		keyRotateRight = kRRight;
		keyPause = kPause;
	}
}
