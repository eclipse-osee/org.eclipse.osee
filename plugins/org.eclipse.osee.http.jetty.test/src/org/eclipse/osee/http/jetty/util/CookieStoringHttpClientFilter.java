/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.http.jetty.util;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.Provider;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class CookieStoringHttpClientFilter implements ClientResponseFilter, ClientRequestFilter {

   private final Map<String, Cookie> cookies = new HashMap<String, Cookie>(3);

   @Override
   public void filter(ClientRequestContext context) {
      setCookies(HttpHeaders.COOKIE, cookies, context.getHeaders());
   }

   @Override
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
      Map<String, NewCookie> newCookies = responseContext.getCookies();
      if (newCookies != null && !newCookies.isEmpty()) {
         cookies.putAll(newCookies);
      }
      setCookies(HttpHeaders.SET_COOKIE, cookies, responseContext.getHeaders());
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   private void setCookies(String header, Map<String, Cookie> cookies, MultivaluedMap headers) {
      if (cookies != null && !cookies.isEmpty()) {
         for (Cookie cookie : cookies.values()) {
            headers.add(header, cookie.toString());
         }
      }
   }
}