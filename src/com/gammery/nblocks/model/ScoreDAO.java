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

import java.io.*;
import java.util.*;
import java.nio.channels.*;
import java.nio.*;

/*
	Si dejo que esta clase administre el Score entonces si puedo hacer otras cosas...
*/


//XXX
//Algo que podria hacer es que esta clase lleve el currentScore:
//private static Score currentScore;
//
//y en el getScore me encargaria de invocar a currentScore.setRank(...)
//Y por lo tanto se supone que en el save() no es necesario que verifique el rank, porq
//debe ser creado con esta clase y en el get se setea el rank

public class ScoreDAO
{
	private static final int RANKING_SLOTS = 3; //RANK_LIMIT = 10;	// topRankSlots // rankSlots // topLimit // ranking_slots
	private static final String FILENAME = "MyScores.dat"; //gmt.name().toLowerCase() + "_" + fname;
	private static List<Score> allScores;		// se actualiza con el primer getHighestScore (si es del mismo GMT)
										// y con cada save. Esto es para evitar tantas lecturas

	// Probablemente no sea necesario
	public static Score createScore(GameModeType gameMode, int startLevel) {
		Score score = new Score();
		score.setGameModeType(gameMode);
		String defaultName = System.getProperty("user.name");
		score.setPlayerName(defaultName);
		score.setLevel(startLevel);
		return score;
	}


	/* Establece el ranking del Score en el mismo objeto. Un numero positivo
	 * indica la posicion del ranking. Un numero negativo (-1) indica que 
	 * esta fuera del ranking */
	public static void setRankingPosition(Score score) {
		List<Score> highestScores = new ArrayList<Score>(getHighestScores(score.getGameModeType()));
		//List<Score> highestScores = new ArrayList<Score>();
		//Collections.addAll(highestScores, getHighestScores(score.getGameModeType()));
		highestScores.add(score);
		sortScores(highestScores);
		System.out.println("\tsetRankingPosition():listSIZE:"+highestScores.size());
		if (highestScores.size() > RANKING_SLOTS) {
			highestScores = highestScores.subList(0, RANKING_SLOTS);					// XXX FIXME Puede que deba ser RANKING_SLOTS -1 CHECK!!
			System.out.println("\tsetRankingPosition():shortSIZE:"+highestScores.size());
		}
		score.setRank(highestScores.indexOf(score));    // + 1 no va
	}

	/*retorna true si fue salvado, esto ocurre si el score esta dentro del RANKING_SLOTS*/
	// XXX Se debe invocar a setRankingPosition antes de intentar salvar el score
	public static boolean trySave(Score score) {

		if (true) return true;

		if (score.getRank() == 0) {
			setRankingPosition(score);
		}
		if (score.getRank() < 0) {
			return false;
		}

		int recordNumber = -1;
		//int position = -1;	// file offset
		GameModeType gameMode = score.getGameModeType();
		if (getHighestScores(gameMode).size() == RANKING_SLOTS) {
			Score outOfRank = getHighestScores(gameMode).get(RANKING_SLOTS - 1); // OR getLowestScore();
			System.out.println("OUT OF RANK>>> " + "s.name: " + outOfRank.getPlayerName() 
				+ " score: " + outOfRank.getScore() + " lines: " + outOfRank.getLinesCleared());
			recordNumber = getRecordNumber(outOfRank);
			//position = getRecordPosition(outOfRank);//getScorePosition(outOfRank);
		}
		System.out.println("trySave()::recordNumber::"+recordNumber);
		System.out.println(">>Score.rank:"+score.getRank());
		save(recordNumber, score);
		if (recordNumber != -1)
			allScores.set(recordNumber, score);
		else
			allScores.add(score);
		return true;
	}

