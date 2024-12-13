import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginRegisterGUI extends JFrame {
    private JTextField txtSoTaiKhoan;
    private JTextField txtMatKhau;
    private ITaiKhoan taiKhoan;

    public LoginRegisterGUI() {
        setTitle("Đăng nhập / Đăng ký");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblSoTaiKhoan = new JLabel("Số tài khoản:");
        lblSoTaiKhoan.setBounds(20, 20, 100, 25);
        add(lblSoTaiKhoan);

        txtSoTaiKhoan = new JTextField();
        txtSoTaiKhoan.setBounds(150, 20, 200, 25);
        add(txtSoTaiKhoan);

        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        lblMatKhau.setBounds(20, 60, 100, 25);
        add(lblMatKhau);

        txtMatKhau = new JPasswordField();
        txtMatKhau.setBounds(150, 60, 200, 25);
        add(txtMatKhau);

        JButton btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setBounds(50, 100, 120, 30);
        btnDangNhap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dangNhap();
            }
        });
        add(btnDangNhap);

        JButton btnDangKy = new JButton("Đăng ký");
        btnDangKy.setBounds(200, 100, 120, 30);
        btnDangKy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dangKy();
            }
        });
        add(btnDangKy);

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);
            taiKhoan = (ITaiKhoan) registry.lookup("TaiKhoanServer");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối tới server.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dangNhap() {
        String soTaiKhoanStr = txtSoTaiKhoan.getText();
        String matKhau = txtMatKhau.getText();
    
        if (soTaiKhoanStr.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tài khoản và mật khẩu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try {
            int soTaiKhoan = Integer.parseInt(soTaiKhoanStr);
            boolean isSuccess = taiKhoan.dangNhap(soTaiKhoan, matKhau);
            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                // Chuyển sang màn hình ATMClientGUI và truyền số tài khoản
                new ATMClientGUI(soTaiKhoan).setVisible(true);
                this.dispose(); // Đóng cửa sổ đăng nhập
            } else {
                JOptionPane.showMessageDialog(this, "Số tài khoản hoặc mật khẩu không chính xác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tài khoản phải là một số nguyên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi đăng nhập.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void dangKy() {
        String soTaiKhoanStr = txtSoTaiKhoan.getText();
        String matKhau = txtMatKhau.getText();

        if (soTaiKhoanStr.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tài khoản và mật khẩu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int soTaiKhoan = Integer.parseInt(soTaiKhoanStr);
            boolean isSuccess = taiKhoan.dangKy(soTaiKhoan, matKhau);
            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Số tài khoản đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi đăng ký.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginRegisterGUI().setVisible(true);
        });
    }
}
