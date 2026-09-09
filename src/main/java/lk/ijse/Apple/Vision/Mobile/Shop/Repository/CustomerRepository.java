package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Customer;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    List<Customer> findAllByCustomerStatus(CustomerStatus customerStatus);
}