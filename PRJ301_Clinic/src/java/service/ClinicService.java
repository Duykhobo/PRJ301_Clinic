package service;

import java.sql.Date;
import java.util.List;
import dao.DoctorProfileDAO;
import dao.DoctorScheduleDAO;
import dao.ServiceDAO;
import model.DoctorProfile;
import model.DoctorSchedule;
import model.Service;

/**
 * ClinicService - Tầng Service Quản lý Dịch vụ, Bác sĩ & Lịch làm việc (Business Logic).
 */
public class ClinicService {

    private final ServiceDAO serviceDAO;
    private final DoctorProfileDAO doctorProfileDAO;
    private final DoctorScheduleDAO doctorScheduleDAO;

    public ClinicService() {
        this.serviceDAO = new ServiceDAO();
        this.doctorProfileDAO = new DoctorProfileDAO();
        this.doctorScheduleDAO = new DoctorScheduleDAO();
    }

    public List<Service> getActiveServices() {
        return serviceDAO.findAllActive();
    }

    public List<DoctorProfile> getAllDoctors() {
        return doctorProfileDAO.findAllActiveDoctors();
    }

    public List<DoctorSchedule> getAvailableSlots(int doctorProfileId, Date date) {
        return doctorScheduleDAO.findAvailableSlotsByDoctorAndDate(doctorProfileId, date);
    }

    public List<DoctorSchedule> getSchedules(int doctorProfileId, Date date) {
        return doctorScheduleDAO.findSchedulesByDoctorAndDate(doctorProfileId, date);
    }
}
