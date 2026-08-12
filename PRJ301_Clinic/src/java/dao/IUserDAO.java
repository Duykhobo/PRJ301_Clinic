package dao;

import model.User;
import java.util.List;

/**
 * Interface IUserDAO định nghĩa các hợp đồng nghiệp vụ DAO cho Bảng Users.
 * Tuân thủ nguyên tắc Interface-based Design (SOLID - Dependency Inversion).
 */
public interface IUserDAO {

    User login(String username, String rawPassword);

    boolean register(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findById(int id);

    boolean updateStatus(int id, boolean status);

    List<User> findAll();
}
