import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.net.InetAddress;
import java.awt.*;			
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;		
import java.awt.Point;
import java.util.*;
import java.awt.geom.Ellipse2D;
import java.awt.RenderingHints;
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.net.InetAddress; 
import java.net.DatagramSocket;
import java.net.*; 

public class User extends JPanel implements UserInterface, MouseListener, ActionListener, MouseMotionListener{

	static String name=null;
	static int id=-1;
	static String clr=null;
	static Vector<UserInterface> users=null;
	static Hashtable<Integer,UserInterface> directory=null;
	static RegistryService server=null;
	static String myurl=null;
	static String tg=null;
	static BufferedImage bi=null;
	static Point beg=null;
	static boolean flag=false;
	public int screenshot_count = 1;
	
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	public  int choice = 1;
	public  Color writer_color = null;
	public static Color eraser_color = BACKGROUND_COLOR;

	int prev_w = 0, prev_h =0;
	int i = 0, j;					
	BufferedImage grid;
	static Graphics2D gc, g,g1;
	JFrame frame=null;
	
	Vector<Point> points = new Vector<Point>();

	static DefaultListModel model;
	
	static class Received
	{
		String operation;
		Vector<Point> list;
		Color c;
	}
	Vector<Received> buffer=new Vector<>();

	protected User(int i,String n,Color c) throws RemoteException {
		// TODO Auto-generated constructor stub
		name=n;
		id=i;
		writer_color=c;
		users=new Vector<>();
		directory=new Hashtable<>();
		UI(n);
	}

	//@Override
	public int getId() throws RemoteException {
		// TODO Auto-generated method stub
		return id;
	}

