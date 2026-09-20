package lk.ijse.Apple.Vision.Mobile.Shop.Scheduler;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.WarrantyStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.WarrantyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class WarrantyScheduler {

    private final WarrantyRepository warrantyRepository;

    public WarrantyScheduler(WarrantyRepository warrantyRepository) {
        this.warrantyRepository = warrantyRepository;
    }


    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void autoExpireWarranties() {
        log.info("Cron Triggered: Checking for outdated warranties to expire...");

        LocalDate today = LocalDate.now();
        int updatedCount = warrantyRepository.expireOutdatedWarranties(
                WarrantyStatus.ACTIVE,
                WarrantyStatus.EXPIRED,
                today
        );

        log.info("Cron Completed: {} warranty record(s) marked as EXPIRED.", updatedCount);
    }
}