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
public final class Anonymous extends AbstractAtsUser {

   protected Anonymous() {
      super(org.eclipse.osee.framework.core.enums.SystemUser.Anonymous.getUserId());
   }

   @Override
   public String getName() {
      return org.eclipse.osee.framework.core.enums.SystemUser.Anonymous.getName();
   }

   @Override
   public Long getId() {
      return org.eclipse.osee.framework.core.enums.SystemUser.Anonymous.getId();
   }

}