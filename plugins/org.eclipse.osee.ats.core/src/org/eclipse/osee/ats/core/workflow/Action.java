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
      return atsApi.getRelationResolver().getRelated(this, AtsRelationTypes.ActionToWorkflow_WorkFlow,
         IAtsTeamWorkflow.class);
   }

   @Override
   public String getAtsId() {
      try {
         return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.AtsId,
            String.valueOf(getId()));
      } catch (OseeCoreException ex) {
         return null;
      }
   }

   @Override
   public void setAtsId(String atsId) {
      throw new OseeStateException("Not implemented");
   }

}
