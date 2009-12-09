/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public final class HttpUrlBuilder {

   private HttpUrlBuilder() {
   }

   private static String encode(String value) throws UnsupportedEncodingException {
      return URLEncoder.encode(value, "UTF-8");
   }

   public static String getParametersAsEncodedUrl(Map<String, String> keyValues) throws UnsupportedEncodingException {
      StringBuilder sb = new StringBuilder();
      for (String key : keyValues.keySet()) {
         sb.append(encode(key));
         sb.append("=");
         sb.append(encode(keyValues.get(key)));
         sb.append("&");
      }
      if (sb.length() - 1 >= 0) {
         // Delete the last unnecessary '&'
         sb.deleteCharAt(sb.length() - 1);
      }
      return sb.toString();
   }

   public static String createURL(String address, int port, String context, Map<String, String> parameters) throws UnsupportedEncodingException {
      StringBuilder sb = new StringBuilder();
      sb.append("http://");
      sb.append(address);
      sb.append(":");
      sb.append(port);
      sb.append("/");
      sb.append(context);
      sb.append("?");
      sb.append(getParametersAsEncodedUrl(parameters));
      return sb.toString();
   }

   public static String createURL(String prefix, String context, Map<String, String> parameters) throws UnsupportedEncodingException {
      StringBuilder sb = new StringBuilder();
      sb.append(prefix);
      sb.append(context);
      sb.append("?");
      sb.append(getParametersAsEncodedUrl(parameters));
      return sb.toString();
   }
}
