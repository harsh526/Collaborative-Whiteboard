import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import java.util.Hashtable;
import java.rmi.Remote;
import java.awt.Point;
import java.awt.Color;
import java.net.InetAddress; 
import java.net.DatagramSocket;
import java.net.*; 

public class RegistryServiceImpl extends UnicastRemoteObject implements RegistryService{
	
	//static Vector<String> registered=null;
	static Vector<UserInterface> UserInterfaces=null;
	static Hashtable<Integer,UserInterface> members=null;
	int count=0;
	
	protected RegistryServiceImpl() throws RemoteException {
		super();
		//registered=new Vector<>();
		UserInterfaces=new Vector<>();
		members=new Hashtable<>();
		// TODO Auto-generated constructor stub
	}
	@Override
	public Vector<UserInterface> register(final Remote obj) throws RemoteException {
		// TODO Auto-generated method stub
		int id=count++;
		UserInterface u=(UserInterface)obj;
		try {
		u.updateId(id);
		}catch(RemoteException e)
		{
			e.printStackTrace();
		}
		UserInterface arr[]=null;
		Vector<UserInterface> toReturn=null;
		synchronized (UserInterfaces) {
			arr=UserInterfaces.toArray(new UserInterface[0]);
			toReturn=(Vector<UserInterface>)UserInterfaces.clone();
			UserInterfaces.add(u);
		}
		synchronized (members) {
			members.put(id, u);
		}
		try {
		for(UserInterface u1:arr)
		{
			u1.addUser(u);
		}
		System.out.println("Registered:" + u.getNameUser());
		}catch(RemoteException e)
		{
			e.printStackTrace();
		}
		
		return toReturn;
	}
	@Override
	public void deregister(int id) throws RemoteException {
		// TODO Auto-generated method stub
		UserInterface u=null;
		synchronized (members) {
			u=members.get(id);
			members.remove(id);
		}
		UserInterface arr[]=null;
		synchronized (UserInterfaces) {
			UserInterfaces.remove(u);
			arr=UserInterfaces.toArray(new UserInterface[0]);
		}
		try {
		for(UserInterface u1:arr)
		{
			u1.removeUser(id);
		}
		System.out.println("Deregistered:" + u.getNameUser());
		}catch(RemoteException e)
		{
			e.printStackTrace();
		}
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

		System.setProperty("java.rmi.server.hostname",my_address);
		String name=args[0];
		try {
			Registry reg=LocateRegistry.createRegistry(4800);
			System.out.println("Registry created.");
			RegistryServiceImpl service=new RegistryServiceImpl();
			String url="//0.0.0.0:4800/"+name;
			Naming.bind(url, service);
			System.out.println("Service registered as:" + name);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getUsers(Vector<Point> list,String message,Color c) throws RemoteException
	{
		try{
		for(UserInterface u:UserInterfaces)
		{
			u.notify(message,list,c);
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
