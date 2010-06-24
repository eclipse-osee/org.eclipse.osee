/*
 * Created on Jun 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.datastore.internal;

import java.io.InputStream;
import java.net.URL;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.datastore.IOseeSchemaResource;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.osgi.framework.Bundle;

public class CoreOseeSchemaResource implements IOseeSchemaResource {

   @Override
   public InputStream getContent() throws OseeCoreException {
      InputStream inputStream = null;
      try {
         Bundle bundle = Platform.getBundle("org.eclipse.osee.framework.core.datastore");
         URL url = bundle.getEntry("support/SKYNET.VERSIONING.SCHEMA.xml");
         inputStream = url.openStream();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return inputStream;
   }
}
