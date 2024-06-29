package tower;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

import tge.BasicListener;
import tge.Updatable;
import tge.Utilities;

public class Tower implements Updatable {

	public static final int sizeX = 32;
	public static final int sizeY = 64;
	
	private double X;
	private double Y;
	private Color team;
	
	private double Income=0.025;
	private double Ressources=0;
	private double maxRessources=30;
	public static BasicListener listener;
	public static Game game;
	
	//textures
	public static BufferedImage TowerTexture = null;
	public static BufferedImage EmptyMask = null;
	public static HashMap<Color,BufferedImage> TowerMask = new HashMap<Color,BufferedImage>();
	
	public static Random R=new Random();
	
	public static double prop=0.5;
	
	private boolean selected=false;

	
	public Tower(double x,double y,Color c) {
		setX(x);
		setY(y);
		setTeam(c);
		if(team==null)
			Income/=3;
		if(TowerTexture==null)
			try {
				TowerTexture = ImageIO.read(new File("images//assets//towers.png"));
				EmptyMask = ImageIO.read(new File("images//assets//mask_towers.png"));
				
			} catch (IOException e) {
			    e.printStackTrace();
			}
		
		if(!TowerMask.containsKey(team)&&team!=null)
			try {
				TowerTexture = ImageIO.read(new File("images//assets//towers.png"));
				BufferedImage mask = ImageIO.read(new File("images//assets//mask_towers.png"));
				Utilities.FillImage(team, mask);
				TowerMask.put(team, mask);
			} catch (IOException e) {
			    e.printStackTrace();
			}
			
			
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(team);
		//g.fillRect((int)(X-sizeX/2), (int)(Y)-sizeY, sizeX, sizeY);
		g.drawImage(team==null?EmptyMask:TowerMask.get(team),(int)X-sizeX/2,(int)Y-sizeY, sizeX,sizeY, null);
		g.drawImage(TowerTexture,(int)X-sizeX/2,(int)Y-sizeY, sizeX,sizeY, null);
		g.setColor(Color.BLACK);
		g.drawString((int)Ressources+"",(int) (X-sizeX/2), (int)(Y-sizeY-3));
		if(selected())
			g.drawLine((int)X, (int)Y, game.getMX(), game.getMY());
	}
	
	private boolean selected() {
		if(listener.isPressed(MouseEvent.BUTTON1)) {
			if(selected)
				return true;
			else if(this.collide(listener.getPX(), listener.getPY()) && this.team==game.player)
				selected = true;
		}else {
			if(selected) {
				for(Tower t:game.towers) {
					if(t.collide(listener.getRX(), listener.getRY())) {
						/**
						if(t==this)
							continue;
						//event attack
						
						int units=(int) (prop*Ressources);
						
						System.out.println("attack from tower : "+this.toString());
						System.out.println("         to tower : "+t.toString());
						for(int i=0;i<units;i++) {
							Unit u=new Unit(X+R.nextDouble()*2-1,Y+R.nextDouble()*2-1, team);
							u.setObjective(t.X+(R.nextDouble()-0.5)*sizeX/8,t.Y-R.nextDouble()*sizeY/4);
							game.addObject(u);
						}
						Ressources-=units;**/
						this.attack(t);
					}
				}
				selected=false;
			}
		}
		return selected;	
	}
	
	public boolean collide(double Xpos,double Ypos) {
		return Xpos>X-sizeX/2&&Xpos<X+sizeX/2
				 &&Ypos>Y-sizeY&&Ypos<Y;
	}
	
	public String toString() {
		return("<tower instance : x:"+X+" | y:"+Y+" | team:"+(team==null?"None":team.toString())+">");
	}

	public Color getTeam() {
		return team;
	}

	public void setTeam(Color team) {
		this.team = team;
	}

	public double getY() {
		return Y;
	}

	public void setY(double y) {
		Y = y;
	}

	public double getX() {
		return X;
	}

	public void setX(double x) {
		X = x;
	}

	public double getIncome() {
		return Income;
	}

	public void setIncome(double income) {
		Income = income;
	}

	public double getRessources() {
		return Ressources;
	}

	public void setRessources(double ressources) {
		Ressources = ressources;
	}

	@Override
	public void update() {
		if(Ressources<maxRessources)
			Ressources+=Income;
	}

	public void attack(Tower t) {
		if(t==this)
			return;
		//event attack
		
		int units=(int) (prop*Ressources);
		
		System.out.println("attack from tower : "+this.toString());
		System.out.println("         to tower : "+t.toString());
		for(int i=0;i<units;i++) {
			Unit u=new Unit(X+R.nextDouble()*2-1,Y+R.nextDouble()*2-1, team);
			u.setObjective(t.X+(R.nextDouble()-0.5)*sizeX/8,t.Y-R.nextDouble()*sizeY/4);
			game.addObject(u);
		}
		Ressources-=units;
	}

	@Override
	public double Zindex() {
		return Y;
	}
}
