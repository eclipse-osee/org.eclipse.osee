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
package org.eclipse.osee.framework.manager.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class OseeCacheServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 6693534844874109524L;

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      try {

         IOseeCachingService provider = MasterServletActivator.getInstance().getOseeCache();

         provider.getBranchCache();
         provider.getTransactionCache();
         provider.getArtifactTypeCache();
         provider.getEnumTypeCache();
         provider.getAttributeTypeCache();
         provider.getRelationTypeCache();

      } catch (Exception ex) {
         OseeLog.log(MasterServletActivator.class, Level.SEVERE, String.format("Branch servlet request error: [%s]",
               req.toString()), ex);
         resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
         resp.setContentType("text/plain");
         resp.getWriter().write(Lib.exceptionToString(ex));
      }
      resp.getWriter().flush();
      resp.getWriter().close();
   }
}
