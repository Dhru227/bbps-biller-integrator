package bharat.connect.biller.dao;

import bharat.connect.biller.model.User;
import java.util.List;

public interface UserDao {
    List<User> findAll();
    User findById(Long id);
    User save(User user);
    User update(User user);
    void delete(Long id);
}