package swp.studentprojectportal.services;

import swp.studentprojectportal.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    User saveUser(User user);
    boolean checkEmailDomain(String email);
    boolean checkExistMail(String email);
    boolean checkExistPhoneNumber(String phoneNumber);
    User findUserByEmailAndPassword(String username, String password);
    User findUserByPhoneAndPassword(String username, String password);
    List<User> findAllUser();
    Optional<User> findUserById(int id);
    List<User> findAllUserByRoleId(int roleId);
    boolean updateUserStatus(int id, boolean status);
    boolean updateUser(int id, String fullName, String email, String phone, int roleId, boolean status, String note);

    boolean addUser(String fullName, String email, String phone, String password, int roleId);
}
