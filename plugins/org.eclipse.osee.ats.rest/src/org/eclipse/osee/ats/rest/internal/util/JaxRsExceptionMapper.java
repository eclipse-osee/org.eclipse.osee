/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import static org.eclipse.osee.template.engine.OseeTemplateTokens.ExceptionHtml;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.template.engine.PageFactory;

/**
 * @author Ryan D. Brooks
 */
@Provider
public final class JaxRsExceptionMapper implements ExceptionMapper<Exception> {
   private final IResourceRegistry registry;
   private static final int NumOfCharsInTypicalStackTrace = 2200;

   public JaxRsExceptionMapper(IResourceRegistry registry) {
      this.registry = registry;
   }

   /**
    * Create valid xhtml exception page that includes the full stack trace and return a reasonable error page even if
    * page creation fails too.
    */
   @Override
   public Response toResponse(Exception ex) {
      return Response.serverError().entity(exceptionToHtml(ex)).type(MediaType.TEXT_HTML_TYPE).build();
   }

   String exceptionToHtml(Exception ex) {
      String html;

      try {
         String exStr = Lib.exceptionToString(ex);
         html = PageFactory.realizePage(registry, ExceptionHtml, "title", ex.toString(), "stacktrace", exStr);
      } catch (Exception ex1) {
         StringBuilder strB = new StringBuilder(NumOfCharsInTypicalStackTrace);
         strB.append("<pre>\n");
         strB.append(StringEscapeUtils.escapeHtml(Lib.exceptionToString(ex)));
         strB.append("\n\nAddtional exception while reporting first exception:\n");
         strB.append(StringEscapeUtils.escapeHtml(Lib.exceptionToString(ex1)));
         strB.append("\n</pre>");
         html = strB.toString();
      }
      return html;
   }
}