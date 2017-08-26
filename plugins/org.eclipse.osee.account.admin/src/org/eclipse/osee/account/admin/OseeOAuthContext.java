/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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