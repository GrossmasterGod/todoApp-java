package com.example.demo.schedule;


import com.example.demo.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTaskEmailSender {


    private final UserService userService;

    ScheduleTaskEmailSender(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendMailToOutOfDateTasks() {
        userService.checkIfTaskIsOutOfDate();
    }
}
