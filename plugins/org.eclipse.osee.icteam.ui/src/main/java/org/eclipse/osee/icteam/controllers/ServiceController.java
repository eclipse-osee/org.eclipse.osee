/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.icteam.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.osee.icteam.service.IcteamHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Ajay Chandrahasan
 */

@RestController
@RequestMapping("/service")
public class ServiceController {

  Logger log = Logger.getLogger(getClass());

  @Autowired
  private HttpServletRequest request;

  @Autowired
  private HttpServletResponse response;

  @Autowired
  IcteamHttpClient icTeamHttpClient;

  @GetMapping
  public String getRequest(@RequestParam("url") final String url) {
    System.out.println("URl: " + url);
    return this.icTeamHttpClient.httpGet(url, "application/json");
  }

  @PutMapping
  public String putRequest(@RequestParam("url") final String url, @RequestBody final String data) {

    return this.icTeamHttpClient.httpPut(url, data, "application/json");

  }

  @DeleteMapping
  public String deleteRequest(@RequestParam("url") final String url, @RequestBody final String data) {

    return this.icTeamHttpClient.httpDelete(url, data, "application/json");

  }

  @PostMapping
  public String postRequest(@RequestParam("url") final String url, @RequestBody final String data) {
    System.out.println("In post method");
    return this.icTeamHttpClient.httpPost(url, data, "application/json");
  }

}
