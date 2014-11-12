/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.activity.server.internal;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.eclipse.osee.database.schema.SchemaResource;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Ryan D. Brooks
 */
public class ActivitySchemaResource implements SchemaResource {

   private static final String SCHEMA_PATH = "schema/ACTIVITY.DS.SCHEMA.xml";

   @Override
   public InputStream getContent() throws OseeCoreException {
      InputStream inputStream = null;
      try {
         URL url = getResourceURL();
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
         return getResourceURL().toURI();
      } catch (URISyntaxException ex) {
         throw new OseeCoreException(ex, "Error finding [%s] schema resource", SCHEMA_PATH);
      }
   }

   private URL getResourceURL() {
      return getClass().getResource(SCHEMA_PATH);
   }
}
