/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core.oauth;

import java.util.Map;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.URIException;
import org.scribe.model.ParameterList;
import org.scribe.utils.OAuthEncoder;

/**
 * Class to extract the Base String
 * 
 * @author Swapna R
 */
public class RequestBaseStringExtractor {

   private static final String AMPERSAND_SEPARATED_STRING = "%s&%s&%s";

   /**
    * @param request to get base string
    * @param oauth_parameters to get base string
    * @return base string
    */
   public String extract(final HttpMethodBase request, final Map<String, String> oauth_parameters) {
      String verb = OAuthEncoder.encode(request.getName());
      String url = OAuthEncoder.encode(getSanitizedUrl(request));
      String params = getSortedAndEncodedParams(request, oauth_parameters);
      return String.format(AMPERSAND_SEPARATED_STRING, verb, url, params);
   }

   private String getSortedAndEncodedParams(final HttpMethodBase request, final Map<String, String> oauth_parameters) {
      ParameterList params = new ParameterList();
      params.addQuerystring(request.getQueryString());
      params.addAll(new ParameterList(oauth_parameters));
      return params.sort().asOauthBaseString();
   }

   /**
    * Method to convert url to UTF-8 format
    * 
    * @param method Request to get the sanitized url
    * @return sanitized url
    */
   public String getSanitizedUrl(final HttpMethodBase method) {
      String url = "";
      try {
         url = method.getURI().getURI().replaceAll("\\?.*", "").replace("\\:\\d{4}", "");
      } catch (URIException e) {
         e.printStackTrace();
      }
      return url;
   }
}
