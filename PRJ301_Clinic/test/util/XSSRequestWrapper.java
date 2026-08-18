package util;

import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * XSSRequestWrapper - Lớp bọc HttpServletRequest tự động làm sạch tham số
 * phòng chống tấn công chèn mã độc Cross-Site Scripting (XSS Sanitizer).
 */
public class XSSRequestWrapper extends HttpServletRequestWrapper {

    private static final Pattern[] XSS_PATTERNS = new Pattern[]{
        Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("<iframe(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };

    public XSSRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = stripXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return stripXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return stripXSS(value);
    }

    private String stripXSS(String value) {
        if (value != null) {
            // Loại bỏ ký tự null
            value = value.replaceAll("\0", "");

            // Áp dụng Regex lọc mã độc XSS
            for (Pattern scriptPattern : XSS_PATTERNS) {
                value = scriptPattern.matcher(value).replaceAll("");
            }
        }
        return value;
    }
}
