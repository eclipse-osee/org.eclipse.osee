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
package org.eclipse.osee.framework.branch.management.servlet;

import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M Finkbeiner
 */
public class BranchManagerServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 226986283540461526L;

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      try {
         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/plain");
         HttpBranchCreationInfo info = new HttpBranchCreationInfo(req);
         int branchId = -1;
         switch (info.getFunction()) {
            case createChildBranch:
               branchId =
                     Activator.getInstance().getBranchCreation().createChildBranch(info.getParentTransactionId(),
                           info.getParentBranchId(), info.getBranchShortName(), info.getBranchName(),
                           info.getCreationComment(), info.getAssociatedArtifactId(), info.getAuthorId(),
                           info.branchWithFiltering(), info.getCompressArtTypeIds(), info.getPreserveArtTypeIds());
               break;
            case createRootBranch:
               branchId =
                     Activator.getInstance().getBranchCreation().createTopLevelBranch(info.getParentTransactionId(),
                           info.getParentBranchId(), info.getBranchShortName(), info.getBranchName(),
                           info.getCreationComment(), info.getAssociatedArtifactId(), info.getAuthorId(),
                           info.getStaticBranchName(), info.isSystemRootBranch());
               break;
         }
         if (branchId != -1) {
            resp.getWriter().write(Integer.toString(branchId));
         } else {
            resp.getWriter().write("Unknown Error during branch creation.");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Failed to respond to a branch servlet request [%s]",
               req.toString()), ex);
         resp.setContentType("text/plain");
         resp.getWriter().write(Lib.exceptionToString(ex));
      }
      resp.getWriter().flush();
      resp.getWriter().close();
   }

}
