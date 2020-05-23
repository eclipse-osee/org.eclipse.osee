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

package org.eclipse.osee.framework.core.server.internal.session;

import java.util.Date;

/**
 * @author Roberto E. Escobar
 */
public interface ISessionCollector {

   void collect(String guid, String userId, Date creationDate, String clientVersion, String clientMachineName, String clientAddress, int clientPort);

}