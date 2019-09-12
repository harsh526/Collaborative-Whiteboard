import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.*;
import java.awt.Point;
import java.awt.Color;
import java.awt.image.BufferedImage;

public interface UserInterface extends Remote{
	
	public int getId()throws RemoteException;
	public void updateId(int id) throws RemoteException;
	
	public byte[] getInitial() throws RemoteException;
	
	public String getNameUser() throws RemoteException;
	public void setNameUser(String name)throws RemoteException;
	
	public String getColor()throws RemoteException;
	
	public void addUser(Remote u)throws RemoteException;
	public void removeUser(int id)throws RemoteException;

	public void broadcast(String msg) throws RemoteException;
	public void broadcast(String operation ,Vector<Point> list,Color c) throws RemoteException;

	public void notify(String msg, UserInterface from) throws RemoteException;
	public void notify(String operation, Vector<Point> list,Color c) throws RemoteException;

}
