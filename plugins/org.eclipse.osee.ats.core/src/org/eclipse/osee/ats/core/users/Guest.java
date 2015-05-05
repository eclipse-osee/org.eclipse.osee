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
public final class Guest extends AbstractAtsUser {

   protected Guest() {
      super("99999998");
   }

   @Override
   public String getName() {
      return "Guest";
   }

   @Override
   public boolean isActive() {
      return true;
   }

   @Override
   public long getUuid() {
      return 1896;
   }

}
