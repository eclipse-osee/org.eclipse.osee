/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.core.server.internal.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.core.util.result.XResultPageBase;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
@Provider
public class XResultDataHtmlWriter implements MessageBodyWriter<XResultData> {

   @Override
   public long getSize(XResultData data, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType) {
      return -1;
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      boolean assignableFrom = XResultData.class.isAssignableFrom(type);
      boolean match = assignableFrom && MediaType.TEXT_HTML.equals(mediaType.toString());
      return match;
   }

   @Override
   public void writeTo(XResultData resultData, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream outputStream)
      throws IOException, WebApplicationException {
      XResultPageBase xResultPage = new XResultPageBase(resultData.getTitle(), resultData.toString());
      String html = xResultPage.getManipulatedHtml(
         Arrays.asList(Manipulations.HTML_MANIPULATIONS, Manipulations.ERROR_RED, Manipulations.CONVERT_NEWLINES));
      outputStream.write(html.getBytes(Strings.UTF_8));
   }

}
