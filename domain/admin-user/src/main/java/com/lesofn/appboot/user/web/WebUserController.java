package com.lesofn.appboot.user.web;

import com.lesofn.appboot.infrastructure.auth.annotation.AuthType;
import com.lesofn.appboot.infrastructure.auth.annotation.BaseInfo;
import com.lesofn.appboot.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.annotation.Resource;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2015-10-18 00:09.
 */
@Controller
@RequestMapping("/web")
public class WebUserController {

    @Resource
    private UserService userService;

    @BaseInfo(needAuth = AuthType.OPTION)
    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String register() {
        return "account/register";
    }

    @BaseInfo(needAuth = AuthType.OPTION)
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login() {
        return "account/login";
    }


}
