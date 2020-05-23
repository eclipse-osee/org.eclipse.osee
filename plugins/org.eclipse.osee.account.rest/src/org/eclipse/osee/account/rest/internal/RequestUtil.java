/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.account.rest.internal;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

/**
 * @author Roberto E. Escobar
 */
public final class RequestUtil {

   private static final String X_FORWARDED_FOR = "X-Forwarded-For";
   private static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
   private static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
   private static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
   private static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
   private static final String UNKNOWN = "unknown";

   private RequestUtil() {
      // Utility class
   }

   public static String getUserAgent(HttpServletRequest request) {
      return request != null ? request.getHeader(HttpHeaders.USER_AGENT) : "";
   }

   public static String getClientIpAddrress(HttpServletRequest request) {
      String ip = "";
      if (request != null) {
         ip = request.getHeader(X_FORWARDED_FOR);
         if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(PROXY_CLIENT_IP);
         }
         if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(WL_PROXY_CLIENT_IP);
         }
         if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_CLIENT_IP);
         }
         if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_X_FORWARDED_FOR);
         }
         if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
         }
      }
      return ip;
   }

}
