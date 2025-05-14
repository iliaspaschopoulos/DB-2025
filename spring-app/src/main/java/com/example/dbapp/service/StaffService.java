package com.example.dbapp.service;

import com.example.dbapp.model.Staff;
import com.example.dbapp.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    @Autowired
    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Optional<Staff> getStaffById(Integer id) {
        return staffRepository.findById(id);
    }

    public Staff saveStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public Staff updateStaff(Integer id, Staff staffDetails) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id " + id));

        staff.setName(staffDetails.getName()); // Corrected
        staff.setAge(staffDetails.getAge()); // Corrected
        staff.setRole(staffDetails.getRole());
        staff.setExperienceLevel(staffDetails.getExperienceLevel()); // Corrected
        // staff.setEventStaff(staffDetails.getEventStaff()); // Managed separately
        return staffRepository.save(staff);
    }

    public void deleteStaff(Integer id) {
        staffRepository.deleteById(id);
    }
}
