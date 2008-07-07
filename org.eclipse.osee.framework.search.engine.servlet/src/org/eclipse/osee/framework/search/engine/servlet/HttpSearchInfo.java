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
package org.eclipse.osee.framework.search.engine.servlet;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.framework.search.engine.Options;

/**
 * @author Roberto E. Escobar
 */
class HttpSearchInfo {

   private String queryString;
   private Options options;

   @SuppressWarnings("unchecked")
   public HttpSearchInfo(HttpServletRequest request) {
      this.options = new Options();
      this.queryString = request.getParameter("query");
      Enumeration<String> enumeration = request.getParameterNames();
      while (enumeration.hasMoreElements()) {
         String name = enumeration.nextElement();
         String value = request.getParameter(name);
         if (name.equals("query")) {
            this.queryString = value;
         } else {
            options.put(name, value);
         }
      }
   }

   public String getQuery() {
      return queryString;
   }

   public String toString() {
      return queryString;
   }

   public Options getOptions() {
      return options;
   }
}
