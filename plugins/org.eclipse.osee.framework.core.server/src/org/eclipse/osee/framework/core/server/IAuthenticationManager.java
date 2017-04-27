/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.server;

import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;

/**
 * @author Roberto E. Escobar
 */
public interface IAuthenticationManager {

   /**
    * @return <b>true</b> if authentication success
    */
   public boolean authenticate(OseeCredential credential) throws OseeAuthenticationException;

   /**
    * Resolves user credentials into an OSEE User Info
    * 
    * @return OSEE user info
    */
   public UserToken asUserToken(OseeCredential credential) throws OseeAuthenticationException;

   /**
    * Gets an array of available authentication protocols
    * 
    * @return authentication protocols
    */
   public String[] getProtocols();

   public String getProtocol();
}
