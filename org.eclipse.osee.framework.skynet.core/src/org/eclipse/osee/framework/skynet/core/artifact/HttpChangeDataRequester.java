/*
 * Created on Nov 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.ChangeReportData;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;

/**
 * @author Jeff C. Phillips
 */
public class HttpChangeDataRequester {

   public static Object getChanges(TransactionRecord toTransactionRecord, TransactionRecord fromTransactionRecord, IProgressMonitor monitor, boolean isHistorical) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.CHANGE_REPORT.name());

      ChangeReportData data = new ChangeReportData(toTransactionRecord, fromTransactionRecord, isHistorical);
      AcquireResult response = post(parameters, data);
      if (response.wasSuccessful()) {
         //OseeEventManager.kickBranchEvent(HttpBranchCreation.class, , branch.getId());
      }
      return response;
   }

   private static AcquireResult post(Map<String, String> parameters, ChangeReportData data) throws OseeCoreException {
      IDataTranslationService service = null;
      PropertyStore propertyStore = service.convert(data, ChangeReportData.class);
      try {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         propertyStore.save(buffer);
         String urlString =
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.BRANCH_CONTEXT, parameters);
         return HttpProcessor.post(new URL(urlString), new ByteArrayInputStream(buffer.toByteArray()), "text/xml",
               "UTF-8", new ByteArrayOutputStream());
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }
}
