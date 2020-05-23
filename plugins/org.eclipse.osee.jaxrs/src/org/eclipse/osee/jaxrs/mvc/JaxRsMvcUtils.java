/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs.mvc;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsMvcUtils {

   private JaxRsMvcUtils() {
      // Utility class
   }

   public static Charset UTF_8_ENCODING = newCharset("UTF-8");

   private static Charset newCharset(String value) {
      return Charset.forName(value);
   }

   public static Charset computeEncoding(MediaType mediaType, Map<String, List<Object>> httpHeaders) {
      return computeEncoding(mediaType, httpHeaders, null);
   }

   public static Charset computeEncoding(MediaType mediaType, Map<String, List<Object>> httpHeaders, Charset defaultEncoding) {
      String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
      Charset encoding = defaultEncoding != null ? defaultEncoding : UTF_8_ENCODING;
      MediaType contentType = mediaType;
      if (charset != null) {
         encoding = Charset.forName(charset);
      } else {
         HashMap<String, String> params = new HashMap<>(mediaType.getParameters());
         params.put(MediaType.CHARSET_PARAMETER, encoding.name());
         contentType = new MediaType(mediaType.getType(), mediaType.getSubtype(), params);
      }
      ArrayList<Object> typeList = new ArrayList<>(1);
      typeList.add(contentType.toString());
      httpHeaders.put(HttpHeaders.CONTENT_TYPE, typeList);
      return encoding;
   }
}
