package finall.service;

import finall.model.Student;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StudentService {
    List<Student> getAllStudents();

    void addStudent(Student student);

    void addStudents(List<Student> students);
    List<Student> searchByGpa(String gpa);

    List<Student> searchByFirstName(String firstName);
    void deleteStudent(String id);

     boolean isValidNameAndAddress(String name);
     boolean isValidGPA(String gpa);
    Student getStudentById(String id);
    void updateStudent(String studentId, Student updatedStudent);
}