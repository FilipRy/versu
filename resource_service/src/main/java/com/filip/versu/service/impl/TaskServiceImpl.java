package com.filip.versu.service.impl;

import com.filip.versu.repository.CommentRepository;
import com.filip.versu.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private CommentRepository commentRepository;

    private final static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Override
    @Scheduled(cron = "0 0 */1 * * *")//execute task each 1 hour...
    public void keepDBConnectionAlive() {//TODO find a better solution to keep conn with DB alive
        if(logger.isInfoEnabled()) {
            logger.info("Executing scheduled task to keep connection with DB alive.");
        }
        commentRepository.findOne(null);//this is a random query to keep the connection with DB "alive"
        commentRepository.setNamesToUtf8Mb4();//sometimes this needs to be re-executed probably, if app has a new connection with DB. ?? TODO
    }
}
