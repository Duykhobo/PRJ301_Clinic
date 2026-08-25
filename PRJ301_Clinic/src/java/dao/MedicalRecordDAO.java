package dao;

import java.util.logging.Logger;
import model.MedicalRecord;

/**
 * MedicalRecordDAO - Thao tác CSDL với bảng MedicalRecords.
 * Quản lý Hồ sơ khám bệnh, Chẩn đoán y khoa & Đơn thuốc.
 */
public class MedicalRecordDAO extends BaseDAO<MedicalRecord> {

    private static final Logger LOGGER = Logger.getLogger(MedicalRecordDAO.class.getName());

    // =========================================================================
    // 🧱 1. ROWMAPPER (TỰ ĐỘNG BẰNG REFLECTION CHUẨN DRY & SOLID)
    // =========================================================================
    private final RowMapper<MedicalRecord> mapper = autoMapper(MedicalRecord.class);

    /**
     * Tìm hồ sơ bệnh án theo mã Cuộc hẹn.
     */
    public MedicalRecord getRecordByAppointmentId(int appointmentId) {
        String sql = "SELECT * FROM MedicalRecords WHERE appointment_id = ?";
        return queryOne(sql, mapper, appointmentId);
    }

    /**
     * Thêm mới hoặc cập nhật Chẩn đoán, Đơn thuốc & Chỉ số da Spa cho Cuộc hẹn.
     */
    public boolean saveOrUpdateRecord(MedicalRecord record) {
        MedicalRecord existing = getRecordByAppointmentId(record.getAppointmentId());

        if (existing == null) {
            String insertSql = "INSERT INTO MedicalRecords (appointment_id, patient_id, doctor_id, diagnosis, prescription_or_result, skin_moisture_level, skin_sebum_level) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            return executeUpdate(insertSql,
                    record.getAppointmentId(),
                    record.getPatientId(),
                    record.getDoctorId(),
                    record.getDiagnosis(),
                    record.getPrescriptionOrResult(),
                    record.getSkinMoistureLevel(),
                    record.getSkinSebumLevel());
        } else {
            String updateSql = "UPDATE MedicalRecords SET diagnosis = ?, prescription_or_result = ?, skin_moisture_level = ?, skin_sebum_level = ? WHERE appointment_id = ?";
            return executeUpdate(updateSql,
                    record.getDiagnosis(),
                    record.getPrescriptionOrResult(),
                    record.getSkinMoistureLevel(),
                    record.getSkinSebumLevel(),
                    record.getAppointmentId());
        }
    }

    /**
     * Lưu đánh giá Rating & Nhận xét của Bệnh nhân sau khi khám.
     */
    public boolean saveReview(int appointmentId, int rating, String reviewComment) {
        String sql = "UPDATE MedicalRecords SET rating = ?, review_comment = ? WHERE appointment_id = ?";
        return executeUpdate(sql, rating, reviewComment, appointmentId);
    }
}
