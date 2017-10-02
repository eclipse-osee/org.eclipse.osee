/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world.search;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Return all Team Workflows with one of TeamDefs guids as TeamDefinition attribute. Uses quick search for performance.
 *
 * @author Donald G. Dunne
 */
public class TeamDefinitionQuickSearch extends AttributeValueQuickSearch {

   public TeamDefinitionQuickSearch(Collection<? extends IAtsTeamDefinition> teamDefs) {
      super(AtsAttributeTypes.TeamDefinitionReference, AtsObjects.toUuidStrings(teamDefs));
   }

   @Override
   public Collection<Artifact> performSearch()  {
      return performSearch(true);
   }

}