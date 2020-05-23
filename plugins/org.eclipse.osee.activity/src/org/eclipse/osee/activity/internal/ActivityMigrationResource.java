/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.activity.internal;

import java.net.URL;
import org.eclipse.osee.jdbc.AbstractJdbcMigrationResource;

/**
 * @author Ryan D. Brooks
 */
public class ActivityMigrationResource extends AbstractJdbcMigrationResource {

   private static final String SCHEMA_PATH = "migration/";

   @Override
   public URL getLocation() {
      return getClass().getResource(SCHEMA_PATH);
   }

}
