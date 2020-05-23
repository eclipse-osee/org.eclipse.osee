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

import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Roberto E. Escobar
 */
public interface IAuthenticationProvider {

   public String getProtocol();

   public boolean authenticate(OseeCredential credential);

   public UserToken asOseeUserId(OseeCredential credential);

}
