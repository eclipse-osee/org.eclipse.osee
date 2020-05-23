/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.ide.world.search;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Return all Team Workflows with one of TeamDefs reference as TeamDefinition attribute. Uses quick search for
 * performance.
 *
 * @author Donald G. Dunne
 */
public class TeamDefinitionQuickSearch extends AttributeValueQuickSearch {

   public TeamDefinitionQuickSearch(Collection<? extends IAtsTeamDefinition> teamDefs) {
      super(AtsAttributeTypes.TeamDefinitionReference, AtsObjects.toIdStrings(teamDefs));
   }

   @Override
   public Collection<Artifact> performSearch() {
      return performSearch(true);
   }

}