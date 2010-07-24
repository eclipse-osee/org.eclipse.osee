/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
