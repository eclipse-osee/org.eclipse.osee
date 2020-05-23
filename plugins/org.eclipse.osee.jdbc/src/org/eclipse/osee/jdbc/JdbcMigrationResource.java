/*********************************************************************
 * Copyright (c) 2009 Boeing
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

import java.net.URL;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public interface JdbcMigrationResource {

   boolean isApplicable(JdbcClientConfig config);

   URL getLocation();

   void addPlaceholders(Map<String, String> placeholders);

}
