package dao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import model.ClinicSetting;

public class ClinicSettingDAO extends BaseDAO<ClinicSetting> {

    private final RowMapper<ClinicSetting> mapper = rs -> new ClinicSetting(
            rs.getInt("id"),
            rs.getString("setting_key"),
            rs.getString("setting_value"),
            rs.getString("description"),
            rs.getTimestamp("updated_at")
    );

    public List<ClinicSetting> getAllSettings() {
        String sql = "SELECT id, setting_key, setting_value, description, updated_at FROM ClinicSettings ORDER BY id ASC";
        return queryList(sql, mapper);
    }

    public Map<String, String> getSettingsMap() {
        List<ClinicSetting> list = getAllSettings();
        Map<String, String> map = new HashMap<>();
        for (ClinicSetting setting : list) {
            map.put(setting.getSettingKey(), setting.getSettingValue());
        }
        return map;
    }

    public boolean updateSetting(String key, String value) {
        String sql = "UPDATE ClinicSettings SET setting_value = ?, updated_at = GETDATE() WHERE setting_key = ?";
        boolean updated = executeUpdate(sql, value, key);
        if (!updated) {
            String insertSql = "INSERT INTO ClinicSettings (setting_key, setting_value, description) VALUES (?, ?, N'Cấu hình động hệ thống')";
            return executeUpdate(insertSql, key, value);
        }
        return true;
    }
}
