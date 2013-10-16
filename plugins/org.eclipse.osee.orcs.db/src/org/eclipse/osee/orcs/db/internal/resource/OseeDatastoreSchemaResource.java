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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.eclipse.osee.database.schema.SchemaResource;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class OseeDatastoreSchemaResource implements SchemaResource {

   private static final String FILE_PATH = "schema/SKYNET.VERSIONING.SCHEMA.xml";

   @Override
   public InputStream getContent() throws OseeCoreException {
      InputStream inputStream = null;
      try {
         URL url = getURL();
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

   @Override
   public URI getLocation() throws OseeCoreException {
      try {
         return getURL().toURI();
      } catch (URISyntaxException ex) {
         throw new OseeCoreException(ex, "Error finding [%s] schema resource", FILE_PATH);
      }
   }

   private URL getURL() {
      Bundle bundle = FrameworkUtil.getBundle(this.getClass());
      return bundle.getEntry(FILE_PATH);
   }
}
