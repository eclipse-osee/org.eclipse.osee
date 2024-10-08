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

package org.eclipse.osee.ats.core.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class Action extends AtsObject implements IAtsAction {

   private final AtsApi atsApi;
   private final ArtifactToken artifact;

   public Action(AtsApi atsApi, ArtifactToken artifact) {
      super(artifact.getName(), artifact.getId());
      this.atsApi = atsApi;
      this.artifact = artifact;
      setStoreObject(artifact);
   }

   @Override
   public Collection<IAtsTeamWorkflow> getTeamWorkflows() {
      return atsApi.getRelationResolver().getRelated(this, AtsRelationTypes.ActionToWorkflow_TeamWorkflow,
         IAtsTeamWorkflow.class);
   }

   @Override
   public String getAtsId() {
      try {
         return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.AtsId, getIdString());
      } catch (OseeCoreException ex) {
         return null;
      }
   }

   @Override
   public void setAtsId(String atsId) {
      throw new OseeStateException("Not implemented");
   }

}
