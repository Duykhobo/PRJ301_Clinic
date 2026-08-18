package util;

import javax.servlet.http.HttpServletRequest;

/**
 * Tiện ích hỗ trợ xử lý Phân trang (Pagination) cho các Servlet.
 * Giúp loại bỏ các khối try-catch lặp lại khi parse số trang.
 */
public class PaginationUtil {

    /**
     * Lấy và parse tham số trang từ request một cách an toàn.
     * Nếu người dùng nhập linh tinh (ví dụ: ?page=abc) hoặc số âm, tự động trả về
     * trang 1.
     *
     * @param request   HTTP request
     * @param paramName Tên của query parameter (thường là "page")
     * @return Số trang hợp lệ (min = 1)
     */
    public static int parsePage(HttpServletRequest request, String paramName) {
        try {
            String val = request.getParameter(paramName);
            return (val != null) ? Math.max(1, Integer.parseInt(val)) : 1;
        } catch (NumberFormatException e) {
            return 1; // Bắt lỗi an toàn, fallback về trang đầu tiên
        }
    }

    /**
     * Tính toán tổng số trang dựa trên tổng số bản ghi và số lượng trên mỗi trang.
     *
     * @param totalRecords Tổng số bản ghi đếm được từ CSDL
     * @param pageSize     Số bản ghi hiển thị trên 1 trang
     * @return Tổng số trang (min = 1)
     */
    public static int totalPages(int totalRecords, int pageSize) {
        if (pageSize <= 0)
            return 1;
        return Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));
    }

    /**
     * Tính toán chỉ số bắt đầu (Offset) dùng cho câu lệnh SQL: OFFSET ? ROWS FETCH
     * NEXT ? ROWS ONLY.
     *
     * @param page     Trang hiện tại (lưu ý: phải đảm bảo page <= totalPages trước
     *                 khi truyền vào đây)
     * @param pageSize Số bản ghi trên 1 trang
     * @return Chỉ số Offset
     */
    public static int offset(int page, int pageSize) {
        return Math.max(0, (page - 1) * pageSize);
    }
}