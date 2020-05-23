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

package org.eclipse.osee.account.admin;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseePermission;

/**
 * @author Roberto E. Escobar
 */
public interface OseeOAuthContext {

   OseePrincipal getOwner();

   OseePrincipal getClient();

   String getTokenGrantType();

   String getClientId();

   String getTokenKey();

   String getTokenAudience();

   List<OseePermission> getPermissions();

}