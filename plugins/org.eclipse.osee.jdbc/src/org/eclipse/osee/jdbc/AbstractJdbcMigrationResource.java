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
package org.eclipse.osee.jdbc;

import java.util.Map;

/**
 * @author John Misinco
 */
public abstract class AbstractJdbcMigrationResource implements JdbcMigrationResource {

   @Override
   public boolean isApplicable(JdbcClientConfig config) {
      return true;
   }

   @Override
   public void addPlaceholders(Map<String, String> placeholders) {
      // do nothing
   }

}
