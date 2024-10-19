package tower;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tge.BasicListener;
import tge.Drawable;
import tge.Sim;
import tge.Updatable;
import tge.Utilities;
import tge.object.BackgroundPanel;

public class Game extends Sim{

	//Constant
	public static final int MAIN_MENU=0;
	public static final int IN_GAME=1;
	public static final int END_GAME_WIN=2;
	public static final int END_GAME_LOSE=3;
	public static final int FINAL_SCREEN=4;

	//assets
	private List<Updatable> layer1=Collections.synchronizedList(new ArrayList<Updatable>());
	private ArrayList<Drawable> Slayer=new ArrayList<Drawable>();
	public ArrayList<Tower> towers=new ArrayList<Tower>();
	private Collection[] assetLists=new Collection[] {layer1,Slayer,towers};
	
	//textures & ressources
	public BufferedImage Background = null;
	private File[] levels;
	private Font mainfont=null;
	
	//data
	public Color player;
	private int level = 0;
	
	private int state = 0;
	
	//Gui
	private JComponent nextMenu;
	
	/**
	 * ===================================================================
	 */
	
	public static void main(String... args) {
		
		
		
		
		//creating game instance
		Game g=new Game();
		BasicListener bl=new BasicListener();
		
		Tower.listener=bl;
		Tower.game=g;
		
		StoopidBot.game=g;
		StoopidBot.tlist=g.towers;
		
		Unit.tlist=g.towers;
		Unit.game=g;
		
		Game.FrameLimiterLogic=120;
		Game.FrameLimiterGraphic=60;
		
		System.out.println("starting game...");
		JFrame dat=g.setupFrame();
		dat.setSize(1920,1080);
		dat.setResizable(false);
		
		BufferedImage favicon=null;
		try {
			favicon = ImageIO.read(new File("images//assets//icon.png"));
		} catch (IOException e) {e.printStackTrace();}
		dat.setIconImage(favicon);
		g.loadMenu("main");
		dat.setVisible(true);
        
		g.loadLevel(g.level);
		g.player=Color.RED;
		
		g.addMouseListener(bl);
		g.StartLoop();
	}
	
	private JButton simpleButton(String text) {
		JButton output=new JButton(text);
		output.setBackground(new Color(239,150,80));
		output.setBorder(null);
		output.setFocusPainted(false);
		return output;
	}
	
	private void loadFile() {
	    File folder = new File("./levels/");
	    
	    // Check if the folder exists and is a directory
	    if (folder.exists() && folder.isDirectory()) {
	        // List all .txt files in the folder
	        levels = folder.listFiles(new FilenameFilter() {
	            @Override
	            public boolean accept(File dir, String name) {
	                return name.toLowerCase().endsWith(".lvl");
	            }
	        });
	    } else {
	        System.out.println("problem !! , no levels found !!");
	    }
	}

