package de.javakara.manf.mcdefrag.api;

import de.javakara.manf.util.LanguageComplete;

@LanguageComplete
public class Highscore {
	public static final int type_score = 0;
	public static final int type_name = 1;
	private static final int type_max = 2;
	private int maxscore;
	private String scores[][];

	public Highscore(int maxscore) {
		this.maxscore = maxscore;
		scores = new String[maxscore][type_max];
		reset();
	}

	public void deleteScore(int i) {
		for (; i < maxscore; i++) {
			if(i+1 == scores.length){
				scores[i][type_name] = "";
				scores[i][type_score] = "0";
				return;
			}
			scores[i][type_score] = scores[i+1][type_score];
			scores[i][type_name] = scores[i+1][type_name];
		}
	}

	public int newScore(String name, long a) {
		for (int i = 0; i < maxscore; i++) {
			String s = scores[i][type_score];

			Long score = Long.parseLong(s);
			if (a < score || score == 0) {
				int rank = i;
				String amount = "" + a;
				for (; i < maxscore; i++) {
					String oldscore = scores[i][type_score];
					String oldname = scores[i][type_name];
					scores[i][type_score] = amount + "";
					scores[i][type_name] = name;
					name = oldname;
					amount = oldscore;
				}
				return rank + 1;
			}
		}
		return -1;
	}

	public String getTop(int i) {
		return scores[i - 1][type_name];
	}

	public String getScore(int i) {
		return scores[i - 1][type_score];
	}

	public int getMaxscore() {
		return maxscore;
	}

	public String[][] getScores() {
		return scores;
	}

	public void reset() {
		for (int i = 0; i < maxscore; i++) {
			scores[i][type_score] = "0";
			scores[i][type_name] = "";
		}
	}
}
