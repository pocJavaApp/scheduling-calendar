package com.ukg.poc.schedulingcalendar.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@RequestMapping("/sched-calendar")
@RestController
public class SchedulingCalendarRest {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingCalendarRest.class);

    public static final String CALENDAR_NAME = "scheduling-calendar.ics";

    @GetMapping("")
    public String getMessages() {
        return "Sync Schedules to phone calendar";
    }

    @GetMapping("/schedules-calendar.ics")
    public ResponseEntity<Resource> feedCalendar(HttpServletRequest request) {
        printHttpRequestMetaData(request);

        Resource resource = new ClassPathResource(CALENDAR_NAME);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private void printHttpRequestMetaData(HttpServletRequest request) {
        String client = new StringBuilder()
                .append("RemoteAddr : ").append(request.getRemoteAddr()).append(",  ")
                .append("RemoteHost : ").append(request.getRemoteHost()).append(",  ")
                .append("RemotePort : ").append(request.getRemotePort()).append(",  ")
                .append("RemoteUser : ").append(request.getRemoteUser()).append(",  ")
                .append("UserPrincipal : ").append(request.getUserPrincipal())
                .toString();

        logger.info("###### Requested client: [{}] , Session ID : {} , URI :" + request.getRequestURL() + "", client, request.getSession().getId());

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String payload = br.lines().collect(Collectors.joining("\n"));
            if (!ObjectUtils.isEmpty(payload))
                logger.info("###### Request body: [{}]", payload);
        } catch (IOException e) {
            throw new RuntimeException("Unable to retrieve request payload", e);
        }
    }
}
