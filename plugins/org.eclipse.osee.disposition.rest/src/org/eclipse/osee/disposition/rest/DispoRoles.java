/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.disposition.rest;

/**
 * @author Angel Avila
 */
public enum DispoRoles {
   USER,
   ADMIN;

   public static final String ROLES_USER = "dispoUser";
   public static final String ROLES_ADMINISTRATOR = "dispoAdmin";

}