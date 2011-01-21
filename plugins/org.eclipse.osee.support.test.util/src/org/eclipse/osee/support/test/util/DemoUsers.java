/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.support.test.util;

import org.eclipse.osee.framework.core.data.IOseeUserInfo;

/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

/**
 * @author Donald G. Dunne
 */
public enum DemoUsers implements IOseeUserInfo {
   Joe_Smith("Joe Smith"),
   Kay_Jones("Kay Jones"),
   Jason_Michael("Jason Michael"),
   Alex_Kay("Alex Kay"),
   Inactive_Steve("Inactive Steve");
   private final String name;

   DemoUsers(String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getUserID() {
      return name;
   }

   @Override
   public String getEmail() {
      return name;
   }

   @Override
   public boolean isActive() {
      return name.contains("Inactive");
   }

   @Override
   public boolean isCreationRequired() {
      return false;
   }

}
