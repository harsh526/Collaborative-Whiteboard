import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;
import java.awt.Point;
import java.awt.Color;
import java.awt.image.BufferedImage;

public interface RegistryService extends Remote{
	public Vector<UserInterface> register(Remote obj)throws RemoteException;
	public void deregister(int id)throws RemoteException;
	public void getUsers(Vector<Point> list,String tag,Color c)throws RemoteException;
}
