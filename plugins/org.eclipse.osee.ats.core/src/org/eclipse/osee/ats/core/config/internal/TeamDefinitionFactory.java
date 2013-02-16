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
package org.eclipse.osee.ats.core.config.internal;

import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionFactory implements ITeamDefinitionFactory {
   private final AtsConfigCache cache;

   public TeamDefinitionFactory(AtsConfigCache cache) {
      this.cache = cache;
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String guid, String name) {
      return createTeamDefinition(name, guid, HumanReadableId.generate());
   }

   public IAtsTeamDefinition createTeamDefinition(String name, String guid, String humanReadableId) {
      if (guid == null) {
         throw new IllegalArgumentException("guid can not be null");
      }
      IAtsTeamDefinition teamDef = new TeamDefinition(name, guid, humanReadableId);
      cache.cache(teamDef);
      return teamDef;
   }

   @Override
   public IAtsTeamDefinition getOrCreate(String guid, String name) {
      IAtsTeamDefinition teamDef = cache.getSoleByGuid(guid, IAtsTeamDefinition.class);
      if (teamDef == null) {
         teamDef = createTeamDefinition(guid, name);
      }
      return teamDef;
   }

}
