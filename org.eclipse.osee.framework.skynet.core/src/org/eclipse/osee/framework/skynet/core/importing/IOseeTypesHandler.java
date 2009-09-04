package org.eclipse.osee.framework.skynet.core.importing;

import java.net.URL;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IOseeTypesHandler {

   boolean isApplicable(String resource);

   void execute(IProgressMonitor monitor, Object context, URL url) throws OseeCoreException;

}