	/* Salva el Score en el archivo en el numero de registro que se
	 * indica. Si recordNumber es -1 el registro se salva al final del archivo */
	private static void save(int recordNumber, Score score) {
		FileChannel out = null;
		try {
			out = new RandomAccessFile(FILENAME, "rw").getChannel();
			long offset = (recordNumber < 0) ? out.size() : recordNumber * Score.RECORD_LENGTH;
			System.out.println("offset: " + offset);
			System.out.println("channelSize:" + out.size());
			//offset = 281;
			out.position(offset);
			System.out.println(">>>>>>>>>>>>><ChanelPos::" + out.position());
			out.write(ByteBuffer.wrap(serialize(score)));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) { }
		}

/*
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILENAME)));
			int bytes = (recordNumber < 0) ? out.available() : recordNumber * Score.RECORD_LENGTH;
			out.skipBytes(bytes);
			out.writeObject(score);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) { }
		}*/
	}



	/* Deserializa el Score */
	private static Score deserialize(byte[] byteObject) {
		Score score = null;
		try {
			ByteArrayInputStream input = new ByteArrayInputStream(byteObject);
			ObjectInputStream deserializer = new ObjectInputStream(input);
			score = (Score) deserializer.readObject();
		} catch (Exception e) { 
			System.out.println(">>>>>>>>>>>>>>>>>>");
			e.printStackTrace();
			System.out.println(">>>>>>>>>>>>>>>>>>");
		} finally {
			return score;
		}
	}

	/* Serializa el Score */
	private static byte[] serialize(Score score) {
		byte[] byteObject = null;
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			ObjectOutputStream serializer = new ObjectOutputStream(buffer);
			serializer.writeObject(score);
			byteObject = buffer.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("scoreSIZE::"+byteObject.length);
			return byteObject; 
		}
	}

	// FIXME Podria retornar null si no hay... Todo depende...
	public static Score getHighScore(GameModeType gameMode)
	{
		Score highScore = null;
		if (getHighestScores(gameMode).size() != 0)
			highScore = getHighestScores(gameMode).get(0);
		else {
			highScore = new Score();	// Si hago esto no hace falta un else,
		}								// instancia cuando declaro, pero si quiero setear un name...
		return highScore;
	}


	/*Lee todos los scores que estan en el archivos
	y los carga en una List sin procesar ni discriminar.*/
	private static List<Score> getAllScores() {
		if (allScores != null)
			return allScores;

		List<Score> scores = new ArrayList<Score>();
		FileInputStream in = null;
		try {
			in = new FileInputStream(FILENAME);
			try {	
				byte[] scoreBytes = new byte[Score.RECORD_LENGTH];
				while (true) {
					if (in.read(scoreBytes) <= 0)
						break;	
					System.out.println("getAllScores():: after break;");
					Score score = deserialize(scoreBytes);
					scores.add(score);
				}
			} catch (IOException e) {
				System.out.println("getAllScores::IOException");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
//			createScoreFile();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) { }
		}
		System.out.println("end getAllScores()");
		allScores = scores;
		return scores;
	}

	public static List<Score> getHighestScores(GameModeType type) {
		List<Score> scores = new ArrayList<Score>();
		for (Score sc : getAllScores()) {
			if (sc.getGameModeType() == type)
				scores.add(sc);
		}
		sortScores(scores);
		return scores;
	}

	// FIXME probablemente este metodo deberia devolver el byteoffset en lugar del
	// record number: getByteOffset // getRecordPosition // getRecordOffset ( en ese
	// caso debo multiplicar el idx por el record size antes de retornar
	private static int getRecordNumber(Score score) {
		//return getAllScores().indexOf(score);
		int rNum = 0;	
		for (Score sc : getAllScores()) {
			if (score.getScore() == sc.getScore()) /* &&
				score.getPlayerName() == sc.getPlayerName() &&
				score.getDate() == sc.getDate() && 
				score.getLevel() == sc.getLevel() &&
				score.getGameModeType() == sc.getGameModeType() &&
				score.getLinesCleared() == sc.getLinesCleared())*/
					return rNum;
			rNum++;
		}
		return -1;
	}


	private static List<Score> sortScores(List<Score> scores) {
		Collections.sort(scores, new ScoreComparator());
		return scores;
	}

/*
	// este metodo trunca el archivo y no deberia... 
	// que el archivo se mantenga desordenado y q lo ordene la aplicacion... 
	// pero debo tener cuidado con no repetir scores
	// Deberia salvar al final si esta en el top10 o hacer un seek y escribir sobre otro Score
	private void saveScore(Score score)
	{
		//TODO Para buscar el removeScore indexFile la manera mas facil es creando una lista
		//de los scores tal cual estan "ordenados" esten en el archivo.
		//entonces simplemente a esa lista le hago un indexOf(removeScore) y de ahi simplemente leo para avanzar
		//el tema esta en como encontrar el removeScore. El PSEUDOCODE de esto seria:
		//List<Score> allScores = getAllScoresFromFile();
		//List<Score> allScoresByGameMode = filtrarByGameMode(allScores, gameModeType);
		//(ordenar allScoresByGameMode...)	
		//Score removeScore = allScoresByGameMode.get(rankingSlots);
		//int scoreIndex = allScores.indexOf(removeScore);
		//De nuevo, todo esto requiere implementar el equals para Score

		// XXX Siempre esta la alternativa de salvar toda la lista entera (aunque en ese caso no
		// necesito que Score sea fixed size). Esta implementacion es interna y no cambia la interfaz
		

		int slotsFree = rankingSlots - fileTopScores.size();
		if (slotsFree != 0)
			// al final
		else {
			Score remove = ...
			ObjectInputStream in = null;
			try {
				in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
				try {	
					do {
						Score score = (Score) in.readObject();
						scoreCounter++;
					} while (!score.equals(remove));					//TODO implementar equals en Score
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (EOFException e) { System.out.println("EPA, un error :S");}
			} catch (FileNotFoundException cantHappen) {
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (in != null)
						in.close();
				} catch (IOException e) { }
			}
		}


		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
			int limit = 0;
			scoreList = sortScores(scoreList);
			for (Score sr : scoreList) {
				out.writeObject(sr);
				if (++limit >= topLimit)
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) { }
		}
	}
*/

/*
	public static int getRecordNumber(Score score) {
		int recordNumber = 0;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILENAME)));
			try {	
				Score sc = null;
				do {
					sc = (Score) in.readObject();
					recordNumber++;
				} while (!score.equals(sc));					//TODO implementar equals en Score
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (EOFException e) { 
				//System.out.println("EPA, un error :S");
				recordNumber = -1;
			}
		} catch (FileNotFoundException cantHappen) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) { }
		}
		return recordNumber;
	}
*/


	public static void main (String [] args)
	{
		if (args.length > 0) {
			System.out.println("LENGTH > 0");
			Score s = generate();
			System.out.println("s.name: " + s.getPlayerName() + " score: " + s.getScore() + " lines: " + s.getLinesCleared());
			if (trySave(s)) {
				System.out.println("Score saved!");
			} else {
				System.out.println("Not saved:" + s.getRank());
			}
	}

		//List<Score> all = getHighestScores(GameModeType.SPRINT);
		List<Score> all = getAllScores();
		sortScores(all);
		System.out.println("all scors::"+all.size());
		for (Score sc : all) {
			System.out.println("sc.name: " + sc.getPlayerName() + " score: " + sc.getScore() + " lines: " + sc.getLinesCleared());
		}
	}


	private static Score generate() {
		java.util.Random rand = new java.util.Random();
		Score s = createScore(GameModeType.SPRINT, 1);
		s.setScore(rand.nextInt(10000));
		s.setLinesCleared(rand.nextInt(60) + 20);
		return s;
	}
}

// TODO
/*currentScore (SCORE) el field level si tiene que ser persistido. Mi duda venia a un caso como en el que yo cambie las reglas en un futuro y entonces el level no queda harcodeado pero en este caso aun si llego a cambiar las reglas esta bien que el level no cambie...
 *
 *
 *
 *
 * EL metodo SORT deberia tener en cuenta como primer ref el Score y en caso de igualdad otro field, por ejmplo, las linesCleared*/
