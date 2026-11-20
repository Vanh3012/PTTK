# Hệ thống quản lý siêu thị điện máy - Đề 7

Java 17, JSP, JDBC và Tomcat 10.1. Chỉ triển khai hai module: **đăng ký thành viên** và **nhập hàng từ nhà cung cấp**. Dự án Maven mở trực tiếp trong NetBeans.

## Chạy ngay

Mở `run.cmd`, sau đó truy cập <http://localhost:8080>. Bản đã đóng gói có sẵn trong `target`, chỉ cần JDK 17. Giữ cửa sổ chạy máy chủ mở; nhấn `Ctrl+C` để dừng.

Nếu chưa có bản biên dịch, `run.cmd` tự dùng Maven để dựng lại. Lần tải thư viện đầu tiên cần Internet.

Hoặc chạy từ terminal tại thư mục dự án:

```powershell
mvn "-Dmaven.repo.local=tmp/m2" compile exec:java
```

Trong NetBeans: **File > Open Project**, chọn thư mục `PTTK`, chọn JDK 17, mở `src/main/java/vn/ptit/qlst9/Main.java` và chọn **Run File**. Chạy tại thư mục gốc dự án để đọc được `src/main/webapp`.

Nếu cổng 8080 đang được sử dụng, dừng lần chạy trước hoặc chạy:

```powershell
mvn "-Dmaven.repo.local=tmp/m2" -Dport=8081 compile exec:java
```

## Tài khoản và dữ liệu mẫu

| Loại | Số điện thoại | Mật khẩu |
|---|---|---|
| Nhân viên kho | `0912000001` đến `0912000010` | `123456` |
| Khách hàng | `0901000001` đến `0901000010` | `123456` |

CSDL đã được khởi tạo tại `data/qlst9.mv.db`. Bao gồm 20 thành viên (10 khách hàng, 10 nhân viên kho), 10 nhà cung cấp, 10 nhóm hàng, 20 mặt hàng, 10 hóa đơn nhập và 10 dòng chi tiết. Dữ liệu gốc tự nạp khi CSDL chưa có dữ liệu; 10 mặt hàng bổ sung được nạp cả vào CSDL đã có, kiểm tra tên và model để không nạp trùng hoặc ghi đè dữ liệu đã lưu. Mật khẩu được băm bằng PBKDF2, không lưu mật khẩu rõ.

Dữ liệu và cấu trúc bảng nằm tại `src/main/resources/seed.sql` và `schema.sql`. H2 chạy nhúng qua JDBC, không cần cài MySQL. Các tên bảng và quan hệ giữ theo sơ đồ CSDL trong báo cáo, giới hạn ở các bảng phục vụ hai module. Đơn vị tiền là VNĐ.

## Luồng sử dụng

**Đăng ký:** trang chủ > Đăng ký thành viên > nhập họ tên, email, số điện thoại, mật khẩu, nhập lại mật khẩu > Đăng ký. Kiểm tra dữ liệu, trùng email hoặc điện thoại, sinh mã khách hàng và chuyển sang đăng nhập. Các trường đã nhập được giữ lại nếu báo lỗi.

**Nhập hàng:** đăng nhập nhân viên kho > Nhập hàng > tìm nhà cung cấp theo tên > Nhập > Thêm mặt hàng > tìm theo tên > Chọn > nhập số lượng và đơn giá > Xác nhận thêm > Submit > In hóa đơn. Ví dụ tìm `Samsung`, sau đó tìm `Smart Tivi`.

- Không có nhà cung cấp: Thêm mới nhà cung cấp > Lưu; tự chuyển sang lập hóa đơn với nhà cung cấp vừa tạo.
- Không có mặt hàng: Thêm mới mặt hàng > Lưu; tự chuyển sang nhập số lượng và đơn giá của mặt hàng vừa tạo. Tồn kho khởi tạo bằng 0. Nhóm hàng lấy từ dữ liệu đã nạp. Có thể lưu ảnh PNG/JPEG/GIF tối đa 3 MB vào CSDL.
- Hóa đơn tạm nằm trong phiên đăng nhập. Thêm/xóa dòng chưa làm tăng tồn kho. Hủy bỏ > Có sẽ bỏ hóa đơn tạm, trở về trang chủ nhân viên kho. Các nhà cung cấp/mặt hàng mới đã lưu riêng vẫn được giữ lại.
- Cặp hóa đơn + mặt hàng là khóa chính của chi tiết theo thiết kế. Khi chọn lại cùng mặt hàng và cùng đơn giá, số lượng được cộng vào dòng đã có. Nếu đổi đơn giá, số lượng và đơn giá mới thay thế dòng đó; có thể xóa dòng rồi nhập lại để thay đổi toàn bộ.
- Submit lưu hóa đơn, chi tiết và tăng tồn kho trong cùng giao dịch; lỗi sẽ rollback toàn bộ. Nút lưu gửi lại không cộng tồn kho lần nữa. Số lượng và đơn giá nhập phải là số nguyên dương.
- In hóa đơn dùng hộp thoại in của trình duyệt, có CSS riêng để ẩn nút thao tác khi in. Có thể chọn máy in hoặc lưu PDF. Chưa kiểm tra bản in bằng máy in vật lý.

