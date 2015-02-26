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

import java.net.URL;
import org.eclipse.osee.jdbc.JdbcClientConfig;
import org.eclipse.osee.jdbc.JdbcMigrationResource;

/**
 * @author Roberto E. Escobar
 */
public class JdbcHttpSessionMigrationResource implements JdbcMigrationResource {

   private static final String SCHEMA_PATH = "migration/";

   @Override
   public boolean isApplicable(JdbcClientConfig config) {
      return true;
   }

   @Override
   public URL getLocation() {
      return getClass().getResource(SCHEMA_PATH);
   }

}
