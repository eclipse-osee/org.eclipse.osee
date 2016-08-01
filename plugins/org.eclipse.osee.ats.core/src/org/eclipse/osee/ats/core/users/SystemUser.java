/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.users;

/**
 * @author Donald G. Dunne
 */
public final class SystemUser extends AbstractAtsUser {

   protected SystemUser() {
      super(org.eclipse.osee.framework.core.enums.SystemUser.OseeSystem.getUserId());
   }

   @Override
   public String getName() {
      return org.eclipse.osee.framework.core.enums.SystemUser.OseeSystem.getName();
   }

   @Override
   public Long getId() {
      return org.eclipse.osee.framework.core.enums.SystemUser.OseeSystem.getId();
   }

}