	//@Override
	public void updateId(int id) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Set id:" + id);
		this.id=id;
	}

	//@Override
	public String getNameUser() throws RemoteException {
		// TODO Auto-generated method stub
		return name;
	}

	//@Override
	public void setNameUser(String n) throws RemoteException {
		// TODO Auto-generated method stub
		name=n;
	}
	public String getColor() throws RemoteException
	{
		return clr;
	}
	
	public static void main(String args[])
	{
		String my_address = null;
		//UI();
		try(final DatagramSocket socket = new DatagramSocket()){
  			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
  			 my_address = socket.getLocalAddress().getHostAddress();
		}
		catch(Exception e){
			System.out.println("Error");
		}

		System.setProperty("java.rmi.server.hostname", my_address);
        User user=null;
		users=new Vector<>();
		directory=new Hashtable<>();
		if(args.length==4)
		{
			String name=args[0];
			clr=args[1];
			String host=args[2];
			String sName=args[3];
			System.out.println("Registering...");
			try {
				Registry reg=LocateRegistry.getRegistry(host, 4800);
	            String ownhost = InetAddress.getLocalHost().getHostName();
				String neighbor="//" + host + ":4800/" + sName;
				server=(RegistryService)Naming.lookup(neighbor);
				System.out.println("Found registry service:" + server);
				Remote myStub=(Remote)UnicastRemoteObject.exportObject(new User(0,name,Color.decode(clr)),0);
				users=server.register(myStub);
				UserInterface arr[]=null;
				synchronized (users) {
					arr=users.toArray(new UserInterface[0]);
				}
				synchronized (directory) {
					for(UserInterface u:arr)
					{
						directory.put(u.getId(),u);
					}
				}
				System.out.println("Got users");
				for(UserInterface u:arr)
				{
					System.out.println(u.getId() + " " + u.getNameUser());
				}
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try{
				for(UserInterface u : users)
					model.addElement(u.getNameUser());
				model.addElement(name);
			}
			catch(Exception p){
				p.printStackTrace();
			}
			if(users.size()!=0)
				{
					try{
					BufferedImage temp = (BufferedImage)ImageIO.read(new ByteArrayInputStream(users.get(0).getInitial()));
					g.drawImage(temp,0,0,null);
					}catch(Exception e1)
					{
						e1.printStackTrace();
					}
				}

		}
		else
			System.out.println("Incorrect commandline arguments");
			Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { /*
		       my shutdown code here
		    */ 
		    	System.out.println("Removing:" + name);
		    		try {
		    			System.out.println(id + " : " + name);
		    			UserInterface toRemove=null;
		    			synchronized (directory) {
		    				toRemove=directory.get(id);
		    			}
		    			synchronized (users) {
		    				users.remove(toRemove);
		    			}
		    			server.deregister(id);
		    			
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    }
		 });
	}

	//@Override
	public void addUser(final Remote obj) throws RemoteException{
		// TODO Auto-generated method stub
		UserInterface u=null;
		try{
			u=(UserInterface)obj;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		synchronized (users) {
			users.addElement(u);
			model.addElement(u.getNameUser());
		}
		synchronized (directory) {
			try {
				directory.put(u.getId(), u);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			System.out.println("Added:" + u.getNameUser());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//@Override
	public void removeUser(int i) throws RemoteException{
		// TODO Auto-generated method stub
		UserInterface toRemove=null;
		synchronized (directory) {
			toRemove=directory.get(i);
			directory.remove(i);
			//model.remove(i);
		}
		synchronized (users) {
			users.remove(toRemove);
		}
		try {
			System.out.println("Removed:" + toRemove.getNameUser());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void UI(String name){

		frame = new JFrame(name);			
		frame.setSize(1370, 740);					

		frame.setBackground(BACKGROUND_COLOR); 				
		frame.getContentPane().add(this);

		JButton write = new JButton("Write"); 				
		write.addActionListener(this);

		JButton erase = new JButton("Erase"); 				
		erase.addActionListener(this);
		
		JButton line = new JButton("Line");
		line.addActionListener(this);

		JButton rect = new JButton("Rectangle");
		rect.addActionListener(this);

		JButton circle = new JButton("Oval");
		circle.addActionListener(this);

		JButton clear = new JButton("Clear");
		clear.addActionListener(this);

		JButton save = new JButton("Save");
		save.addActionListener(this);

		model = new DefaultListModel();
		JList user_list = new JList(model);
		JScrollPane scroll = new JScrollPane(user_list);

		this.add(scroll);
		this.add(write);
		this.add(erase);
		this.add(line);
		this.add(rect);
		this.add(circle);
		this.add(clear);
		this.add(save);

		this.setLayout(null);
      	write.setBounds(1200,300,120,40);
      	erase.setBounds(1200,350,120,40);
      	line.setBounds(1200,400,120,40);
      	rect.setBounds(1200,450,120,40);
      	circle.setBounds(1200,500,120,40);
      	clear.setBounds(1200,550,120,40);
      	save.setBounds(1200, 600, 120, 40);
      	scroll.setBounds(1200, 100, 120, 150);

		addMouseMotionListener(this);
		addMouseListener(this); 					
		frame.setVisible(true);
		frame.setResizable(false);						
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		g = (Graphics2D) getGraphics();
	}
	

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
     	RenderingHints.VALUE_ANTIALIAS_ON);

     	if (grid == null)
		{
			int w = this.getWidth(); 						
			int h = this.getHeight(); 						
			grid = (BufferedImage) (this.createImage(w-200, h));
			g = grid.createGraphics();
			g.setColor(Color.BLUE);
		}
	
		g2d.drawImage(grid, null, 0, 0);
	}

	public void write(){
		g.setColor(writer_color);
		g.setStroke(new BasicStroke(3));
		g.drawLine(points.get(i-1).x, points.get(i-1).y, points.get(i).x, points.get(i).y);
	}

	public void erase(){
		g.clearRect(points.get(i).x, points.get(i).y, 20, 20);
	}

	public void drawALine(Point p1,Point p2){
		g.setColor(writer_color);
		g.setStroke(new BasicStroke(3));
		g.drawImage(bi,-5,-5,null);
		g.drawLine(p1.x,p1.y,p2.x,p2.y);
	}

	public void drawRectangle(Point p1,Point p2){
		g.drawImage(bi,-5,-5,null);
		g.setColor(writer_color);
		g.setStroke(new BasicStroke(3));
		int w = Math.abs(p2.x - p1.x);
		int h = Math.abs(p2.y - p1.y);
		int x=Math.min(p1.x,p2.x);
		int y=Math.min(p1.y,p2.y);
		g.drawRect(x,y, w, h);
	}

	public void processBuffer()
	{
		if(buffer.size()!=0)
		{
			for(Received r:buffer)
			{
				try{
				notify(r.operation,r.list,r.c);
				}catch(RemoteException e)
				{
					e.printStackTrace();
				}
			}
			buffer.clear();
		}
	}
	public void drawCircle(Point p1,Point p2){
		g.drawImage(bi,-5,-5,null);
		g.setColor(writer_color);
		g.setStroke(new BasicStroke(3));
		int w = Math.abs(p2.x - p1.x);
		int h = Math.abs(p2.y - p1.y);
		int x=Math.min(p1.x,p2.x);
		int y=Math.min(p1.y,p2.y);
		g.drawOval(x,y, w, h);
	}

	public void actionPerformed(ActionEvent e){
		super.removeMouseMotionListener(this);

		if (e.getActionCommand().equals("Write")) 				// action when user choose Erase button
		{	
			
			choice = 1;
			super.addMouseMotionListener(this);
		}

		if (e.getActionCommand().equals("Erase")) 				// action when user choose Erase button
		{
			choice = 2;
			super.addMouseMotionListener(this);
		}

		if (e.getActionCommand().equals("Line")) 				// action when user choose Erase button
		{
			choice = 3;
			super.addMouseMotionListener(this);
		}

		if (e.getActionCommand().equals("Rectangle")) 				// action when user choose Erase button
		{
			choice = 4;
			super.addMouseMotionListener(this);
		}

		if (e.getActionCommand().equals("Oval")) 				// action when user choose Erase button
		{
			choice = 5;
			super.addMouseMotionListener(this);
		}

		if (e.getActionCommand().equals("Clear")) 				// action when user choose Erase button
		{
			choice = 1;
			i = 0;
			points.clear();
			repaint();
			try{
				broadcast("clear");
			}
			catch(RemoteException ep){
				ep.printStackTrace();
			}
			super.addMouseMotionListener(this);
		}

		if (e.getActionCommand().equals("Save")) 				// action when user choose Erase button
		{
			BufferedImage save_image=null;
			Point pp=new Point(0,0);
			SwingUtilities.convertPointToScreen(pp,this);
			Rectangle area=this.getBounds();
			area.x=pp.x;
			area.y=pp.y;
			area.width=1170;
			try{
				save_image = new Robot().createScreenCapture(area);
				File file=new File("screenshot_" + this.getNameUser() + " _"+ screenshot_count +".png");
				ImageIO.write(save_image,"PNG",file);
				screenshot_count++;
				
			}catch(Exception e1){
				e1.printStackTrace();
			}

		super.addMouseMotionListener(this);
		}


	}
	public void mouseExited(MouseEvent e) 						// action when mouse is exited
	{
	}

	public void mouseEntered(MouseEvent e)						// action when mouse is entered
	{
	}

	public void mouseClicked(MouseEvent e) 						// action when mouse is clicked
	{
		
		
	}

	public void mousePressed(MouseEvent e) 						// action when mouse is pressed
	{
		flag=true;
		if(choice==1 || choice==2)
		{	points.add(e.getPoint());
			i++;
		}
		else
		{
			beg=e.getPoint();
			Point pp=new Point(0,0);
			SwingUtilities.convertPointToScreen(pp,this);
			Rectangle area=this.getBounds();
			area.x=pp.x;
			area.y=pp.y;
			area.width=1170;
			try{
				bi=new Robot().createScreenCapture(area);
			}catch(Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		
		Point p=e.getPoint();
		Vector<Point> list=new Vector<>();
		if(choice == 4){
			drawRectangle(beg,p);
			list.add(beg);
			list.add(p);
		}
		else if(choice==3)
		{
			drawALine(beg,p);
			list.add(beg);
			list.add(p);
		}
		else if(choice==5)
		{
			drawCircle(beg,p);
			list.add(beg);
			list.add(p);
		}
		else
			points.add(p);

		try{
			//this.broadcast("Hello");

			if(choice == 1){
				//this.broadcast("write", points);
				tg="write";
			}

			if(choice == 2){
				//this.broadcast("erase", points);
				tg="erase";
			}

			if(choice == 3){
				//this.broadcast("line", points);
				tg="line";
			}

			if(choice == 4){
				//this.broadcast("rectangle", points);
				tg="rectangle";
			}

			if(choice == 5){
				//this.broadcast("circle", points);
				tg="circle";
			}	
			if(choice==1 || choice==2)
				broadcast(tg,points,writer_color);
			else
				broadcast(tg,list,writer_color);
			points.clear();
			i = 0;
			flag=false;
			processBuffer();
			//beg=null;
		}
		catch(RemoteException ep){

			ep.printStackTrace();
		}

	}

	public void mouseDragged(MouseEvent e)
	{
		Point p=e.getPoint();
		
		if(choice == 1)
		{
			points.add(p);
			write();
		}
		else if(choice == 2)
		{
			points.add(p);
			erase();
		}
		else if(choice == 4)
			drawRectangle(beg,p);
		else if(choice==3)
			drawALine(beg,p);
		else if(choice==5)
			drawCircle(beg,p);

		i++;
	}

	public void mouseMoved(MouseEvent arg0)
	{
	}

	public void broadcast(String msg) throws RemoteException{
		
		for(UserInterface u : users){
			u.notify(msg, this);
		}
	}

	public void broadcast(String operation, Vector<Point> list,Color c) throws RemoteException{
		for(UserInterface u:users)
		{
			u.notify(operation,list,c);
		}
		server.getUsers(list,operation,c);
		//server.update(img);
	}


	public void notify(String msg, UserInterface from) throws RemoteException{

		String n = from.getNameUser();
		System.out.println("Got message from " + n + ":" + msg);
		if(msg.equals("clear")){
			choice = 1;
			i = 0;
			repaint();
		}
	}

	public void notify(String operation, Vector<Point> common,Color c) throws RemoteException{

		//points.clear();
		int k=0;
		k = common.size();
		if(flag)
		{
			Received r=new Received();
			r.operation=operation;
			r.list=new Vector<>();
			r.list.addAll(common);
			r.c=c;
			buffer.add(r);
		}
		else{
		int l=0;
		if(operation.equals("write")){
			for(; l < k-1; l++){
				g.setColor(c);
				g.setStroke(new BasicStroke(3));
				g.drawLine(common.get(l).x, common.get(l).y, common.get(l+1).x, common.get(l+1).y);
			}
		}

		if(operation.equals("erase")){
			for(l = 0; l < k; l++){
				g.clearRect(common.get(l).x, common.get(l).y, 20, 20);
			}
		}

		if(operation.equals("line"))
		{
			Point p1=common.get(0);
			Point p2=common.get(1);
			g.setColor(c);
			g.setStroke(new BasicStroke(3));
			g.drawLine(p1.x,p1.y,p2.x,p2.y);
		}
		if(operation.equals("rectangle"))
		{
			Point p1=common.get(0);
			Point p2=common.get(1);
			g.setColor(c);
			g.setStroke(new BasicStroke(3));
			int w = Math.abs(p2.x - p1.x);
			int h = Math.abs(p2.y - p1.y);
			int x=Math.min(p1.x,p2.x);
			int y=Math.min(p1.y,p2.y);
			g.drawRect(x,y, w, h);
		}
		if(operation.equals("circle"))
		{
			Point p1=common.get(0);
			Point p2=common.get(1);
			g.setColor(c);
			g.setStroke(new BasicStroke(3));
			int w = Math.abs(p2.x - p1.x);
			int h = Math.abs(p2.y - p1.y);
			int x=Math.min(p1.x,p2.x);
			int y=Math.min(p1.y,p2.y);
			g.drawOval(x,y, w, h);
		}
		}
	}
	
	//@Override
	public byte[] getInitial()throws RemoteException
	{
		System.out.println("Hi!");
		BufferedImage img=null;
		Point pp=new Point(0,0);
		SwingUtilities.convertPointToScreen(pp,this);
		Rectangle area=this.getBounds();
		area.x=pp.x;
		area.y=pp.y;
		area.width=1170;
		try{
			img=new Robot().createScreenCapture(area);
		}catch(Exception e1)
		{
			e1.printStackTrace();
		}
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		File file=new File("a.png");
		try{
        ImageIO.write(img, "PNG", bytes);
		ImageIO.write(img,"PNG",file);
		System.out.println("Hello");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
        return (bytes.toByteArray());
	}
}