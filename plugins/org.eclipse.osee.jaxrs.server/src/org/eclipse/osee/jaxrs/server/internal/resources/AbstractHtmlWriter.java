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

package org.eclipse.osee.jaxrs.server.internal.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.jaxrs.server.internal.JaxRsUtils;

/**
 * @author Roberto E. Escobar
 */
@Provider
public abstract class AbstractHtmlWriter<T> extends AbstractMessageBodyWriter<T> {

   @Context
   private Providers providers;

   @Override
   public Collection<MediaType> getSupportedMediaTypes() {
      return JaxRsUtils.HTML_MEDIA_TYPES;
   }

   @Override
   public final void writeTo(T data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      ViewModel model = asViewModel(data);
      MessageBodyWriter<ViewModel> writer =
         providers.getMessageBodyWriter(ViewModel.class, genericType, annotations, mediaType);
      if (writer != null) {
         writer.writeTo(model, ViewModel.class, genericType, annotations, mediaType, httpHeaders, entityStream);
      } else {
         writeHtml(model, entityStream);
      }
   }

   public abstract ViewModel asViewModel(T data);

   /**
    * Fall-back HTML - used when templating system is not initialized.
    */
   private void writeHtml(ViewModel model, OutputStream output) throws IOException {
      Writer writer = new OutputStreamWriter(output);
      Map<String, Object> asMap = model.asMap();
      writer.write("<!DOCTYPE HTML>");
      writer.write("<html lang=\"en\"><head><link rel=\"stylesheet\" href=\"/lib/css/bootstrap.min.css\">");
      writer.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"></meta>");
      writer.write("<meta charset=\"UTF-8\"></head>");
      writer.write("<body>");
      writer.write("<div class=\"container\">");
      writer.write("<h1>OSEE - ");
      writer.write(model.getViewId());
      writer.write("</h1>");
      writer.write("<div class=\"container\" class=\"panel-body\">");
      writer.write("<div class=\"lead\">");
      writer.write("Templating system is not initialized. Using default template.");
      writer.write("</div>");
      writer.write("<table class=\"table table-striped\">");
      writer.write("<thead><tr><th>Key</th><th>Value</th></tr></thead>");
      writer.write("<tbody>");
      for (Entry<String, Object> entry : asMap.entrySet()) {
         writer.write("<tr><td>");
         writer.write(entry.getKey());
         writer.write("</td><td>");
         writer.write(String.valueOf(entry.getValue()));
         writer.write("</td></tr>");
      }
      writer.write("</tbody></table></div></div></body></html>");
      writer.flush();
   }

}
