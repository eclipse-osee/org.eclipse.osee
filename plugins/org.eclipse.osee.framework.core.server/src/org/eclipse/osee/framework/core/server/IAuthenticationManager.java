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
public interface IAuthenticationManager {

   /**
    * @return <b>true</b> if authentication success
    */
   public boolean authenticate(OseeCredential credential);

   /**
    * Resolves user credentials into an OSEE User Info
    *
    * @return OSEE user info
    */
   public UserToken asUserToken(OseeCredential credential);

   /**
    * Gets an array of available authentication protocols
    *
    * @return authentication protocols
    */
   public String[] getProtocols();

   public String getProtocol();
}
