/*********************************************************************
 * Copyright (c) 2015 Boeing
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
