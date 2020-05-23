/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.config;

import java.util.Collection;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.internal.AtsApiService;

/**
 * @author Donald G Dunne
 */
public class TeamDefinitionUtility implements ITeamDefinitionUtility {

   @Override
   public Collection<IAtsTeamDefinition> getImpactedTeamDefs(Collection<IAtsActionableItem> aias) {
      return AtsApiService.get().getTeamDefinitionService().getImpactedTeamDefs(aias);
   }

}
