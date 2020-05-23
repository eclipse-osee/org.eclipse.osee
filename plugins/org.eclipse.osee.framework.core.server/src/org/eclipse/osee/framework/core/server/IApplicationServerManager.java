/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.server;

import java.util.Date;

/**
 * @author Roberto E. Escobar
 */
public interface IApplicationServerManager {

   void shutdown();

   boolean isSystemIdle();

   boolean executeLookupRegistration();

   String getId();

   String getServerUri();

   int getPort();

   Date getDateStarted();

   String[] getVersions();
}