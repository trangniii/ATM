import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class TaiKhoanServer extends UnicastRemoteObject implements ITaiKhoan {
    private Map<Integer, Double> danhSachTaiKhoan;
    private Map<Integer, String> matKhauTaiKhoan; // Lưu mật khẩu
    private IThongBao thongBaoClient;
    private static final String FILE_PATH = "danhSachTaiKhoan.xml";

    protected TaiKhoanServer() throws RemoteException {
        danhSachTaiKhoan = new HashMap<>();
        matKhauTaiKhoan = new HashMap<>();
        docDuLieuXML(); // Đọc dữ liệu từ file XML khi khởi động
    }

    private void ghiDuLieuXML() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("DanhSachTaiKhoan");
            doc.appendChild(rootElement);

            for (Map.Entry<Integer, Double> entry : danhSachTaiKhoan.entrySet()) {
                Element taiKhoan = doc.createElement("TaiKhoan");

                Element soTaiKhoan = doc.createElement("SoTaiKhoan");
                soTaiKhoan.appendChild(doc.createTextNode(entry.getKey().toString()));
                taiKhoan.appendChild(soTaiKhoan);

                Element soDu = doc.createElement("SoDu");
                soDu.appendChild(doc.createTextNode(entry.getValue().toString()));
                taiKhoan.appendChild(soDu);
                
                // Lưu mật khẩu vào XML (nên mã hóa mật khẩu trong thực tế)
                Element matKhau = doc.createElement("MatKhau");
                matKhau.appendChild(doc.createTextNode(matKhauTaiKhoan.get(entry.getKey())));
                taiKhoan.appendChild(matKhau);

                rootElement.appendChild(taiKhoan);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(FILE_PATH));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void docDuLieuXML() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                // Tạo file mới nếu chưa tồn tại
                ghiDuLieuXML();
                return;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("TaiKhoan");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                int soTaiKhoan = Integer.parseInt(element.getElementsByTagName("SoTaiKhoan").item(0).getTextContent());
                double soDu = Double.parseDouble(element.getElementsByTagName("SoDu").item(0).getTextContent());
                String matKhau = element.getElementsByTagName("MatKhau").item(0).getTextContent(); // Đọc mật khẩu

                danhSachTaiKhoan.put(soTaiKhoan, soDu);
                matKhauTaiKhoan.put(soTaiKhoan, matKhau); // Lưu mật khẩu
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean dangKy(int soTaiKhoan, String matKhau) throws RemoteException {
        if (danhSachTaiKhoan.containsKey(soTaiKhoan)) {
            return false; // Tài khoản đã tồn tại
        }
        danhSachTaiKhoan.put(soTaiKhoan, 0.0); // Tạo tài khoản mới với số dư bằng 0
        matKhauTaiKhoan.put(soTaiKhoan, matKhau); // Lưu mật khẩu
        ghiDuLieuXML(); // Cập nhật dữ liệu vào file XML
        return true;
    }

    public boolean dangNhap(int soTaiKhoan, String matKhau) throws RemoteException {
        String storedPassword = matKhauTaiKhoan.get(soTaiKhoan);
        return storedPassword != null && storedPassword.equals(matKhau);
    }

    public void napTien(int soTaiKhoan, double soTien) throws RemoteException {
        danhSachTaiKhoan.put(soTaiKhoan, danhSachTaiKhoan.getOrDefault(soTaiKhoan, 0.0) + soTien);
        ghiDuLieuXML();  // Cập nhật dữ liệu vào file XML
        if (thongBaoClient != null) {
            thongBaoClient.nhanThongBao("Nạp tiền thành công! Số dư hiện tại: " + danhSachTaiKhoan.get(soTaiKhoan));
        }
    }

    public void rutTien(int soTaiKhoan, double soTien) throws RemoteException {
        if (danhSachTaiKhoan.getOrDefault(soTaiKhoan, 0.0) >= soTien) {
            danhSachTaiKhoan.put(soTaiKhoan, danhSachTaiKhoan.get(soTaiKhoan) - soTien);
            ghiDuLieuXML();  // Cập nhật dữ liệu vào file XML
            if (thongBaoClient != null) {
                thongBaoClient.nhanThongBao("Rút tiền thành công! Số dư hiện tại: " + danhSachTaiKhoan.get(soTaiKhoan));
            }
        } else {
            if (thongBaoClient != null) {
                thongBaoClient.nhanThongBao("Số dư không đủ.");
            }
        }
    }

    public void chuyenKhoan(int soTaiKhoanGui, int soTaiKhoanNhan, double soTien) throws RemoteException {
        if (danhSachTaiKhoan.getOrDefault(soTaiKhoanGui, 0.0) >= soTien) {
            danhSachTaiKhoan.put(soTaiKhoanGui, danhSachTaiKhoan.get(soTaiKhoanGui) - soTien);
            danhSachTaiKhoan.put(soTaiKhoanNhan, danhSachTaiKhoan.getOrDefault(soTaiKhoanNhan, 0.0) + soTien);
            ghiDuLieuXML();  // Cập nhật dữ liệu vào file XML
            if (thongBaoClient != null) {
                thongBaoClient.nhanThongBao("Chuyển khoản thành công! Số dư hiện tại: " + danhSachTaiKhoan.get(soTaiKhoanGui));
            }
        } else {
            if (thongBaoClient != null) {
                thongBaoClient.nhanThongBao("Số dư không đủ.");
            }
        }
    }

    public double laySoDu(int soTaiKhoan) throws RemoteException {
        return danhSachTaiKhoan.getOrDefault(soTaiKhoan, 0.0);
    }

    public void dangKyThongBao(IThongBao thongBao) throws RemoteException {
        this.thongBaoClient = thongBao;
    }

    // Khởi động server RMI trên cổng cụ thể
    public static void main(String[] args) {
        try {
            // Tạo registry RMI trên cổng 5000
            Registry registry = LocateRegistry.createRegistry(5000);

            // Tạo đối tượng server và đăng ký với RMI registry
            TaiKhoanServer server = new TaiKhoanServer();
            registry.rebind("TaiKhoanServer", server);

            System.out.println("Server RMI đã sẵn sàng trên cổng 5000...");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
