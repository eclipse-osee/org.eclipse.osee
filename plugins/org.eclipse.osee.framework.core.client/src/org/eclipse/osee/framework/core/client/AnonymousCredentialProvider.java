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
package org.eclipse.osee.framework.core.client;

import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.enums.SystemUser;

/**
 * @author Roberto E. Escobar
 */
public class AnonymousCredentialProvider extends BaseCredentialProvider {

   @Override
   public OseeCredential getCredential() {
      OseeCredential credential = super.getCredential();
      credential.setUserName(SystemUser.Anonymous.getName());
      return credential;
   }
}
