package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.User;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.UserRole;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);
    List<User> findAllByRole(UserRole role);
    List<User> findAllByUserStatus(UserStatus userStatus);
}