/*
 * Created on May 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.servlet;

import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M Finkbeiner
 */
public class BranchManagerServlet extends HttpServlet {

   /**
    * 
    */
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
                     Activator.getInstance().getBranchCreation().createChildBranch(info.getParentBranchId(),
                           info.getBranchShortName(), info.getBranchName(), info.getCreationComment(),
                           info.getAssociatedArtifactId(), info.getAuthorId());
               break;
            case createRootBranch:
               branchId =
                     Activator.getInstance().getBranchCreation().createRootBranch(info.getParentBranchId(),
                           info.getBranchShortName(), info.getBranchName(), info.getCreationComment(),
                           info.getAssociatedArtifactId(), info.getAuthorId(), info.getStaticBranchName());
               break;
         }
         if (branchId != -1) {
            resp.getWriter().write(Integer.toString(branchId));
         } else {
            resp.getWriter().write("Unknown Error during branch creation.");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class.getName(), Level.SEVERE, String.format(
               "Failed to respond to a branch servlet request [%s]", req.toString()), ex);
         resp.getWriter().write(Lib.exceptionToString(ex));
      }
      resp.getWriter().flush();
      resp.getWriter().close();
   }

}
