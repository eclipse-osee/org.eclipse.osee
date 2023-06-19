/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Roberto E. Escobar
 */
public final class HttpUrlBuilder {

   private HttpUrlBuilder() {
      // Utility Class
   }

   public static UrlQuery getParametersFromUrl(String url) throws UnsupportedEncodingException {
      UrlQuery query = createUrlQuery();
      return query.parse(url);
   }

   public static UrlQuery getParametersFromUrl(URL url) throws UnsupportedEncodingException {
      UrlQuery query = createUrlQuery();
      return query.parse(url);
   }

   public static UrlQuery createUrlQuery() {
      return new UrlQuery();
   }

   public static String getParametersAsEncodedUrl(Map<String, String> keyValues) throws UnsupportedEncodingException {
      UrlQuery query = createUrlQuery();
      for (Entry<String, String> entry : keyValues.entrySet()) {
         query.put(entry.getKey(), entry.getValue());
      }
      return query.toUrl();
   }

   public static String createURL(URI uri, String context, Map<String, String> parameters)
      throws UnsupportedEncodingException {
      StringBuilder sb = new StringBuilder();
      sb.append(uri.getScheme());
      sb.append("://");
      sb.append(uri.getHost());
      sb.append(":");
      sb.append(uri.getPort());
      sb.append("/");
      sb.append(context);
      if (parameters != null && !parameters.isEmpty()) {
         sb.append("?");
         sb.append(getParametersAsEncodedUrl(parameters));
      }
      return sb.toString();
   }

   public static String createURL(String prefix, String context, Map<String, String> parameters)
      throws UnsupportedEncodingException {
      StringBuilder sb = new StringBuilder();
      sb.append(prefix);
      sb.append(context);
      if (parameters != null && !parameters.isEmpty()) {
         sb.append("?");
         sb.append(getParametersAsEncodedUrl(parameters));
      }
      return sb.toString();
   }

}
