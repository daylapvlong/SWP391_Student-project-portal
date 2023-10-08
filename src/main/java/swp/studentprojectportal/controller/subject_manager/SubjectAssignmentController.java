package swp.studentprojectportal.controller.subject_manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import swp.studentprojectportal.model.Assignment;
import swp.studentprojectportal.service.IAssignmentService;
import swp.studentprojectportal.service.ISubjectService;


import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class SubjectAssignmentController {
    @Autowired
    ISubjectService subjectService;
    @Autowired
    IAssignmentService assignmentService;

    List<Assignment> assignmentList = new CopyOnWriteArrayList<>();
    @GetMapping("subject-manager/subject-assignment")
    public String AssignmentPage(Model model) {
        assignmentList = assignmentService.findAllAssignment(0,10);
        model.addAttribute("assignmentList", assignmentList);
        return "subject_manager/subject_assignment/subjectAssignmentList";
    }

    @GetMapping("/subject-manager/subject-assignment/updateStatus")
    public String updateAssignmentStatus(
            @RequestParam int id,
            @RequestParam boolean status) {
        Assignment assignment = assignmentService.getAssignmentById(id);
        assignment.setStatus(status);
        assignmentService.saveAssignment(assignment);
        return "redirect:/";
    }

    @GetMapping("/subject-manager/addSubjectAssignment")
    public String AddSubjectAssignment(Model model){
        model.addAttribute("assignment", new Assignment());
        model.addAttribute("subject", subjectService.getAllSubjects());
        return "subject_manager/subject_assignment/subjectAssignmentAdd";
    }

    @PostMapping("/subject-manager/addAssignment")
    public String createAssignment(Model model, WebRequest request){
        String title = Objects.requireNonNull(request.getParameter("title")).trim();
        int subjectId = Integer.parseInt(Objects.requireNonNull(request.getParameter("subject")).trim());
        String description = Objects.requireNonNull(request.getParameter("description")).trim();

        String errorMessage = checkValidateAssignment(title, description);
        if(errorMessage != null) {
            model.addAttribute("errorMsg", errorMessage);
            model.addAttribute("title", title);
            model.addAttribute("description", description);
            model.addAttribute("subject", subjectService.getAllSubjects());
            model.addAttribute("subject", subjectId);
            return "subject_manager/subject_assignment/subjectAssignmentAdd";
        }

        int newAssignmentId = assignmentService.addAssignment(title, description, subjectId, true).getId();
        return "redirect:/subject-manage/subjectAssignmentDetails?id=" + newAssignmentId;
    }

    @GetMapping("/subject-manager/subjectAssignmentDetail")
    public String subjectAssignmentDetail(Model model, @RequestParam("id") Integer id){
        Assignment assignment = assignmentService.getAssignmentById(id);
        model.addAttribute("assignment", assignment);
        model.addAttribute("subject", subjectService.getAllSubjects());
        return "subject_manager/subject_assignment/subjectAssignmentDetails";
    }

    private String checkValidateAssignment(String title, String description) {
        if(title.isEmpty()) return "Please input subject assignment title";
        if(description.isEmpty()) return "Please input subject assignment description";
//        if(assignmentService.checkTitleExisted(title)) return "Subject assignment title already exist";
//        if(assignmentService.checkDescriptionMatch(description)) return "Subject assignment description already exist";
        return null;
    }

}
