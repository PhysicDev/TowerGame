package tower;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import tge.Updatable;

public class StoopidBot implements Updatable {

	public static ArrayList<Tower> tlist;
	public static Game game;
	
	private Color team;
	public static Random R=new Random();
	
	
	private double probSend=1;//nombre moyen de tentative d'attaque par seconde
	
	
	public StoopidBot(Color team,double prob) {
		this.setTeam(team);
		this.setProbSend(prob);
	}
	public StoopidBot(Color team) {
		this.setTeam(team);
	}

	@Override
	public void draw(Graphics g) {
		//nothing (empty asset)
	}

	@Override
	public void update() {
		if(R.nextDouble()<probSend/game.FrameLogic()) {
			Tower t=tlist.get(R.nextInt(tlist.size()));
			if(t.getTeam()==team) {
				Tower t2=tlist.get(R.nextInt(tlist.size()));
				t.attack(t2);
			}
		}
	}

	public double getProbSend() {
		return probSend;
	}

	public void setProbSend(double probSend) {
		this.probSend = probSend;
	}

	public Color getTeam() {
		return team;
	}

	public void setTeam(Color team) {
		this.team = team;
	}
	@Override
	public double Zindex() {
		return 0;
	}

}
