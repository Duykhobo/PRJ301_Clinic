package constant;

/**
 * SystemConstant - Quản lý các Hằng số Trạng thái, Key Session, Attributes & Default Fallbacks của Hệ thống.
 */
public class SystemConstant {

    // Session & Request Attribute Keys
    public static final String SESSION_USER = "LOGIN_USER";
    public static final String CSRF_TOKEN_SESSION = "CSRF_TOKEN";
    public static final String CSRF_TOKEN_REQ_ATTR = "csrfToken";
    public static final String ERROR_MESSAGE_ATTR = "errorMessage";
    public static final String SUCCESS_MESSAGE_ATTR = "successMessage";
    public static final String REDIRECT_PARAM = "redirect";

    // Appointment Status
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // Payment Status
    public static final String PAYMENT_UNPAID = "UNPAID";
    public static final String PAYMENT_PAID = "PAID";

    // Payment Methods
    public static final String METHOD_CASH = "CASH";
    public static final String METHOD_SEPAY_QR = "SEPAY_QR";

    // Dynamic Setting Keys (bảng ClinicSettings)
    public static final String KEY_CLINIC_NAME = "CLINIC_NAME";
    public static final String KEY_CLINIC_HOTLINE = "CLINIC_HOTLINE";
    public static final String KEY_CLINIC_EMAIL = "CLINIC_EMAIL";
    public static final String KEY_CLINIC_ADDRESS = "CLINIC_ADDRESS";
    public static final String KEY_OPENING_HOURS = "OPENING_HOURS";
    public static final String KEY_SLOT_DURATION = "CLINIC_SLOT_DURATION";
    public static final String KEY_TIME_SLOTS = "CLINIC_TIME_SLOTS";
    public static final String KEY_SEPAY_BANK_NAME = "SEPAY_BANK_NAME";
    public static final String KEY_SEPAY_BANK_ACC = "SEPAY_BANK_ACC";
    public static final String KEY_SEPAY_ACCOUNT_HOLDER = "SEPAY_ACCOUNT_HOLDER";

    // Default Fallbacks
    public static final int DEFAULT_SLOT_DURATION_MINUTES = 60;
    public static final String[] DEFAULT_CLINIC_TIME_SLOTS = {
            "08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00"
    };
}
