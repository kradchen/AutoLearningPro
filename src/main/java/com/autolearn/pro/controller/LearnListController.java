package com.autolearn.pro.controller;

import com.autolearn.pro.BackgroundLearner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class LearnListController {
    @RequestMapping(value = "/learnlist/", method = RequestMethod.GET)
    public Collection<BackgroundLearner.LearnState> getLearnList()
    {
       return BackgroundLearner.getCurrentLearners();
    }
}
