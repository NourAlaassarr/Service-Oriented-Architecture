package finall.controller;

import finall.model.Student;
import finall.model.StudentsForm;
import finall.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class indexController {
    private final StudentService studentService;
    public indexController(StudentService studentService) {
        this.studentService = studentService;
    }
    //List all students in the Xml
    @GetMapping("/list")
    public String getAllStudents(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "listSTD";
    }

    @GetMapping("/add")
    public String showAddStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "index";
    }

    //ass2
    @PostMapping("/add")
    public String addStudent(@ModelAttribute Student student,Model model) {
        try{
            studentService.addStudent(student);
            return "redirect:/list";
        }catch (RuntimeException  e) {
            // Catch the custom exception and add an error message to the model
            model.addAttribute("errorMessage", e.getMessage());
            return "index";
        }

    }



    @GetMapping("/search-by-gpa")
    public String searchByGpa(@RequestParam(name = "gpa", required = false) String gpa, Model model) {
        List<Student> searchResults;
        if (gpa != null && !gpa.isEmpty()) {
            searchResults = studentService.searchByGpa(gpa);
        } else {
            searchResults = studentService.searchByGpa("");
        }
        model.addAttribute("students", searchResults);
        return "searchByGpa";
    }

    @GetMapping("/search-by-firstname")
    public String searchByFirstName(@RequestParam(name = "firstName", required = false) String firstName, Model model) {
        List<Student> searchResults;
        if (firstName != null && !firstName.isEmpty()) {
            searchResults = studentService.searchByFirstName(firstName);
        } else {
            searchResults = studentService.searchByFirstName("");
        }
        model.addAttribute("students", searchResults);
        return "searchByFirstName";
    }

    @GetMapping("/add-students-form")
    public String addStudentsForm(Model model) {
        model.addAttribute("student", new Student());
        return "addStudents";
    }

    @PostMapping("/add-students")
    public String addStudents(@ModelAttribute Student student, Model model) {
        if (student.getNumStudents() > 0) {
            model.addAttribute("numStudents", student.getNumStudents());
            return "addStudents";
        } else {
            return "redirect:/list";
        }
    }


    @PostMapping("/save-students")
    public String saveStudents(@ModelAttribute("studentsForm") StudentsForm studentsForm, Model model) {
        List<Student> students = studentsForm.getStudents();

        try {
            studentService.addStudents(students);
            return "redirect:/list";
        } catch (RuntimeException e) {
            // Handle the case where duplicate IDs are detected
            model.addAttribute("errorMessage", e.getMessage());
            return "error"; // Create an "error.html" template to display the error message
        }

    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable String id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        return "updateForm";
    }

    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable String id, @ModelAttribute Student updatedStudent, Model model) {
        try {
            studentService.updateStudent(id, updatedStudent);
            return "redirect:/list";
        } catch (RuntimeException e) {
            // Catch the custom exception and add an error message to the model
            model.addAttribute("errorMessage", e.getMessage());
            return "updateForm";
        }
    }


    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return "redirect:/list";
    }
}
