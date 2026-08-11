package models;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

/**
 * Model DoctorSchedule - Ánh xạ bảng DoctorSchedules trong CSDL SQL Server.
 */
public class DoctorSchedule implements Serializable {

    private int id;
    private int doctorId;
    private Date workDate;
    private Time startTime;
    private Time endTime;
    private boolean isAvailable;

    // Joined Fields
    private String doctorName;
    private String specialty;

    public DoctorSchedule() {
    }

    public DoctorSchedule(int id, int doctorId, Date workDate, Time startTime, Time endTime, boolean isAvailable) {
        this.id = id;
        this.doctorId = doctorId;
        this.workDate = workDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = isAvailable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public boolean isIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
