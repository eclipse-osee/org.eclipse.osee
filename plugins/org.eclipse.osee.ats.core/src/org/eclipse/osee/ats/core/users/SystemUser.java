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

import org.eclipse.osee.framework.core.data.ArtifactId;
/**
 * @author Donald G. Dunne
 */
public final class SystemUser extends AbstractAtsUser {

   protected SystemUser() {
      super("99999999");
   }

   @Override
   public String getName() {
      return "OSEE System";
   }

   @Override
   public String getGuid() {
      return "AAABDBYPet4AGJyrc9dY1w";
   }

   @Override
   public String getDescription() {
      return "System User";
   }

   @Override
   public boolean isActive() {
      return true;
   }

   @Override
   public long getId() {
      return 11;
   }

}
