package org.eclipse.osee.framework.core.dsl.ui.integration.operations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.dsl.integration.util.ModelUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeDslProvider implements OseeDslProvider {

   private OseeDsl oseeDsl;
   private final String locationUri;

   protected AbstractOseeDslProvider(String locationUri) {
      this.locationUri = locationUri;
   }

   protected abstract String getModelFromStorage() throws OseeCoreException;

   protected abstract void saveModelToStorage(String model) throws OseeCoreException;

   @Override
   public void loadDsl() throws OseeCoreException {
      String accessModel = getModelFromStorage();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      IOperation operation = new OseeTypesExportOperation(outputStream);
      Operations.executeWorkAndCheckStatus(operation);
      try {
         outputStream.write(accessModel.getBytes("utf-8"));
         oseeDsl = ModelUtil.loadModel(locationUri, outputStream.toString("utf-8"));
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   @Override
   public OseeDsl getDsl() throws OseeCoreException {
      if (oseeDsl == null) {
         loadDsl();
      }
      return oseeDsl;
   }

   @Override
   public void storeDsl(OseeDsl dsl) throws OseeCoreException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try {
         ModelUtil.saveModel(dsl, locationUri, outputStream, false);
         saveModelToStorage(outputStream.toString("UTF-8"));
         loadDsl();
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

}