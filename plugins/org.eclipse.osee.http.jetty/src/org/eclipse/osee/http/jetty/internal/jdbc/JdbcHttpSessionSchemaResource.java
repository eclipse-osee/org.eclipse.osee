/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.http.jetty.internal.jdbc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClientConfig;
import org.eclipse.osee.jdbc.JdbcSchemaResource;

/**
 * @author Roberto E. Escobar
 */
public class JdbcHttpSessionSchemaResource implements JdbcSchemaResource {

   private static final String SCHEMA_PATH = "schema/HTTP.SESSION.DS.SCHEMA.xml";

   @Override
   public InputStream getContent() {
      URL url = getResourceURL();
      try {
         return new BufferedInputStream(url.openStream());
      } catch (IOException ex) {
         throw new OseeCoreException(ex, "Error opening schema resource url [%s]", url);
      }
   }

   @Override
   public boolean isApplicable(JdbcClientConfig config) {
      return true;
   }

   @Override
   public URI getLocation() {
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
