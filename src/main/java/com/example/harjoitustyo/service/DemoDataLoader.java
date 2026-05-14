package com.example.harjoitustyo.service;

import com.example.harjoitustyo.model.AppUser;
import com.example.harjoitustyo.model.Customer;
import com.example.harjoitustyo.model.CustomerProfile;
import com.example.harjoitustyo.model.CustomerStatus;
import com.example.harjoitustyo.model.Project;
import com.example.harjoitustyo.model.ProjectPriority;
import com.example.harjoitustyo.model.ProjectStatus;
import com.example.harjoitustyo.model.Role;
import com.example.harjoitustyo.model.Technology;
import com.example.harjoitustyo.repository.AppUserRepository;
import com.example.harjoitustyo.repository.CustomerProfileRepository;
import com.example.harjoitustyo.repository.CustomerRepository;
import com.example.harjoitustyo.repository.ProjectRepository;
import com.example.harjoitustyo.repository.TechnologyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class DemoDataLoader implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CustomerProfileRepository profileRepository;
    private final ProjectRepository projectRepository;
    private final TechnologyRepository technologyRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataLoader(AppUserRepository userRepository,
                          CustomerRepository customerRepository,
                          CustomerProfileRepository profileRepository,
                          ProjectRepository projectRepository,
                          TechnologyRepository technologyRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.profileRepository = profileRepository;
        this.projectRepository = projectRepository;
        this.technologyRepository = technologyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        if (customerRepository.count() > 0) {
            return;
        }

        Technology vaadin = technology("Vaadin", "Frontend", 4, "UI Guild", "Server-side Java UI framework for rich business apps.");
        Technology spring = technology("Spring Boot", "Backend", 5, "Platform Team", "Opinionated backend stack for production Java services.");
        Technology postgres = technology("PostgreSQL", "Database", 5, "Data Team", "Relational database used in docker-compose deployment.");
        Technology quill = technology("Quill.js", "Editor", 3, "Content Team", "External JavaScript rich text editor embedded in Vaadin.");
        technologyRepository.saveAll(List.of(vaadin, spring, postgres, quill));

        Customer nordic = customer("Nordic Bikes", "ops@nordicbikes.example", "+358 40 111 2233", "Helsinki", CustomerStatus.ACTIVE);
        Customer aurora = customer("Aurora Foods", "it@aurorafoods.example", "+358 50 222 3344", "Tampere", CustomerStatus.LEAD);
        Customer lumen = customer("Lumen Care", "digital@lumencare.example", "+358 45 333 4455", "Turku", CustomerStatus.PAUSED);
        customerRepository.saveAll(List.of(nordic, aurora, lumen));

        profileRepository.save(profile(nordic, "Nordic Bikes Oy", "FI-104455", "Aino Koski", "https://nordicbikes.example",
                "Fleet customer with several webshop and logistics integrations.", LocalDate.of(2015, 4, 1)));
        profileRepository.save(profile(aurora, "Aurora Foods Oy", "FI-208811", "Mika Laine", "https://aurorafoods.example",
                "Food industry customer piloting a supplier reporting portal.", LocalDate.of(2018, 9, 15)));
        profileRepository.save(profile(lumen, "Lumen Care Oy", "FI-774411", "Sara Niemi", "https://lumencare.example",
                "Healthcare customer with strict audit and access control needs.", LocalDate.of(2012, 1, 20)));

        projectRepository.save(project("B2B ordering portal", nordic, ProjectStatus.ACTIVE, ProjectPriority.HIGH,
                BigDecimal.valueOf(42000), LocalDate.now().minusWeeks(3), LocalDate.now().plusMonths(2), vaadin, spring, postgres));
        projectRepository.save(project("Supplier analytics pilot", aurora, ProjectStatus.PLANNED, ProjectPriority.MEDIUM,
                BigDecimal.valueOf(18000), LocalDate.now().plusWeeks(1), LocalDate.now().plusMonths(4), spring, postgres));
        projectRepository.save(project("Care team notes editor", lumen, ProjectStatus.REVIEW, ProjectPriority.CRITICAL,
                BigDecimal.valueOf(36000), LocalDate.now().minusMonths(1), LocalDate.now().plusWeeks(2), vaadin, quill, spring));
    }

    private void seedUsers() {
        seedUser("admin", "admin@example.com", "Admin User", "admin123", Role.ADMIN);
        seedUser("super", "super@example.com", "Super User", "super123", Role.SUPER);
        seedUser("user", "user@example.com", "Regular User", "user123", Role.USER);
    }

    private void seedUser(String username, String email, String fullName, String password, Role role) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            return;
        }
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    private Technology technology(String name, String category, int maturity, String owner, String description) {
        Technology technology = new Technology();
        technology.setName(name);
        technology.setCategory(category);
        technology.setMaturityLevel(maturity);
        technology.setOwnerTeam(owner);
        technology.setDescription(description);
        technology.setIntroducedDate(LocalDate.now().minusYears(2));
        technology.setActive(true);
        return technology;
    }

    private Customer customer(String name, String email, String phone, String city, CustomerStatus status) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setCity(city);
        customer.setStatus(status);
        customer.setJoinedDate(LocalDate.now().minusMonths(8));
        return customer;
    }

    private CustomerProfile profile(Customer customer,
                                    String companyName,
                                    String vat,
                                    String contact,
                                    String website,
                                    String notes,
                                    LocalDate founded) {
        CustomerProfile profile = new CustomerProfile();
        profile.setCustomer(customer);
        profile.setCompanyName(companyName);
        profile.setVatNumber(vat);
        profile.setContactPerson(contact);
        profile.setWebsite(website);
        profile.setNotes(notes);
        profile.setFoundedDate(founded);
        return profile;
    }

    private Project project(String title,
                            Customer customer,
                            ProjectStatus status,
                            ProjectPriority priority,
                            BigDecimal budget,
                            LocalDate start,
                            LocalDate deadline,
                            Technology... technologies) {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription("Delivery project for " + customer.getName() + " covering planning, implementation and production rollout.");
        project.setCustomer(customer);
        project.setStatus(status);
        project.setPriority(priority);
        project.setBudget(budget);
        project.setStartDate(start);
        project.setDeadline(deadline);
        project.setTechnologies(new LinkedHashSet<>(List.of(technologies)));
        return project;
    }
}
