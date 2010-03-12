package org.eclipse.osee.framework.skynet.core.importing;

import java.net.URI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IOseeTypesHandler {

   boolean isApplicable(String resource);

   void execute(IProgressMonitor monitor, URI uri) throws OseeCoreException;

}
