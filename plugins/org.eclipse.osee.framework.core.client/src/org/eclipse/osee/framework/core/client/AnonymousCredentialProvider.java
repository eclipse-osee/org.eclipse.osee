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
