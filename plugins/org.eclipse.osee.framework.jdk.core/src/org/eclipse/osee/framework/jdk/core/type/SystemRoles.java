/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Roberto E. Escobar
 */
public enum SystemRoles {
   ANONYMOUS,
   ADMINISTRATOR,
   AUTHENTICATED;

   public static final String ROLES_ANONYMOUS = "anonymous";
   public static final String ROLES_ADMINISTRATOR = "administrator";
   public static final String ROLES_AUTHENTICATED = "authenticated";

}
