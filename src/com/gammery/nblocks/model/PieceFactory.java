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

package com.gammery.nblocks.model;

import java.util.*;
import java.awt.Color;

public class PieceFactory
{
	private long seed;
	private Random random;
	private Queue<Piece> pQueue;
	private List<Color> colors = new ArrayList<Color>();
	private boolean randColor;
	private static PieceFactory pFactory = new PieceFactory();

	private PieceFactory() {
		Collections.addAll(colors, Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, 
			Color.PINK, Color.CYAN, Color.MAGENTA, Color.GRAY, Color.ORANGE);
		reset();
	}

	public static PieceFactory getInstance() {
		return pFactory;
	}
		
	public void reset() {
		reset(false);
	}
	public void reset(boolean rColor) {
		seed = new Random().nextLong();
		reset(rColor, true);
	}
	public void reset(boolean rColor, boolean playNewMatch) {
		if (playNewMatch)
			seed = new Random().nextLong();
		random = new Random(seed);
		System.out.println("PieceFactory.reset()::seed = " + seed);
		pQueue = new LinkedList<Piece>();
		randColor = rColor;
	}

	/* Retorna el seed utilizado para generar la lista aleatoria 
	 * de tetriminos. Obtener el seed me permite reestablecer (mediante
	 * el metodo reset) la misma secuencia de tetriminos para poder 
	 * guardar y reproducir replays.*/
	public long getSeed() {
		return seed;
	}

	/* Retorna el siguiente tetrimino de la secuencia y lo remueve
	 * de la lista interna.*/
	public Piece next() {
		if (pQueue.isEmpty()) {	// tambien se puede usar peek y comparar con null
			pQueue.offer(create(random, randColor));
		}
		return pQueue.poll();
	}

	/* Retorna una lista de n size de los siguientes tetriminos en la secuencia
	 * */
	public List<Piece> next(int pListSize) {
		List<Piece> list = new ArrayList<Piece>();
		if (pQueue.size() < pListSize) {	
			for (int i = 0; i < pListSize; i++)
				pQueue.offer(create(random, randColor));
		}
		
		return ((List<Piece>)pQueue).subList(0, pListSize);
	}

	// Este metodo es utilizado por Tiles para mostrar una random piece pero sin
	// modificar la pQueue que almacena las siguientes piezas que va a utilizar el juego
	public Piece getRandomPiece() {	// No altera la lista de piezas
		//FIXME Tengo que usar un Random distinto ya que si uso el que es miembro de la clase
		//estoy alterando la secuencia de Piece's por venir, y si quiero repetir la misma partida
		//esto no va a ser posible de esta forma.
		Random r = new Random();
		return create(r, false);
	}

	// Este metodo es public por que tambien lo uso en Board.[generateTrashLines??]
	public Color getRandomColor() {
		Random r = new Random();
		return colors.get(r.nextInt(colors.size()));
	}


	// deberia usar returns en cada case asi me ahorro lineas de codigo
	private Piece create(Random rand, boolean randColor) {
		// Esto tambien podria ser asi, para evitar el switch:
		// pieces.get(rand.nextInt(pieces.size()));
		// Siendo pieces un List<Class<? extends Piece>>
		// pero deberia ver como instanciar cdo el ctor requiere un arg
		Piece piece = null;
		switch (rand.nextInt(7)) {
			default:
			case 0: 
				piece = new PieceI(randColor? getRandomColor() : Color.RED);			// FIXME Los colores no deberian estar seteados en cada Piece????
				break;
			case 1: 
				piece = new PieceJ(randColor? getRandomColor() : Color.YELLOW);
				break;
			case 2: 
				piece = new PieceL(randColor? getRandomColor() : Color.MAGENTA);
				break;
			case 3: 
				piece = new PieceO(randColor? getRandomColor() : Color.BLUE);
				break;
			case 4:	
				piece = new PieceS(randColor? getRandomColor() : Color.CYAN);
				break;
			case 5:	
				piece = new PieceZ(randColor? getRandomColor() : Color.ORANGE);
				break;
			case 6:	
				piece = new PieceT(randColor? getRandomColor() : Color.GREEN);
				break;
		}
		return piece;
	}
}
// Dilema: Usar reflection o no? con reflection tengo algunas ventajas...
// 1) Si quiero agregar nuevas Piece's solo tengo que agregar la Class en una List
// 2) 
