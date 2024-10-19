package tower;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

import tge.Updatable;
import tge.Utilities;

public class Unit implements Updatable {

	//state variable
	private double X;
	private double Y;
	private double Xobj;
	private double Yobj;
	

	public static final int size = 32;
	
	//constant
	public static final double timeStep=1d/120d;
	private static final double immuneTime=2;
	private static final int immuneStep=(int) (immuneTime/timeStep);
	
	//textures
	public static BufferedImage UnitTexture = null;
	public static BufferedImage EmptyMask = null;
	public static HashMap<Color,BufferedImage> UnitMask = new HashMap<Color,BufferedImage>();

	//intern variable
	private double V=0;
	private double A=Math.PI/2;
	
	private int age=0;
	
	private double Atarg=-Math.PI;
	private Color team;
	
	//semi constant (is constant for the instance but vary from instance to instance)
	public double maxSpeed=300;
	public double maxAcc=100;
	public double maxAngAcc=Math.PI*5/4;
	
	//random
	public static Random R=new Random();
	
	//parent game variable
	public static ArrayList<Tower> tlist;
	public static Game game;
	
	private double DV() {
		return (maxSpeed-V)*maxAcc/maxSpeed;
	}

	private double DA() {
		return (Atarg-A)*maxAngAcc;
	}
	
	public void setObjective(double x,double y) {
		Xobj=x;
		Yobj=y;
	}
	
	public Unit(double x,double y,Color c) {
		setX(x);
		setY(y);
		setTeam(c);
		Atarg+=R.nextDouble()*Math.PI/8-Math.PI/16;
		maxSpeed+=R.nextDouble()*50-25;
		A=R.nextDouble()*2*Math.PI;
		
		if(UnitTexture==null)
			try {
				UnitTexture = ImageIO.read(new File("images//assets//towers.png"));
				EmptyMask = ImageIO.read(new File("images//assets//mask_towers.png"));
				
			} catch (IOException e) {
			    e.printStackTrace();
			}
		
		if(!UnitMask.containsKey(team)&&team!=null)
			try {
				UnitTexture = ImageIO.read(new File("images//assets//drone.png"));
				BufferedImage mask = ImageIO.read(new File("images//assets//mask_drone.png"));
				Utilities.FillImage(team, mask);
				UnitMask.put(team, mask);
			} catch (IOException e) {
			    e.printStackTrace();
			}
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		g.setColor(team==null?Color.BLACK:team);
		//g.drawOval((int)(X-size/2), (int)(Y-size/2), size, size);
		g.drawImage(team==null?EmptyMask:UnitMask.get(team),(int)X-size/2,(int)Y-size/2, size,size, null);
		g.drawImage(UnitTexture,(int)X-size/2,(int)Y-size/2, size,size, null);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		//**

		while(A>Math.PI)
			A-=2*Math.PI;
		while(A<-Math.PI)
			A+=2*Math.PI;
		Atarg=Math.atan2(Yobj-Y, Xobj-X);
		if(Atarg<0)
			if(A>Math.PI+Atarg)
				Atarg+=Math.PI*2;
		else
			if(A<Atarg-Math.PI)
				Atarg-=Math.PI*2;
		
		//System.out.println("targ : "+Atarg+" "+A);
		//System.out.println("derivee : "+DV()+" "+DA()+" "+Math.cos(A)*V+" "+Math.sin(A)*V);
		X+=timeStep*Math.cos(A)*V;
		Y+=timeStep*Math.sin(A)*V;

		V+=timeStep*DV();
		A+=timeStep*DA();
		//System.out.println("valeur : "+V+" "+A+" "+X+" "+Y);//*/
		
		//check colllision
		if(age>immuneStep)
			for(Tower t:tlist)
				if(t.collide(X, Y)) {
					if(t.getTeam()!=team) {
						t.setRessources(t.getRessources()-1);
						if(t.getRessources()<0) {
							if(t.getTeam()==null)
								t.setIncome(t.getIncome()*3);
							t.setTeam(team);
						}
						t.setRessources(Math.abs(t.getRessources()));
					}else
						t.setRessources(t.getRessources()+1);
					Kill();
				}
		age++;
	}
	
	private void Kill() {
		game.removeObject(this);
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

	@Override
	public double Zindex() {
		return Y+16;
	}

}
