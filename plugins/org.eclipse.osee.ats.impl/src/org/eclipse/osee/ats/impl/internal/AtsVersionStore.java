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
package org.eclipse.osee.ats.impl.internal;

import org.eclipse.osee.ats.api.version.IAtsVersionStore;

public class AtsVersionStore {

   public static AtsVersionStore instance;
   private static IAtsVersionStore versionStore;

   public void start() {
      AtsVersionStore.instance = this;
   }

   public static IAtsVersionStore getService() {
      if (instance == null) {
         throw new IllegalStateException("Ats Version Store Service has not been activated");
      }
      return versionStore;
   }

   public void setVersionStore(IAtsVersionStore definitionStore) {
      AtsVersionStore.versionStore = definitionStore;
   }

}
