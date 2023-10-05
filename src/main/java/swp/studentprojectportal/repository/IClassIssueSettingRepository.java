package swp.studentprojectportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.studentprojectportal.model.ClassIssueSetting;
@Repository
public interface IClassIssueSettingRepository extends JpaRepository<ClassIssueSetting, Integer> {
}
