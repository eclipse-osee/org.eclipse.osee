/*********************************************************************
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India
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
package org.eclipse.osee.doors.connector.core;

import java.util.logging.Level;
import org.eclipse.osee.doors.connector.core.oauth.DWAOAuthService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.scribe.model.Token;

/**
 * Handler class to authenticate and get the Doors response
 *
 * @author Chandan Bandemutt
 */
public class DoorsOSLCConnector {

   private static Token accessToken;
   /**
   *
   */
   public static DWAOAuthService service1;

   /**
    * Method to authenticate
    *
    * @param service : ICTeamOAuthService
    * @param name : username
    * @param password : password
    * @return access Token
    */
   public DoorsArtifact getAuthentication(final DWAOAuthService service, final String name, final String password) {
      DoorsOSLCConnector.service1 = service;
      DoorsArtifact parse = null;
      Token requestToken1 = DoorsOSLCConnector.service1.getRequestToken();
      String authorizeURL = DoorsOSLCConnector.service1.getAuthorizeURL(requestToken1);
      DoorsOSLCConnector.service1.doAuthentication(authorizeURL, requestToken1, name, password);
      accessToken = DoorsOSLCConnector.service1.getAccessToken(requestToken1);
      if (accessToken != null) {
         ServiceProviderCatalogReader catalogReader = new ServiceProviderCatalogReader();
         try {
            ServiceProviderCatalog child = new ServiceProviderCatalog();
            child.setName("Top Level");
            child.setPath(service1.getResourceUrl());
            parse = catalogReader.parse(child);
         } catch (Exception e) {
            e.printStackTrace();
         }
      } else {
         OseeLog.log(DWAOAuthService.class, Level.WARNING, "Failed to obtain valid authentication response");
      }
      return parse;
   }

   /**
    * @param path : OSLC URL to get the response
    * @param queryString : url to get the response
    * @return : returns the response
    */
   public String getCatalogResponse(final String path, final String queryString) {
      String response = null;
      if (DoorsOSLCConnector.accessToken != null) {
         response = DoorsOSLCConnector.service1.getResponse(DoorsOSLCConnector.accessToken, path, queryString);
      }
      return response;
   }
}
