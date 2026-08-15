package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import model.MedicalRecord;

/**
 * MedicalRecordDAO - Thao tác CSDL với bảng MedicalRecords.
 * Quản lý Hồ sơ khám bệnh, Chẩn đoán y khoa & Đơn thuốc.
 */
public class MedicalRecordDAO extends BaseDAO<MedicalRecord> {

    private static final Logger LOGGER = Logger.getLogger(MedicalRecordDAO.class.getName());

    protected MedicalRecord mapResultSetToRecord(ResultSet rs) throws SQLException {
        MedicalRecord record = new MedicalRecord();
        record.setId(rs.getInt("id"));
        record.setAppointmentId(rs.getInt("appointment_id"));
        record.setPatientId(rs.getInt("patient_id"));
        record.setDoctorId(rs.getInt("doctor_id"));
        record.setDiagnosis(rs.getString("diagnosis"));
        record.setPrescriptionOrResult(rs.getString("prescription_or_result"));
        record.setRating((Integer) rs.getObject("rating"));
        record.setReviewComment(rs.getString("review_comment"));
        record.setCreatedAt(rs.getTimestamp("created_at"));
        return record;
    }

    /**
     * Tìm hồ sơ bệnh án theo mã Cuộc hẹn.
     */
    public MedicalRecord getRecordByAppointmentId(int appointmentId) {
        String sql = "SELECT * FROM MedicalRecords WHERE appointment_id = ?";
        return queryOne(sql, this::mapResultSetToRecord, appointmentId);
    }

    /**
     * Thêm mới hoặc cập nhật Chẩn đoán & Đơn thuốc cho Cuộc hẹn.
     */
    public boolean saveOrUpdateRecord(MedicalRecord record) {
        MedicalRecord existing = getRecordByAppointmentId(record.getAppointmentId());

        if (existing == null) {
            String insertSql = "INSERT INTO MedicalRecords (appointment_id, patient_id, doctor_id, diagnosis, prescription_or_result) " +
                    "VALUES (?, ?, ?, ?, ?)";
            return executeUpdate(insertSql,
                    record.getAppointmentId(),
                    record.getPatientId(),
                    record.getDoctorId(),
                    record.getDiagnosis(),
                    record.getPrescriptionOrResult());
        } else {
            String updateSql = "UPDATE MedicalRecords SET diagnosis = ?, prescription_or_result = ? WHERE appointment_id = ?";
            return executeUpdate(updateSql,
                    record.getDiagnosis(),
                    record.getPrescriptionOrResult(),
                    record.getAppointmentId());
        }
    }
}
