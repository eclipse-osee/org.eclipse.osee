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

import org.eclipse.osee.icteam.service.IcteamHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Ajay Chandrahasan
 */

@RestController
@RequestMapping("/file")
public class FileController {

  @Autowired
  IcteamHttpClient icTeamHttpClient;


  @Autowired
  private HttpServletRequest request;

  @Autowired
  private HttpServletResponse response;

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  public String uploadFile(@RequestParam("url") final String url, @RequestBody final MultipartFile file) {
    String resp = this.icTeamHttpClient.upload(url, file, this.request, this.response);
    return resp;
  }

  @RequestMapping(value = "/download", method = RequestMethod.POST)
  public void downloadFile(@RequestParam("url") final String url, @RequestBody final String data) {
    this.icTeamHttpClient.download(url, data, this.request, this.response);
  }

}
