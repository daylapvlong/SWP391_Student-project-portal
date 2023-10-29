package swp.studentprojectportal.controller.subject_manager.subject;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import swp.studentprojectportal.model.Assignment;
import swp.studentprojectportal.model.Subject;
import swp.studentprojectportal.model.IssueSetting;
import swp.studentprojectportal.model.User;
import swp.studentprojectportal.service.servicesimpl.AssignmentService;
import swp.studentprojectportal.service.servicesimpl.IssueSettingService;
import swp.studentprojectportal.service.servicesimpl.SubjectService;
import swp.studentprojectportal.utils.Validate;

import java.util.List;
import java.util.Objects;

@Controller
public class SubjectHomeController {
    @Autowired
    SubjectService subjectService;
    @Autowired
    AssignmentService assignmentService;
    @Autowired
    IssueSettingService issueSettingService;

    @GetMapping("/subject-manager/subject")
    public String subjectPage(@RequestParam Integer subjectId,
                              Model model, HttpSession session) {
        Subject subject = subjectService.getSubjectById(subjectId);
        model.addAttribute("subject", subject);
        User user = (User) session.getAttribute("user");

        Assignment assignment = new Assignment();
        assignment.setId(-1);
        assignment.setDescription("");

        Page<IssueSetting> issueSettingList = issueSettingService.filter(subjectId, "", 0, 10, "id", 1, "", -1);
        Page<Assignment> assignmentList = assignmentService.filter(user.getId(), "", 0, 10, "id", 1, subjectId, -1);
        List<String> settingGroupList = issueSettingService.findAllDistinctSettingGroup(subjectId);
        model.addAttribute("pageSize", 10);
        model.addAttribute("pageNo", 0);
        model.addAttribute("sortBy", "id");
        model.addAttribute("sortType", 1);
        model.addAttribute("totalPage", assignmentList.getTotalPages());

        model.addAttribute("pageSizeS", 10);
        model.addAttribute("pageNoS", 0);
        model.addAttribute("sortByS", "id");
        model.addAttribute("sortTypeS", 1);
        model.addAttribute("settingGroupS", "");
        model.addAttribute("totalPageS", issueSettingList.getTotalPages());
        model.addAttribute("assignment", assignment);

        model.addAttribute("subjectList", subjectService.findAllSubjectByUserAndStatus(user, true));
        model.addAttribute("issueSettingList", issueSettingList);
        model.addAttribute("assignmentList", assignmentList);
        model.addAttribute("settingGroupList", settingGroupList);
        return "/subject_manager/subjectHome";
    }

    @PostMapping("/subject-manager/subject")
    public String subject(@RequestParam Integer pageNo, @RequestParam Integer pageSize,
                          @RequestParam String search,
                          @RequestParam Integer subjectId, @RequestParam Integer status,
                          @RequestParam String newTitle, @RequestParam(defaultValue = "") String description,
                          @RequestParam String sortBy, @RequestParam Integer sortType,
                          @RequestParam Integer pageNoS, @RequestParam Integer pageSizeS,
                          @RequestParam String searchS, @RequestParam Integer assignmentId,
                          @RequestParam Integer statusS, @RequestParam String settingGroupS,
                          @RequestParam String sortByS, @RequestParam Integer sortTypeS,
                          Model model, HttpSession session, WebRequest request) {
        String newStatus = request.getParameter("newStatus");
        Subject subject = subjectService.getSubjectById(subjectId);
        model.addAttribute("subject", subject);
        User user = (User) session.getAttribute("user");

        Assignment assignment = assignmentService.getAssignmentById(assignmentId);
        if (assignment==null) {
            assignment = new Assignment();
            assignment.setId(-1);
        }

        Page<Assignment> assignmentList = assignmentService.filter(user.getId(), search, pageNo, pageSize, sortBy, sortType, subjectId, status);
        String errorMsg = checkValidateAssignment(newTitle, description);
        if(errorMsg != null){
            model.addAttribute("errorMessage", errorMsg);
            model.addAttribute("newTitle", newTitle);
            model.addAttribute("description", description);
        } else {
            assignment.setTitle(newTitle);
            assignment.setDescription(description);
            if(assignmentId!=-1)  assignment.setStatus(newStatus!=null);
            assignment.setSubject(subject);
            if (assignmentService.checkExistedAssignment(newTitle, subjectId, assignmentId)) {
                model.addAttribute("errorMessage", "This assignemnt name has already existed in this subject!");
            } else {
                if (assignmentId == -1)
                    model.addAttribute("toastMessage", "Add new assignment successfully");
                else
                    model.addAttribute("toastMessage", "Update assignment details successfully");
                assignmentService.saveAssignment(assignment);
                assignmentList = assignmentService.filter(user.getId(), search, pageNo, pageSize, sortBy, sortType, subjectId, status);
                assignment = new Assignment();
                assignment.setId(-1);
            }
        }

        model.addAttribute("assignment", assignment);

        Page<IssueSetting> issueSettingList = issueSettingService.filter(subjectId, searchS, pageNoS, pageSizeS, sortByS, sortTypeS, settingGroupS, statusS);
        List<String> settingGroupList = issueSettingService.findAllDistinctSettingGroup(subjectId);

        model.addAttribute("pageSize", pageSize);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortType", sortType);
        model.addAttribute("totalPage", assignmentList.getTotalPages());

        model.addAttribute("pageSizeS", pageSizeS);
        model.addAttribute("pageNoS", pageNoS);
        model.addAttribute("searchS", searchS);
        model.addAttribute("statusS", statusS);
        model.addAttribute("sortByS", sortByS);
        model.addAttribute("settingGroupS", settingGroupS);
        model.addAttribute("sortTypeS", sortTypeS);
        model.addAttribute("totalPageS", issueSettingList.getTotalPages());

        model.addAttribute("subjectList", subjectService.findAllSubjectByUserAndStatus(user, true));
        model.addAttribute("issueSettingList", issueSettingList);
        model.addAttribute("assignmentList", assignmentList);
        model.addAttribute("settingGroupList", settingGroupList);
        return "/subject_manager/subjectHome";
    }

    @PostMapping("/subject-manager/subject/update")
    public String subject(@RequestParam("id") Integer id, @RequestParam String description,
                          Model model, HttpSession session, RedirectAttributes attributes) {
        Subject subject = subjectService.getSubjectById(id);
        subject.setDescription(description);
        subjectService.saveSubject(subject);
        attributes.addFlashAttribute("toastMessage", "Update subject description successfully");
        return "redirect:/subject-manager/subject?subjectId=" + id;
    }

    private String checkValidateAssignment(String title, String description) {
        if (title.isEmpty()) return "Please input subject assignment title";
        if (description.isEmpty()) return "Please input subject assignment description";
        if (!Validate.validTilteDescription(title, description)) return "Please dont input special characters";
        return null;
    }
}
