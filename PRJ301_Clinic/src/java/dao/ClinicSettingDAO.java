package dao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import model.ClinicSetting;

public class ClinicSettingDAO extends BaseDAO<ClinicSetting> {

    private final RowMapper<ClinicSetting> mapper = autoMapper(ClinicSetting.class);

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
        String checkSql = "SELECT COUNT(*) FROM ClinicSettings WHERE setting_key = ?";
        if (queryCount(checkSql, key) > 0) {
            String updateSql = "UPDATE ClinicSettings SET setting_value = ?, updated_at = GETDATE() WHERE setting_key = ?";
            return executeUpdate(updateSql, value, key);
        } else {
            String insertSql = "INSERT INTO ClinicSettings (setting_key, setting_value, description, updated_at) VALUES (?, ?, N'Cấu hình động hệ thống', GETDATE())";
            return executeUpdate(insertSql, key, value);
        }
    }
}
