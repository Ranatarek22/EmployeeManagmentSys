package com.EmployeeManagmentSystem.Assigment3;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class EmployeeController {
    private static final String JSON_FILE_PATH = "Employee.json";
    private List<Employee> employees = new ArrayList<>();

    @PostConstruct
    public void init() throws IOException {
        // Load employees from JSON during application startup
        loadEmployeesFromJsonFile();
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/add")
    public String showAddForm() {
        return "add";
    }

    @PostMapping("/add")
    public String addEmployee(@ModelAttribute Employee employee, Model model) throws IOException {
        // Validate the entered data before saving it to the file
        if (validateEmployee(employee, model)) {
            employees.add(employee);
            saveEmployeesToJsonFile();
            return "redirect:/";
        } else {
            return "add"; // Return to the add form if validation fails
        }
    }

    private boolean validateEmployee(Employee employee, Model model) {
        // Validate that all attributes are not null or empty
        //To Do : arrayValidation
        if (isNullOrEmpty(employee.getEmployeeID()) || isNullOrEmpty(employee.getFirstName()) || isNullOrEmpty(employee.getLastName())
                || isNullOrEmpty(employee.getDesignation())
        ) {
            model.addAttribute("error", "All attributes must be provided.");
            return false;
        }

        // Validate that ID does not already exist (no duplicates)
        if (isDuplicateId(employee.getEmployeeID())) {
            model.addAttribute("error", "ID already exists. Choose a different ID.");
            return false;
        }

        // Validate that employee name (first name and last name) and address are characters (a-z) only
        if (!isValidCharacters(employee.getFirstName()) || !isValidCharacters(employee.getLastName()) || !isValidCharacters(employee.getDesignation())) {
            model.addAttribute("error", "employee name and address must contain only characters (a-z).");
            return false;
        }
//        if(!employee.getKnownLanguages().getClass().isArray()){
//            return false;
//        }

        // Validate GPA is in the range 0 to 4

        for (Language language : employee.getKnownLanguages()) {
            if (isNullOrEmpty(language.getLanguageName())) {
                model.addAttribute("error", "Language name must be provided.");
                return false;
            }
        }
        return true; // Validation passed
    }

    private boolean isNullOrEmpty(String value) {

        return value == null || value.trim().isEmpty();
    }

    private boolean isDuplicateId(String id) {
        return employees.stream().anyMatch(employee -> employee.getEmployeeID().equals(id));
    }

    private boolean isValidCharacters(String value) {
        return value.matches("[a-zA-Z]+");
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable String id, Model model) {

        Employee employeeToUpdate = employees.stream()
                .filter(employee -> employee.getEmployeeID().equals(id))
                .findFirst()
                .orElse(null);

        if (employeeToUpdate != null) {
            model.addAttribute("employee", employeeToUpdate);
            return "update";
        } else {
            return "redirect:/search";
        }
    }

    @PostMapping("/update/{id}")
    public String updateemployee(@PathVariable String id, @ModelAttribute Employee updatedemployee) throws IOException {

        Employee employeeToUpdate = employees.stream()
                .filter(employee -> employee.getEmployeeID().equals(id))
                .findFirst()
                .orElse(null);


        if (employeeToUpdate != null) {

            employeeToUpdate.setFirstName(updatedemployee.getFirstName());
            employeeToUpdate.setLastName(updatedemployee.getLastName());
            employeeToUpdate.setDesignation(updatedemployee.getDesignation());
//            employeeToUpdate.setKnownLanguages(updatedemployee.getKnownLanguages());
//            employeeToUpdate.setEmployeeID(updatedemployee.getEmployeeID());


            saveEmployeesToJsonFile();

            return "redirect:/search";

        } else {

            return "redirect:/search";
        }
    }


    @GetMapping("/search")
    public String showSearchForm() {
        return "search";
    }

    @PostMapping("/search")
    public String searchEmployees(@RequestParam(required = false) String id,
                                  @RequestParam(required = false) String designation,
                                  Model model) {
        List<Employee> searchResults = new ArrayList<>();

        for (Employee employee : employees) {
            if (id != null && employee.getEmployeeID().equalsIgnoreCase(id)) {
                searchResults.add(employee);
            } else if (designation != null && employee.getDesignation().equalsIgnoreCase(designation)) {
                searchResults.add(employee);
            }

        }

        model.addAttribute("results", searchResults);
        model.addAttribute("numberOfResults", searchResults.size());
        return "search";
    }


    @PostMapping("/delete/{id}")
    public String deleteemployee(@PathVariable String id) throws IOException {
        employees.removeIf(employee -> {
            return employee.getEmployeeID() != null && employee.getEmployeeID().equals(id);
        });
        saveEmployeesToJsonFile();
        return "redirect:/";
    }
    // Add this method to your controller
    @GetMapping("/sorted-employees")
    public String showSortedJavaExperts(Model model) throws IOException {
        // Clear the existing list before loading and sorting
        employees.clear();
        loadEmployeesFromJsonFile();

        // Filter employees who know Java with score > 50
        List<Employee> javaExperts = employees.stream()
                .filter(employee -> employee.getKnownLanguages().stream()
                        .anyMatch(language -> "Java".equalsIgnoreCase(language.getLanguageName()) && language.getScoreOutof100() > 50))
                .collect(Collectors.toList());

        // Sort the result in ascending order based on the score of Java language
        javaExperts.sort(Comparator.comparing(employee -> {
            // Find the score of Java language for each employee
            Optional<Language> javaLanguage = employee.getKnownLanguages().stream()
                    .filter(language -> "Java".equalsIgnoreCase(language.getLanguageName()))
                    .findFirst(); // java 50 java 80

            // If Java language is present, return its score; otherwise, return 0
            return javaLanguage.map(Language::getScoreOutof100).orElse(0);
        }));

        model.addAttribute("sortedEmployees", javaExperts);
        return "sorted-employees";
    }



    private void loadEmployeesFromJsonFile() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource(JSON_FILE_PATH);

        try (InputStream inputStream = resource.getInputStream()) {
            Employee[] loadedEmployees = objectMapper.readValue(inputStream, Employee[].class);
            employees.addAll(Arrays.asList(loadedEmployees));
        }
    }

    private void saveEmployeesToJsonFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource(JSON_FILE_PATH);

        try (OutputStream outputStream = Files.newOutputStream(resource.getFile().toPath())) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputStream, employees);
        }
    }

    @GetMapping("/update_b")
    public String updateemployee_b(@ModelAttribute Employee updatedemployee) throws IOException {

        Employee employeeToUpdate = employees.stream()
                .filter(employee -> employee.getEmployeeID().equals("2000"))
                .findFirst()
                .orElse(null);


        if (employeeToUpdate != null) {

            employeeToUpdate.setDesignation("Team Leader");


            saveEmployeesToJsonFile();

            return "redirect:/search";

        } else {

            return "redirect:/index";
        }
    }
}

