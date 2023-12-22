package finall.service;

import finall.model.Student;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private static final String XML_FILE_PATH = "src/main/xml/university.xml";

    @Override
    public List<Student> getAllStudents() {
        return readStudentsFromXml();
    }

    private void createXmlFile(File xmlFile) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("University");
            doc.appendChild(rootElement);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Student> readStudentsFromXml() {
        List<Student> students = new ArrayList<>();

        try {
            File xmlFile = new File(XML_FILE_PATH);
            if (!xmlFile.exists()) {
                createXmlFile(xmlFile);
            }
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(xmlFile);

            NodeList studentNodes = document.getElementsByTagName("Student");

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Element studentElement = (Element) studentNodes.item(i);
                Student student = new Student();
                student.setId(getTextContent(studentElement, "id"));
                student.setFirstName(getTextContent(studentElement, "firstName"));
                student.setLastName(getTextContent(studentElement, "lastName"));
                student.setGender(getTextContent(studentElement, "gender"));
                student.setGpa(getTextContent(studentElement, "gpa"));
                student.setLevel(getTextContent(studentElement, "level"));
                student.setAddress(getTextContent(studentElement, "address"));

                students.add(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return students;
    }

    private String getTextContent(Element parent, String childTagName) {
        NodeList nodeList = parent.getElementsByTagName(childTagName);
        return nodeList.item(0).getTextContent();
    }

    @Override
    public List<Student> searchByGpa(String gpa) {
        List<Student> allStudents = readStudentsFromXml();
        List<Student> searchResults = new ArrayList<>();

        for (Student student : allStudents) {
            if (student.getGpa().equals(gpa)) {
                searchResults.add(student);
            }
        }
        return searchResults;
    }

    @Override
    public List<Student> searchByFirstName(String firstName) {
        List<Student> allStudents = readStudentsFromXml();
        List<Student> searchResults = new ArrayList<>();

        for (Student student : allStudents) {
            if (student.getFirstName().equals(firstName)) {
                searchResults.add(student);
            }
        }

        return searchResults;
    }
    /*public void addStudent(Student student) {
        List<Student> students = readStudentsFromXml();
        students.add(student);
        saveStudentsToXml(students);
    }*/

    //ass2

     public boolean isValidNameAndAddress(String name) {
       return name != null && name.trim().matches("^[a-zA-Z]+$");
    }

    public boolean isValidGPA(String gpa){
        return Float.parseFloat(gpa) >= 0 && Float.parseFloat(gpa) <= 4;

    }
    public void addStudent(Student student) {
        List<Student> students = readStudentsFromXml();

        // Check if the student ID already exists
        boolean idExists = students.stream().anyMatch(s -> s.getId().equals(student.getId()));
        boolean validNames = isValidNameAndAddress(student.getFirstName()) && isValidNameAndAddress(student.getLastName());
        boolean validAddress = isValidNameAndAddress(student.getAddress());
        boolean validGPA= isValidGPA(student.getGpa());

        if (idExists) {
            // Throw a runtime exception for duplicate ID
            throw new RuntimeException("Error: Student with ID " + student.getId() + " already exists.");
        }

        if (!validNames) {
            // Throw a runtime exception for invalid first name
            throw new RuntimeException("Error: Invalid first name or last name. they should contain only alphabetical characters.");
        }

        if (!validAddress) {
            // Throw a runtime exception for invalid address
            throw new RuntimeException("Error: Invalid address. It should contain only alphabetical characters.");
        }
        if(!validGPA){
            throw new RuntimeException("Error: invalid GPA");
        }

        // If all conditions pass, add the student to the list
        students.add(student);
        saveStudentsToXml(students);
    }


    public void addStudents(List<Student> students) {
        List<Student> existingStudents = readStudentsFromXml();


        // Check for existing IDs
        for (Student newStudent : students) {
            boolean validNames = isValidNameAndAddress(newStudent.getFirstName()) && isValidNameAndAddress(newStudent.getLastName());
            boolean validAddress = isValidNameAndAddress(newStudent.getAddress());
            boolean validGPA= isValidGPA(newStudent.getGpa());
            for (Student existingStudent : existingStudents) {
                if (existingStudent.getId().equals(newStudent.getId())) {
                    // ID already exists, handle this case (throw exception, log, etc.)
                    // For simplicity, let's print an error message and return
                    throw new RuntimeException("Student with ID " + newStudent.getId() + " already exists.");
                }
                if (!validNames){
                    throw new RuntimeException("Invalid first name or last name. they should contain only alphabetical characters.");
                }
                if (!validAddress) {
                    // Throw a runtime exception for invalid address
                    throw new RuntimeException("Invalid address. It should contain only alphabetical characters.");
                }
                if(!validGPA){
                    throw new RuntimeException(" invalid GPA");
                }
            }
        }

        // Add new students
        existingStudents.addAll(students);
        saveStudentsToXml(existingStudents);
    }

    public Student getStudentById(String id) {
        List<Student> students = readStudentsFromXml();

        for (Student student : students) {
            if (student.getId().equals(id)) {
                return student;
            }
        }
        return null;
    }
    public void updateStudent(String studentId, Student updatedStudent) {
        List<Student> students = readStudentsFromXml();
        boolean validNames = isValidNameAndAddress(updatedStudent.getFirstName()) && isValidNameAndAddress(updatedStudent.getLastName());
        boolean validAddress = isValidNameAndAddress(updatedStudent.getAddress());
        boolean validGPA= isValidGPA(updatedStudent.getGpa());

        // Find the student with the given ID
        Optional<Student> existingStudentOptional = students.stream()
                .filter(s -> s.getId().equals(studentId))
                .findFirst();

        if (existingStudentOptional.isPresent()) {
            Student existingStudent = existingStudentOptional.get();

            if (updatedStudent.getFirstName() != null) {
                if (!validNames){
                    throw new RuntimeException("Invalid first name or last name. they should contain only alphabetical characters.");
                }
                existingStudent.setFirstName(updatedStudent.getFirstName());
            }

            if (updatedStudent.getLastName() != null) {
                if (!validNames){
                    throw new RuntimeException("Invalid first name or last name. they should contain only alphabetical characters.");
                }
                existingStudent.setLastName(updatedStudent.getLastName());
            }

            if (updatedStudent.getGender() != null) {
                existingStudent.setGender(updatedStudent.getGender());
            }


            if (updatedStudent.getGpa() != null) {
                if (!validGPA){
                    throw new RuntimeException(" Invalid GPA");
                }
                existingStudent.setGpa(updatedStudent.getGpa());
            }

            if (updatedStudent.getLevel() != null) {
                existingStudent.setLevel(updatedStudent.getLevel());
            }

            if (updatedStudent.getAddress() != null) {
                if (!validAddress) {
                    // Throw a runtime exception for invalid address
                    throw new RuntimeException("Invalid address. It should contain only alphabetical characters.");
                }
                existingStudent.setAddress(updatedStudent.getAddress());
            }

            // Save the updated list to XML
            saveStudentsToXml(students);
        } else {
            throw new RuntimeException("Student with ID " + studentId + " not found.");
        }
    }






    public void deleteStudent(String studentId) {
        //createXmlFile(); // Ensure the XML file exists
        List<Student> students = readStudentsFromXml();
        students.removeIf(student -> student.getId().equals(studentId));
        saveStudentsToXml(students);
    }
    private void saveStudentsToXml(List<Student> students) {
        try {
            File xmlFile = new File(XML_FILE_PATH);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);

            // Remove existing student elements
            NodeList existingStudents = doc.getElementsByTagName("Student");
            for (int i = existingStudents.getLength() - 1; i >= 0; i--) {
                Node studentNode = existingStudents.item(i);
                studentNode.getParentNode().removeChild(studentNode);
            }

            // Add new student elements
            for (Student student : students) {
                Element studentElement = doc.createElement("Student");
                createElementAndAppend(doc, studentElement, "id", student.getId());
                createElementAndAppend(doc, studentElement, "firstName", student.getFirstName());
                createElementAndAppend(doc, studentElement, "lastName", student.getLastName());
                createElementAndAppend(doc, studentElement, "gender", student.getGender());
                createElementAndAppend(doc, studentElement, "gpa", student.getGpa());
                createElementAndAppend(doc, studentElement, "level", student.getLevel());
                createElementAndAppend(doc, studentElement, "address", student.getAddress());
                doc.getDocumentElement().appendChild(studentElement);
            }


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createElementAndAppend(Document doc, Element parentElement, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.setTextContent(textContent);
        parentElement.appendChild(element);
    }
}