/*
 * Created on Nov 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;

/**
 * @author Jeff C. Phillips
 */
public class HttpChangeDataRequester {

   public static Object getChanges(TransactionRecord toTransactionRecord, TransactionRecord fromTransactionRecord, IProgressMonitor monitor, boolean isHistorical) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("sessionId", ClientSessionManager.getSessionId());
      parameters.put("toTransactionId", Integer.toString(toTransactionRecord.getId()));
      parameters.put("fromTransactionId", Integer.toString(fromTransactionRecord.getId()));
     
      if(isHistorical){
         parameters.put("historical", "historical");
      }
      return post(parameters);
   }

   private static Object post(Map<String, String> parameters) throws OseeCoreException {
      Object returnObject = null;
      String response = "";
      try {
         response =
               HttpProcessor.post(new URL(HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(
                     OseeServerContext.BRANCH_CREATION_CONTEXT, parameters)));
         //Not sure what will be returned
//         int branchId = Integer.parseInt(response);
//         branch = BranchManager.getBranch(branchId);
      } catch (NumberFormatException ex) {
         throw new OseeCoreException(response);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }

      // Kick events
//      OseeEventManager.kickBranchEvent(HttpBranchCreation.class, BranchEventType.Added, branch.getId());

      return returnObject;
   }
}
