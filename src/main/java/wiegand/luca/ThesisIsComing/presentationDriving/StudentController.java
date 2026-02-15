package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import wiegand.luca.ThesisIsComing.ApplicationService.StudentService;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.StudentValidationException;
import wiegand.luca.ThesisIsComing.domainModel.Student;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.BestandenerKurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.presentationDriving.exception.StudentListIsEmpty;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping("/studierende")
    public String getAllStudents(Model model) {
        var students = service.listAll();
        if (students.isEmpty()) {
            throw new StudentListIsEmpty("Keine Studierenden gefunden");
        }
        model.addAttribute("students", students);
        return "student/list";
    }

    @GetMapping("/studierende/{id}")
    public String getStudentById(@PathVariable UUID id, Model model) {
        model.addAttribute("student", service.getById(id));
        return "student/detail";
    }

    @GetMapping("/studierende/neu")
    @PreAuthorize("hasAnyRole('ADMIN','STUDIERENDE')")
    public String showCreateForm(Model model) {
        model.addAttribute("studentForm", new StudentForm("", null, null, "", ""));
        populateFormModel(model, null, "Studierendenprofil anlegen", "Anlegen");
        return "student/form";
    }

    @PostMapping("/studierende")
    @PreAuthorize("hasAnyRole('ADMIN','STUDIERENDE')")
    public String createStudent(@Valid StudentForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            populateFormModel(model, null, "Studierendenprofil anlegen", "Anlegen");
            return "student/form";
        }
        try {
            Student created = service.createStudent(
                    form.name(),
                    form.email(),
                    form.matrikelnummer(),
                    parseBestandeneKurse(form.bestandeneKurse()),
                    parseInteressensgebiete(form.interessensgebiete())
            );
            return "redirect:/studierende/" + created.getId();
        } catch (StudentValidationException ex) {
            bindingResult.rejectValue("name", "student.name.invalid", ex.getMessage());
            populateFormModel(model, null, "Studierendenprofil anlegen", "Anlegen");
            return "student/form";
        }
    }

    @GetMapping("/studierende/{id}/bearbeiten")
    @PreAuthorize("hasAnyRole('ADMIN','STUDIERENDE')")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Student student = service.getById(id);
        String email = student.getKontaktinformationen() != null
                ? student.getKontaktinformationen().getEmail()
                : null;
        String matrikelnummer = student.getKontaktinformationen() != null
                ? student.getKontaktinformationen().getMatrikelnummer()
                : null;
        model.addAttribute("studentForm", new StudentForm(
                student.getName(),
                email,
                matrikelnummer,
                joinBestandeneKurse(student.getBestandeneKurse()),
                joinInteressensgebiete(student.getInteressensgebiete())
        ));
        populateFormModel(model, id, "Studierendenprofil bearbeiten", "Speichern");
        return "student/form";
    }

    @PostMapping("/studierende/{id}/bearbeiten")
    @PreAuthorize("hasAnyRole('ADMIN','STUDIERENDE')")
    public String updateStudent(@PathVariable UUID id,
                                @Valid StudentForm form,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            populateFormModel(model, id, "Studierendenprofil bearbeiten", "Speichern");
            return "student/form";
        }
        try {
            service.updateStudent(
                    id,
                    form.name(),
                    form.email(),
                    form.matrikelnummer(),
                    parseBestandeneKurse(form.bestandeneKurse()),
                    parseInteressensgebiete(form.interessensgebiete())
            );
            return "redirect:/studierende/" + id;
        } catch (StudentValidationException ex) {
            bindingResult.rejectValue("name", "student.name.invalid", ex.getMessage());
            populateFormModel(model, id, "Studierendenprofil bearbeiten", "Speichern");
            return "student/form";
        }
    }

    @PostMapping("/studierende/{id}/loeschen")
    @PreAuthorize("hasAnyRole('ADMIN','STUDIERENDE')")
    public String deleteStudent(@PathVariable UUID id) {
        service.deleteStudent(id);
        return "redirect:/studierende";
    }

    private static Set<BestandenerKurs> parseBestandeneKurse(String input) {
        Set<BestandenerKurs> result = new HashSet<>();
        for (String entry : splitEntries(input)) {
            result.add(new BestandenerKurs(entry, null, null));
        }
        return result;
    }

    private static Set<Fachgebiet> parseInteressensgebiete(String input) {
        Set<Fachgebiet> result = new HashSet<>();
        for (String entry : splitEntries(input)) {
            result.add(new Fachgebiet(entry));
        }
        return result;
    }

    private static Set<String> splitEntries(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Set.of();
        }
        Set<String> result = new HashSet<>();
        for (String raw : input.split("[\n\r,]+")) {
            String trimmed = raw.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    private static String joinBestandeneKurse(Set<BestandenerKurs> kurse) {
        if (kurse == null || kurse.isEmpty()) {
            return "";
        }
        return kurse.stream()
                .map(BestandenerKurs::getKursbezeichnung)
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining("\n"));
    }

    private static String joinInteressensgebiete(Set<Fachgebiet> interessen) {
        if (interessen == null || interessen.isEmpty()) {
            return "";
        }
        return interessen.stream()
                .map(Fachgebiet::getBezeichnung)
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining("\n"));
    }

    private void populateFormModel(Model model, UUID id, String pageTitle, String submitLabel) {
        String formAction = id == null ? "/studierende" : "/studierende/" + id + "/bearbeiten";
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("submitLabel", submitLabel);
    }
}
