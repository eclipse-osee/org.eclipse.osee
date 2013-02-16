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

import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionStore;

public class AtsTeamDefinitionStore {

   public static AtsTeamDefinitionStore instance;
   private static IAtsTeamDefinitionStore teamDefStore;

   public void start() {
      AtsTeamDefinitionStore.instance = this;
   }

   public static IAtsTeamDefinitionStore getService() {
      if (instance == null) {
         throw new IllegalStateException("Ats Team Definition Store Service has not been activated");
      }
      return teamDefStore;
   }

   public void setTeamDefinitionStore(IAtsTeamDefinitionStore definitionStore) {
      AtsTeamDefinitionStore.teamDefStore = definitionStore;
   }

}
