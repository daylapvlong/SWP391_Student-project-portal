package swp.studentprojectportal.services.servicesimpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp.studentprojectportal.model.User;
import swp.studentprojectportal.repository.ISettingRepository;
import swp.studentprojectportal.repository.IUserRepository;
import swp.studentprojectportal.services.IUserService;
import swp.studentprojectportal.utils.GooglePojo;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {
    @Autowired
    IUserRepository userRepository;
    @Autowired
    ISettingRepository settingRepository;

    @Override
    public User saveUser(User user) {
        // TO-DO: set enable here
        return userRepository.save(user);
    }

    @Override
    public boolean checkEmailDomain(String email) {
        if(email == null) {
            return false;
        }
        String[] temp = email.split("@");
        if(temp.length == 0) return false;
        email = temp[temp.length-1];
        if (settingRepository.findSettingByTypeIdAndSettingTitle(2, email) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkExistMail(String email) {
        if(userRepository.findUserByEmail(email) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkExistPhoneNumber(String phoneNumber) {
        if(userRepository.findUserByPhone(phoneNumber) != null) {
            return true;
        }
        return false;
    }
    @Override
    public List<User> getUser(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> userPage = userRepository.findAll(pageable);
        List<User> users = userPage.getContent();
        return users;
    }
    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAllUserByRoleId(int roleId) {
        return userRepository.findAllBySetting(settingRepository.findById(roleId).get());
    }

    @Override
    public boolean updateUserStatus(int id, boolean status) {
        Optional<User> user = userRepository.findById(id);

        if(!user.isPresent()) return false;

        user.get().setStatus(status);
        userRepository.save(user.get());
        return true;
    }

    @Override
    public boolean updateUser(int id, String fullName, String email, String phone, int roleId, boolean status, String note) {
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()) return false;

        User userData = user.get();
        userData.setFullName(fullName);
        userData.setEmail(email);
        userData.setPhone(phone);
        userData.setSetting(settingRepository.findById(roleId).get());
        userData.setStatus(status);
        userData.setNote(note);

        userRepository.save(userData);
        return true;
    }

    @Override
    public User addUser(String fullName, String email, String phone, String password, int roleId) {
        User user = new User();

        user.setActive(true);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);
        user.setSetting(settingRepository.findById(roleId).get());

        userRepository.save(user);

        return user;
    }

    @Override
    public User findUserByEmailAndPassword(String username, String password) {
        return userRepository.findUserByEmailAndPassword(username, password);
    }

    @Override
    public User findUserByPhoneAndPassword(String username, String password) {
        return userRepository.findUserByPhoneAndPassword(username, password);
    }

    @Override
    public User findUserByUsernameAndPassword(String username, String password) {
        User user;
        if (username.contains("@"))
            user = userRepository.findUserByEmailAndPassword(username, password);
        else
            user = userRepository.findUserByPhoneAndPassword(username, password);
        return user;
    }

    public User registerAccountFromGoogle(GooglePojo googlePojo) {
        String[] temp = googlePojo.getEmail().split("@");
        String fullName = temp[0];
        //save to account
        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(googlePojo.getEmail());
        newUser.setPassword(googlePojo.getId());
        newUser.setAvatarUrl(googlePojo.getPicture());
        newUser.setActive(true);
        newUser.setSetting(settingRepository.findById(1).get());
        User user = userRepository.save(newUser);
        return user;
    }
    public void setCookie(Cookie cu, Cookie cp, Cookie cr, String remember) {
        if(remember!=null){
            cu.setMaxAge(60*60*24*7);
            cp.setMaxAge(60*60*24*7);
            cr.setMaxAge(60*60*24*7);
        } else {
            cu.setMaxAge(0);
            cp.setMaxAge(0);
            cr.setMaxAge(0);
        }
    }
    @Override
    // get totalPage
    public int getTotalPage(int pageSize) {
        long count = userRepository.count();
        int totalPage = count % pageSize == 0 ? (int) (count / pageSize) : (int) (count / pageSize) + 1;
        return totalPage;
    }
}