## Đối chiếu thiết kế

Lấy **sơ đồ lớp chi tiết và Scenario v3.0 (trang 45-53)** làm thiết kế triển khai cuối cùng. Các hình giao diện trang 41-45 được dùng để xác định trường và nút, còn bố cục được làm lại. Không đưa các nút quản lý mặt hàng, quản lý nhà cung cấp, duyệt đơn, thống kê, bán hàng hoặc quên mật khẩu vào giao diện vì nằm ngoài hai module đang xây dựng.

Các phần mô tả trước đó có `receiveDate`, `paymentStatus`, `dueDate`, thông số kỹ thuật nhưng không có trong sơ đồ lớp chi tiết/CSDL cuối, nên không bổ sung vào bản này. Đăng ký kiểm tra trùng **email và số điện thoại** theo v3.0; câu “email hoặc mật khẩu” ở kịch bản cũ được hiểu là lỗi diễn đạt.

| Thành phần | Tệp/lớp đúng thiết kế |
|---|---|
| Đăng ký | `gdHome9.jsp`, `gdRegister9.jsp`, `doRegister9.jsp`, `gdLogin9.jsp` |
| Nhập hàng | `gdWarehouseHome9.jsp`, `gdSearchSupplier9.jsp`, `gdAddSupplier9.jsp`, `doSaveSupplier9.jsp`, `gdImportInvoice9.jsp`, `gdSearchProduct9.jsp`, `gdAddProduct9.jsp`, `doSaveProduct9.jsp`, `gdPrintInvoice9.jsp` |
| DAO | `DAO`, `MemberDAO`, `CustomerDAO`, `SupplierDAO`, `ProductDAO`, `ImportInvoiceDAO`, `ImportInvoiceDetailDAO` |
| Thực thể | `Member9`, `Customer9`, `Staff9`, `WarehouseStaff9`, `Supplier9`, `Category9`, `Product9`, `ProductImage9`, `ImportInvoice9`, `ImportInvoiceDetail9` |

Tiền dùng `BigDecimal` để khớp cột `DECIMAL` của CSDL, tránh sai số `float`. Các lớp `Main`, `Application`, `AccessFilter`, `Web` chỉ khởi chạy máy chủ, khởi tạo dữ liệu, kiểm tra phiên/CSRF và dùng chung hàm định dạng; không tạo thêm module hay màn hình. JSP gọi DAO trực tiếp qua các trang xử lý `do…9.jsp` như sơ đồ, không thêm Spring hoặc ORM.

## Kiểm thử và đóng gói

```powershell
mvn "-Dmaven.repo.local=tmp/m2" test
mvn "-Dmaven.repo.local=tmp/m2" package
```

10 kiểm thử đã đạt: 9 kiểm thử CSDL/nghiệp vụ và 1 kiểm thử xuyên suốt bằng HTTP trên Tomcat thật, bao gồm tất cả trang JSP, lỗi đăng ký, đăng nhập/phân quyền, thêm nhà cung cấp/mặt hàng, hóa đơn rỗng, thêm/xóa/hủy, lưu/in, chống lưu lặp, rollback khi cập nhật tồn kho thất bại và nhập đồng thời. Các kiểm thử dùng CSDL bộ nhớ riêng, không thay đổi `data/qlst9.mv.db`.

`target/qlst9.war` có thể triển khai lên Tomcat 10.1 với Java 17. CSDL khi đó nằm trong thư mục làm việc của Tomcat; có thể chỉ định đường dẫn tuyệt đối bằng thuộc tính JVM `-Dqlst.db=jdbc:h2:file:D:/duong-dan/qlst9;DB_CLOSE_ON_EXIT=FALSE`.
