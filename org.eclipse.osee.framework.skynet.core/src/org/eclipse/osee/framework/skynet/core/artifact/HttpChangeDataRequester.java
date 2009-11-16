/*
 * Created on Nov 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ChangeReportData;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;

/**
 * @author Jeff C. Phillips
 */
public class HttpChangeDataRequester {

   public static Object getChanges(TransactionRecord toTransactionRecord, TransactionRecord fromTransactionRecord, IProgressMonitor monitor, boolean isHistorical) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.CHANGE_REPORT.name());

      ChangeReportData requestData = new ChangeReportData(toTransactionRecord, fromTransactionRecord, isHistorical);
      AcquireResult response =
            HttpMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters, requestData, AcquireResult.class);
      if (response.wasSuccessful()) {
         //OseeEventManager.kickBranchEvent(HttpBranchCreation.class, , branch.getId());
      }
      return response;
   }
}
