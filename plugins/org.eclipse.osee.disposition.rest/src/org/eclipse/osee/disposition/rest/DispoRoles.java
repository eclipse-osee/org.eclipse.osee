/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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