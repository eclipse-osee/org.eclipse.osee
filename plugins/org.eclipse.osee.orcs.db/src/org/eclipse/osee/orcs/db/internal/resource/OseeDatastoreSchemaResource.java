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
package org.eclipse.osee.orcs.db.internal.resource;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import org.eclipse.osee.database.schema.SchemaResource;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class OseeDatastoreSchemaResource implements SchemaResource {

   @Override
   public InputStream getContent() throws OseeCoreException {
      InputStream inputStream = null;
      try {
         Bundle bundle = FrameworkUtil.getBundle(this.getClass());
         URL url = bundle.getEntry("schema/SKYNET.VERSIONING.SCHEMA.xml");
         inputStream = new BufferedInputStream(url.openStream());
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return inputStream;
   }

   @Override
   public boolean isApplicable() {
      return true;
   }
}
