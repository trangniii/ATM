import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ATMClientGUI extends JFrame {
    private JTextField txtSoTaiKhoan;
    private JTextField txtSoDu;
    private JTextField txtSoTien;
    private JTextField txtTaiKhoanNhan;
    private ITaiKhoan taiKhoan;
    private int soTaiKhoanHienTai; // Lưu số tài khoản hiện tại

    public ATMClientGUI(int soTaiKhoan) {
        this.soTaiKhoanHienTai = soTaiKhoan; // Nhận số tài khoản khi khởi tạo
        setTitle("ATM Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblSoTaiKhoan = new JLabel("Số tài khoản:");
        lblSoTaiKhoan.setBounds(20, 20, 100, 25);
        add(lblSoTaiKhoan);

        txtSoTaiKhoan = new JTextField(String.valueOf(soTaiKhoanHienTai)); // Hiển thị số tài khoản
        txtSoTaiKhoan.setEditable(false);
        txtSoTaiKhoan.setBounds(150, 20, 200, 25);
        add(txtSoTaiKhoan);

        JLabel lblSoTien = new JLabel("Số tiền:");
        lblSoTien.setBounds(20, 60, 100, 25);
        add(lblSoTien);

        txtSoTien = new JTextField();
        txtSoTien.setBounds(150, 60, 200, 25);
        add(txtSoTien);

        JLabel lblSoDu = new JLabel("Số dư:");
        lblSoDu.setBounds(20, 100, 100, 25);
        add(lblSoDu);

        txtSoDu = new JTextField();
        txtSoDu.setBounds(150, 100, 200, 25);
        txtSoDu.setEditable(false);
        add(txtSoDu);

        JLabel lblTaiKhoanNhan = new JLabel("Tài khoản nhận:");
        lblTaiKhoanNhan.setBounds(20, 140, 120, 25);
        add(lblTaiKhoanNhan);

        txtTaiKhoanNhan = new JTextField();
        txtTaiKhoanNhan.setBounds(150, 140, 200, 25);
        add(txtTaiKhoanNhan);

        JButton btnNapTien = new JButton("Nạp tiền");
        btnNapTien.setBounds(20, 180, 100, 30);
        btnNapTien.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                napTien();
            }
        });
        add(btnNapTien);

        JButton btnRutTien = new JButton("Rút tiền");
        btnRutTien.setBounds(140, 180, 100, 30);
        btnRutTien.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rutTien();
            }
        });
        add(btnRutTien);

        JButton btnChuyenKhoan = new JButton("Chuyển khoản");
        btnChuyenKhoan.setBounds(260, 180, 120, 30);
        btnChuyenKhoan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chuyenKhoan();
            }
        });
        add(btnChuyenKhoan);

        // Đăng xuất button
        JButton btnDangXuat = new JButton("Đăng xuất");
        btnDangXuat.setBounds(20, 220, 100, 30);
        btnDangXuat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dangXuat();
            }
        });
        add(btnDangXuat);

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);
            taiKhoan = (ITaiKhoan) registry.lookup("TaiKhoanServer");
            laySoDu(); // Lấy số dư khi khởi động
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối tới server.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void napTien() {
        if (!validateSoTien()) return;
        try {
            double soTien = Double.parseDouble(txtSoTien.getText());
            taiKhoan.napTien(soTaiKhoanHienTai, soTien);
            JOptionPane.showMessageDialog(this, "Nạp tiền thành công!");
            laySoDu(); // Cập nhật số dư
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi nạp tiền.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rutTien() {
        if (!validateSoTien()) return;
        try {
            double soTien = Double.parseDouble(txtSoTien.getText());
            taiKhoan.rutTien(soTaiKhoanHienTai, soTien);
            JOptionPane.showMessageDialog(this, "Rút tiền thành công!");
            laySoDu(); // Cập nhật số dư
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi rút tiền.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chuyenKhoan() {
        if (!validateSoTien() || !validateTaiKhoanNhan()) return;
        try {
            int soTaiKhoanNhan = Integer.parseInt(txtTaiKhoanNhan.getText());
            double soTien = Double.parseDouble(txtSoTien.getText());
            taiKhoan.chuyenKhoan(soTaiKhoanHienTai, soTaiKhoanNhan, soTien);
            JOptionPane.showMessageDialog(this, "Chuyển khoản thành công!");
            laySoDu(); // Cập nhật số dư
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi chuyển khoản.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void laySoDu() {
        try {
            double soDu = taiKhoan.laySoDu(soTaiKhoanHienTai);
            txtSoDu.setText(String.valueOf(soDu));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy số dư.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dangXuat() {
        // Đoạn mã xử lý đăng xuất
        JOptionPane.showMessageDialog(this, "Đăng xuất thành công!");
        this.dispose(); // Đóng cửa sổ hiện tại
        new LoginRegisterGUI().setVisible(true); // Mở lại cửa sổ đăng nhập
    }
    

    // Phương thức kiểm tra số tiền hợp lệ
    private boolean validateSoTien() {
        if (txtSoTien.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            double soTien = Double.parseDouble(txtSoTien.getText());
            if (soTien <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền phải là một số hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Phương thức kiểm tra tài khoản nhận hợp lệ
    private boolean validateTaiKhoanNhan() {
        if (txtTaiKhoanNhan.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản nhận.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(txtTaiKhoanNhan.getText());
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tài khoản nhận phải là một số nguyên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Giả sử bạn lấy số tài khoản từ màn hình đăng nhập
            new ATMClientGUI(123456).setVisible(true); // Thay thế 123456 bằng số tài khoản thực tế
        });
    }
}