	public Game() {
		
		//laod font
		try {
            // Load the TTF font from the file path
            File fontFile = new File("font/font.ttf"); // Change to your TTF file path
            mainfont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
        } catch (FontFormatException | IOException e) {e.printStackTrace();}
		
		//laod background
		try {
			Background = ImageIO.read(new File("images//assets//background.png"));
		} catch (IOException e) {e.printStackTrace();}
		
		//load level list
		loadFile();
		
		//creating menus:
		
		//main menu
		JPanel main=new BackgroundPanel("./images/assets/title3.png");
		JButton play=simpleButton("");
		
		//panel
		main.setLayout(new GridBagLayout());
		
		//button play
		play.setPreferredSize(new Dimension(100,100));
		play.setSize(new Dimension(100,100));
		play.setContentAreaFilled(false);
		BufferedImage Picon=null;
		try {
			Picon= ImageIO.read(new File("./images/assets/play_.png"));
		}catch (IOException e){e.printStackTrace();}
		play.setIcon(new ImageIcon( Picon.getScaledInstance(play.getWidth(), play.getHeight(), Picon.SCALE_SMOOTH)));
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadMenu(Sim.GAME_SCREEN);
				paused=false;
				state=Game.IN_GAME;
				System.out.println("lanching game "+state);
		}});
		Utilities.addHighlight(play);
		
		GridBagConstraints gbc = new GridBagConstraints();
	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.insets = new Insets(250,0,0,0);
	    gbc.anchor = GridBagConstraints.CENTER;
	    main.add(play, gbc);

		this.addMenu(main, "main");
		
		
		
		//end game sub Menu
		
		JPanel miniScreen=new BackgroundPanel("./images/assets/menu.png");
		miniScreen.setLayout(new GridBagLayout());
		miniScreen.setOpaque(false);
		miniScreen.setBackground(new Color(0,0,0,0));

		JButton next=simpleButton("");
		try {
			Picon= ImageIO.read(new File("./images/assets/next.png"));
		}catch (IOException e){e.printStackTrace();}
		next.setIcon(new ImageIcon( Picon.getScaledInstance(play.getWidth(), play.getHeight(), Picon.SCALE_SMOOTH)));
		JButton replay=simpleButton("");
		try {
			Picon= ImageIO.read(new File("./images/assets/replay.png"));
		}catch (IOException e){e.printStackTrace();}
		replay.setIcon(new ImageIcon( Picon.getScaledInstance(play.getWidth(), play.getHeight(), Picon.SCALE_SMOOTH)));
		this.setLayout(null);
		this.add(miniScreen);

		next.setSize(new Dimension(100,100));
		next.setPreferredSize(new Dimension(100,100));
		next.setContentAreaFilled(false);
		Utilities.addHighlight(next);
		replay.setSize(new Dimension(100,100));
		replay.setPreferredSize(new Dimension(100,100));
		replay.setContentAreaFilled(false);
		Utilities.addHighlight(replay);
		
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("next");
				level++;
				if(level==levels.length) {
					loadMenu("credit");
					state=Game.FINAL_SCREEN;
					return;
				}
				loadLevel(level);
				loadMenu(Game.GAME_SCREEN);
				nextMenu.setVisible(false);
				state=Game.IN_GAME;
			}
		});
		
		replay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("replay");
				loadLevel(level);
				loadMenu(Game.GAME_SCREEN);
				nextMenu.setVisible(false);
				nextMenu.getComponent(1).setVisible(true);
				state=Game.IN_GAME;
			}
		});
		
		gbc = new GridBagConstraints();
	    gbc.fill = GridBagConstraints.NONE; 
	    gbc.insets = new Insets(10,20,10,20);
	    gbc.anchor = GridBagConstraints.CENTER;
	    
	    gbc.gridx = 0;
        gbc.gridy = 1;
		miniScreen.add(replay, gbc);
		
		gbc.gridx = 1;
		miniScreen.add(next, gbc);
		miniScreen.setPreferredSize(new Dimension(300,240));
		
		
		JLabel text=new JLabel("test text");
		text.setFont(mainfont.deriveFont(48f));
		text.setForeground(Color.WHITE);
		gbc.gridy=0;
		gbc.gridx=0;
		gbc.gridwidth=2;
		miniScreen.add(text, gbc);
		

		nextMenu=miniScreen;
		nextMenu.setVisible(false);
		//final Screen
		JPanel credit=new BackgroundPanel("./images/assets/credit2.png");
		addMenu(credit,"credit");
	}
	
	public void loadLevel(int i) {
		loadLevel(levels[i].getAbsolutePath());
	}

	public void loadLevel(String lvl) {
		Level l=new Level(lvl);
		l.setGameInstance(this);
		l.loadLevel();
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int lvl) {
		level=lvl;
		this.loadLevel(level);
	}

	public void addObjects(Collection<Object> objs) {
		for(Object o:objs)
			addObject(o);
	}
	
	public void addObject(Object o) {
		if(o instanceof Updatable)
			layer1.add((Updatable) o);
		else if(o instanceof Drawable)
			Slayer.add((Drawable) o);
		
		if(o instanceof Tower)
			towers.add((Tower)o);
	}
	
	public void removeObject(Object o) {
		for(Collection c:assetLists)
			c.remove(o);
	}

	public void updateOrder() {
		Collections.sort(layer1, (p1, p2) -> Double.compare(p1.Zindex(), p2.Zindex()));
		Collections.sort(Slayer, (p1, p2) -> Double.compare(p1.Zindex(), p2.Zindex()));
	}
	
	public void clearAssets() {
		layer1.clear();
		Slayer.clear();
		towers.clear();
	}
	
	//WARNING : DO NOT USE THAT TO ADD TOWER
	public void addUpdatables(Collection<Updatable> dat) {
		layer1.addAll(dat);}
	public void addUpdatable(Updatable asset) {
		layer1.add(asset);}

	public void addStatic(Collection<Drawable> dat) {
		Slayer.addAll(dat);}
	public void addStatic(Drawable asset) {
		Slayer.add(asset);}

	@Override
	public int LogicLoop() {
		super.LogicLoop();
		for(int i=0;i<layer1.size();i++) {
			layer1.get(i).update();
		}
		
		
		//check end game if game not ended
		if(this.state==Game.IN_GAME) {
			boolean playAlive=false;
			boolean enemyAlive=false;
			for(Tower t:towers) {
				if(!playAlive && t.getTeam()==this.player)
					playAlive=true;
				if(!enemyAlive && t.getTeam()!=this.player && t.getTeam()!=null)
					enemyAlive=true;
				if(playAlive && enemyAlive)
					break;
			}
			
			//check defeat
			if(!playAlive) {
				System.out.println("player Defeat !!!");
				this.state=Game.END_GAME_LOSE;
				endGame();
			}
			
			//check victory
			if(!enemyAlive) {
				System.out.println("player Victory !!!");
				this.state=Game.END_GAME_WIN;
				endGame();
			}
		}
		
		
		return 0;
	}
	

	@Override 
	public JFrame setupFrame() {
		super.setupFrame();
		return this.sim;
	}
	@Override 
	public JFrame setupFrame(JFrame frame) {
		super.setupFrame(frame);
		return this.sim;
	}

	public void endGame() {
		this.loadMenu(GAME_SCREEN);
		nextMenu.setVisible(true);
		if(state==Game.END_GAME_LOSE)
			nextMenu.getComponent(1).setVisible(false);
		((JLabel)nextMenu.getComponent(2)).setText("you "+(state==Game.END_GAME_LOSE?"lose...":"win !!"));
		nextMenu.setBounds((this.getWidth()-300)/2,(this.getHeight()-240)/2,300,240);
	}
	
	@Override
	public int GraphicLoop() {
		super.GraphicLoop();
		updateOrder();
		return 0;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		 Graphics2D g2 = (Graphics2D)g;
		    
		    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        
		    g2.setStroke(new BasicStroke(3));
		g.setFont(mainfont.deriveFont(30f));
		g.drawImage(Background, 0,0,this.getWidth(),this.getHeight(),null);
		//this.frameDebug(g);
		for(Drawable asset:Slayer)
			asset.draw(g);
		for(int i=0;i<layer1.size();i++)//Updatable asset:layer1)
			layer1.get(i).draw(g);
		
	}
}
