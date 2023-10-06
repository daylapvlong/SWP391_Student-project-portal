package swp.studentprojectportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.studentprojectportal.model.Class;
import swp.studentprojectportal.model.SubjectSetting;
import swp.studentprojectportal.model.User;
import swp.studentprojectportal.repository.IClassRepository;

import java.util.List;

public interface IClassService {
    List<User> getAllStudent(int classId);
    Class getClass(int classId);

    List<Class> findAllByClassManagerId(int classManagerId);
    List<Class> findAllBySubjectManagerId(int subjectManagerId);
    Class findById(int id);
    Class saveClass(Class classA);
}