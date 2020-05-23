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

package org.eclipse.osee.jdbc;

/**
 * @author Roberto E. Escobar
 */
public interface JdbcService {

   String getId();

   JdbcClient getClient();

   boolean hasServer();

   JdbcServerConfig getServerConfig();

   boolean isServerAlive(long waitTime);

}