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

package org.eclipse.osee.ats.core.config;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemArtifact extends Artifact {

   public ActionableItemArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public Collection<User> getLeads() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelationTypes.ActionableItemLead_Lead, User.class);
   }

   public boolean isActionable() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.Actionable, false);
   }

   public Collection<TeamDefinitionArtifact> getImpactedTeamDefs() throws OseeCoreException {
      return TeamDefinitionManagerCore.getImpactedTeamDefs(Arrays.asList(this));
   }

}
