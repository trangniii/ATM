import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IThongBao extends Remote {
    void nhanThongBao(String message) throws RemoteException;
}
