package swp.studentprojectportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.studentprojectportal.model.StudentClass;
import swp.studentprojectportal.model.User;

import java.util.List;

@Repository
public interface IStudentClassRepository extends JpaRepository<StudentClass, Integer> {
//    List<User> findAllByAclass_Id(int classId);
}
