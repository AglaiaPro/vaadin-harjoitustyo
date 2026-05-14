package com.example.harjoitustyo.service;

import com.example.harjoitustyo.model.Customer;
import com.example.harjoitustyo.model.Project;
import com.example.harjoitustyo.model.ProjectPriority;
import com.example.harjoitustyo.model.ProjectStatus;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
public class CsvService {

    private final ProjectService projectService;
    private final CustomerService customerService;

    public CsvService(ProjectService projectService, CustomerService customerService) {
        this.projectService = projectService;
        this.customerService = customerService;
    }

    public String exportProjects() throws IOException {
        StringWriter writer = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                .setHeader("title", "description", "budget", "startDate", "deadline", "status", "priority", "customerEmail")
                .build())) {
            for (Project project : projectService.findAll()) {
                printer.printRecord(
                        project.getTitle(),
                        project.getDescription(),
                        project.getBudget(),
                        project.getStartDate(),
                        project.getDeadline(),
                        project.getStatus(),
                        project.getPriority(),
                        project.getCustomer() == null ? "" : project.getCustomer().getEmail()
                );
            }
        }
        return writer.toString();
    }

    public int importProjects(InputStream inputStream) throws IOException {
        List<Customer> customers = customerService.findAll();
        int count = 0;
        try (CSVParser parser = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build()
                .parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            for (CSVRecord record : parser) {
                Customer customer = customers.stream()
                        .filter(candidate -> candidate.getEmail().equalsIgnoreCase(record.get("customerEmail")))
                        .findFirst()
                        .orElse(null);
                if (customer == null) {
                    continue;
                }
                Project project = new Project();
                project.setTitle(record.get("title"));
                project.setDescription(record.get("description"));
                project.setBudget(new BigDecimal(record.get("budget")));
                project.setStartDate(LocalDate.parse(record.get("startDate")));
                project.setDeadline(LocalDate.parse(record.get("deadline")));
                project.setStatus(ProjectStatus.valueOf(record.get("status")));
                project.setPriority(ProjectPriority.valueOf(record.get("priority")));
                project.setCustomer(customer);
                projectService.save(project);
                count++;
            }
        }
        return count;
    }
}
