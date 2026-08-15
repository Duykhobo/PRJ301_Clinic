package exception;

/**
 * ValidationException - Ngoại lệ Nâng cao đại diện cho lỗi Kiểm tra dữ liệu đầu vào.
 */
public class ValidationException extends AppException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
