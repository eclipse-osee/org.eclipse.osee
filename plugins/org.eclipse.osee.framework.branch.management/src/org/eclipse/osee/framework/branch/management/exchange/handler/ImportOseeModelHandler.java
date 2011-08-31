/*
 * Created on Aug 31, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.message.IOseeModelingService;
import org.eclipse.osee.framework.core.message.OseeImportModelRequest;
import org.eclipse.osee.framework.core.message.OseeImportModelResponse;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.util.Lib;

public class ImportOseeModelHandler extends AbstractOperation {

   private final IOseeModelingService modelingService;
   private final URI model;

   public ImportOseeModelHandler(IOseeModelingService modelingService, OperationLogger logger, URI model) {
      super("Import Exchange Type Model", Activator.PLUGIN_ID, logger);
      this.modelingService = modelingService;
      this.model = model;
   }

   private String getModel(URL url) throws IOException {
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(url.openStream());
         return Lib.inputStreamToString(inputStream);
      } finally {
         Lib.close(inputStream);
      }
   }

   private String getName(URI uri) {
      String name = uri.toASCIIString();
      int index = name.lastIndexOf("/");
      if (index > 0) {
         name = name.substring(index + 1, name.length());
      }
      return name;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      OseeImportModelRequest modelRequest =
         new OseeImportModelRequest(getName(model), getModel(model.toURL()), false, false, true);
      OseeImportModelResponse response = new OseeImportModelResponse();

      logf("Updating Type Model with [%s]", model);
      modelingService.importOseeTypes(monitor, true, modelRequest, response);
      log("Type Model Import complete");
   }
}
