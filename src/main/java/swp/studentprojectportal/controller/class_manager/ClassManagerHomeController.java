package swp.studentprojectportal.controller.class_manager;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import swp.studentprojectportal.model.Class;
import swp.studentprojectportal.model.Setting;
import swp.studentprojectportal.model.User;
import swp.studentprojectportal.service.servicesimpl.ClassService;
import swp.studentprojectportal.service.servicesimpl.SettingService;

import java.util.List;

@Controller
public class ClassManagerHomeController {
    @Autowired
    ClassService classService;
    @Autowired
    SettingService settingService;

    @GetMapping("/class-manager/home")
    public String ClassManagerHome(@RequestParam(defaultValue = "0") Integer pageNo,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(defaultValue = "") Integer semesterId,
                                    Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        Page<Class> classList = classService.findAllBySemester(pageNo, pageSize, semesterId, user.getId());
        List<Setting> semesterList = settingService.findSemesterByClassManagerId(user.getId());
        model.addAttribute("semesterList", semesterList);
        model.addAttribute("classList", classList);
        return "/class_manager/classManagerHome";
    }
}
