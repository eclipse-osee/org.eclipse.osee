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
package org.eclipse.osee.doors.connector.core.oauth;

import java.util.Map;
import org.apache.commons.httpclient.HttpMethodBase;
import org.scribe.extractors.HeaderExtractorImpl;
import org.scribe.utils.OAuthEncoder;

/**
 * Class to extract the request Header
 *
 * @author Swapna R
 */
public class RequestHeaderExtractor extends HeaderExtractorImpl {

   private static final String PARAM_SEPARATOR = ", ";

   private static final String PREAMBLE = "OAuth realm=\"DWA\", ";

   /**
    * @param request to extract the request header
    * @param oauth_parameters to extract the request header
    * @return request header
    */
   public String extract(final HttpMethodBase request, final Map<String, String> oauth_parameters) {
      StringBuffer header = new StringBuffer(oauth_parameters.size() * 20);
      header.append(PREAMBLE);
      for (String key : oauth_parameters.keySet()) {
         if (header.length() > PREAMBLE.length()) {
            header.append(PARAM_SEPARATOR);
         }
         header.append(String.format("%s=\"%s\"", key, OAuthEncoder.encode(oauth_parameters.get(key))));
      }
      return header.toString();
   }
}
