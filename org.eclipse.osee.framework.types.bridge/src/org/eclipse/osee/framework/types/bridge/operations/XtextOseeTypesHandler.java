package org.eclipse.osee.framework.types.bridge.operations;

import java.net.URL;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.importing.IOseeTypesHandler;

public class XtextOseeTypesHandler implements IOseeTypesHandler {

   @Override
   public void execute(IProgressMonitor monitor, Object context, URL url) throws OseeCoreException {
      try {
         IOperation operation = new XTextToOseeTypeOperation(context, url.toURI());
         Operations.executeWork(operation, monitor, -1);
         Operations.checkForErrorStatus(operation.getStatus());
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public boolean isApplicable(String resource, URL url) {
      return url != null && Strings.isValid(resource) && resource.endsWith(".osee");
   }

}
