package swp.studentprojectportal.controller.subject_manager;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import swp.studentprojectportal.model.Subject;
import swp.studentprojectportal.model.SubjectSetting;
import swp.studentprojectportal.model.User;
import swp.studentprojectportal.repository.ISubjectRepository;
import swp.studentprojectportal.service.servicesimpl.SubjectSettingService;
import swp.studentprojectportal.service.servicesimpl.SubjectService;

import java.util.List;
@Controller
public class SubjectSettingController {
    @Autowired
    SubjectService subjectService;
    @Autowired
    SubjectSettingService subjectSettingService;
    @Autowired
    ISubjectRepository subjectRepository;

    @GetMapping("/subject-manager/subject-setting")
    public String searchPage(@RequestParam(defaultValue = "0") Integer pageNo,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(defaultValue = "") String search,
                             @RequestParam(defaultValue = "-1") Integer subjectId,
                             @RequestParam(defaultValue = "-1") Integer typeId,
                             @RequestParam(defaultValue = "-1") Integer status,
                             @RequestParam(defaultValue = "subject_id") String sortBy,
                             @RequestParam(defaultValue = "1") Integer sortType,Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Subject> subjectList = subjectService.findAllSubjectByUser(user);
        Page<SubjectSetting> subjectSettingList= subjectSettingService.filter(user.getId(), search, pageNo, pageSize, sortBy, sortType, subjectId, typeId, status);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("search", search);
        model.addAttribute("subjectId", subjectId);
        model.addAttribute("typeId", typeId);
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortType", sortType);
        model.addAttribute("totalPage", subjectSettingList.getTotalPages());
        model.addAttribute("subjectSettingList", subjectSettingList);
        model.addAttribute("subjectList",subjectList);
        return "subject_manager/subject_setting/subjectSettingList";
    }

    @GetMapping("/subject-manager/subject-setting/updateStatus")
    public String updateSubjectSettingStatus(
            @RequestParam int id,
            @RequestParam boolean status) {
        SubjectSetting subjectSetting = subjectSettingService.findById(id);
        subjectSetting.setStatus(status);
        subjectSettingService.saveSubjectSetting(subjectSetting);
        return "redirect:/";
    }
    @RequestMapping("/subject-manager/subject-setting/detail")
    public String detailSubjectSetting(@RequestParam("id") int id, Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        List<Subject> subjectList = subjectService.findAllSubjectByUser(user);
        SubjectSetting subjectSetting = subjectSettingService.findById(id);
        model.addAttribute("setting",subjectSetting);
        model.addAttribute("subjectList",subjectList);
        return "subjectSettingDetail";
    }

    @PostMapping("/subject-manager/subject-setting/update")
    public String updateSetting(
            @RequestParam Integer id,
            @RequestParam Integer subjectId,
            @RequestParam Integer typeId,
            @RequestParam String settingTitle,
            @RequestParam Integer displayOrder,
            WebRequest request,Model model,HttpSession session) {

//        if(settingTitle.trim().isEmpty()){
//            model.addAttribute("errmsg","Title not empty!");
//            User user = (User) session.getAttribute("user");
//            List<Subject> subjectList = subjectService.findAllSubjectByUser(user);
//            SubjectSetting subjectSetting = subjectSettingService.findById(id);
//            model.addAttribute("setting",subjectSetting);
//            model.addAttribute("subjectList",subjectList);
//            return "subjectSettingDetail";
//        }
        String status = request.getParameter("status");
        SubjectSetting subjectSetting = new SubjectSetting();
        subjectSetting.setId(id);
        subjectSetting.setSubject(subjectRepository.getById(subjectId));
        subjectSetting.setTypeId(typeId);
        subjectSetting.setSettingTitle(settingTitle);
        subjectSetting.setDisplayOrder(displayOrder);
        subjectSetting.setStatus(status!=null);
        subjectSettingService.saveSubjectSetting(subjectSetting);
        return "redirect:/subject-manager/subject-setting";
    }

    @RequestMapping(path = "/subject-manager/subject-setting/add")
    public String addSubjectSettingaPage(Model model,HttpSession session){
        User user = (User) session.getAttribute("user");
        List<Subject> subjectList = subjectService.findAllSubjectByUser(user);
        model.addAttribute("subjectList",subjectList);
        return  "subject_manager/subject_setting/subjectSettingAdd";
    }

    @PostMapping("/subject-manager/subject-setting/add")
    public String addSetting(
            @RequestParam Integer subjectId,
            @RequestParam Integer typeId,
            @RequestParam String settingTitle,
            @RequestParam Integer displayOrder,
            WebRequest request) {
        String status = request.getParameter("status");
        SubjectSetting subjectSetting = new SubjectSetting();
        subjectSetting.setSubject(subjectRepository.getById(subjectId));
        subjectSetting.setTypeId(typeId);
        subjectSetting.setSettingTitle(settingTitle);
        subjectSetting.setDisplayOrder(displayOrder);
        subjectSetting.setStatus(status!=null);
        subjectSettingService.saveSubjectSetting(subjectSetting);
        return "redirect:/subject-manager/subject-setting";
    }

}
