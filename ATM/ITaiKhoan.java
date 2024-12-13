import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITaiKhoan extends Remote {
    boolean dangKy(int soTaiKhoan, String matKhau) throws RemoteException;
    boolean dangNhap(int soTaiKhoan, String matKhau) throws RemoteException;
    void napTien(int soTaiKhoan, double soTien) throws RemoteException;
    void rutTien(int soTaiKhoan, double soTien) throws RemoteException;
    void chuyenKhoan(int soTaiKhoanGui, int soTaiKhoanNhan, double soTien) throws RemoteException;
    double laySoDu(int soTaiKhoan) throws RemoteException;
    void dangKyThongBao(IThongBao thongBao) throws RemoteException;
}
