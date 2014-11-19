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
public final class UnAssigned extends AbstractAtsUser {

   protected UnAssigned() {
      super("99999997");
   }

   @Override
   public String getName() {
      return "UnAssigned";
   }

   @Override
   public String getGuid() {
      return "AAABDi1tMx8Al92YWMjeRw";
   }

   @Override
   public boolean isActive() {
      return true;
   }

   @Override
   public long getId() {
      return 33429;
   }

}
