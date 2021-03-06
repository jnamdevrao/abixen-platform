/**
 * Copyright (c) 2010-present Abixen Systems. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.abixen.platform.client.web.controller;

import com.abixen.platform.client.web.hystrix.ModuleTypeHystrixClient;
import com.abixen.platform.client.web.model.ModuleType;
import com.abixen.platform.client.web.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("")
public class ApplicationViewController extends BaseController {

    @Autowired
    ModuleTypeHystrixClient moduleTypeHystrixClient;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView renderApplicationPage() {
        log.debug("renderApplicationPage()");

        final List<ModuleType> moduleTypes = moduleTypeHystrixClient.getAllModuleTypes();
        final Set<Resource> resources = new HashSet<>();

        moduleTypes.forEach(moduleType -> {
            resources.addAll(moduleType.getResources());
        });

        resources.forEach(resource -> log.debug("resource: {}", resource));
        List<Resource> uniqueResources = resources.stream().filter(distinctByKey(resource -> resource.getRelativeUrl())).collect(Collectors.toList());


        List<String> angularJsModules = moduleTypes.stream().filter(moduleType -> moduleType.getAngularJsNameApplication() != null).filter(distinctByKey(moduleType -> moduleType.getAngularJsNameApplication())).map(moduleType -> moduleType.getAngularJsNameApplication()).collect(Collectors.toList());
        angularJsModules.forEach(angularJsModule -> log.debug(angularJsModule));

        ModelAndView modelAndView = new ModelAndView("application/index");
        modelAndView.addObject("resources", uniqueResources);
        modelAndView.addObject("angularJsModules", angularJsModules);

        return modelAndView;
    }
}