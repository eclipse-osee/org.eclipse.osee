/*
 * Created on Nov 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.IOseeUser;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;

/**
 * @author Megumi Telles
 */
public class HttpCommitDataRequester {

   public static void commitBranch(IProgressMonitor monitor, IOseeUser user, Branch sourceBranch, Branch destinationBranch, boolean archiveSourceBranch) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", "BranchCommit");
      if (user != null) {
         parameters.put("User", user.getName());
      }
      if (sourceBranch != null) {
         parameters.put("Source Branch", sourceBranch.getName());
      }
      if (destinationBranch != null) {
         parameters.put("Destination Branch", destinationBranch.getName());
      }
      parameters.put("Archive Source Branch", new Boolean(archiveSourceBranch).toString());
      post(parameters);
   }

   private static void post(Map<String, String> parameters) throws OseeCoreException {
      String response = "";
      try {
         response =
               HttpProcessor.post(new URL(HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(
                     OseeServerContext.BRANCH_CREATION_CONTEXT, parameters)));
      } catch (MalformedURLException ex) {
         throw new OseeCoreException(response);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }

      // Kick commit event?
      //OseeEventManager.kickBranchEvent(HttpBranchCreation.class, , branch.getId());
   }
}
