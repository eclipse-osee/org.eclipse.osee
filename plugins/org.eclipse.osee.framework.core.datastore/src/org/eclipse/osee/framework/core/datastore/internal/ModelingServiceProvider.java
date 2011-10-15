package org.eclipse.osee.framework.core.datastore.internal;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IOseeModelingService;

public interface ModelingServiceProvider {
   IOseeModelingService getIOseeModelingService() throws OseeCoreException;
}
