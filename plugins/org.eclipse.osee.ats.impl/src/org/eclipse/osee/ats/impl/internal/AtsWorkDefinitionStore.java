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

import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionStore;

public class AtsWorkDefinitionStore {

   public static AtsWorkDefinitionStore instance;
   private static IAtsWorkDefinitionStore definitionStore;

   public void start() {
      AtsWorkDefinitionStore.instance = this;
   }

   public static IAtsWorkDefinitionStore getService() {
      if (instance == null) {
         throw new IllegalStateException("Ats Work Definition Store Service has not been activated");
      }
      return definitionStore;
   }

   public void setWorkDefinitionStore(IAtsWorkDefinitionStore definitionStore) {
      AtsWorkDefinitionStore.definitionStore = definitionStore;
   }
}
