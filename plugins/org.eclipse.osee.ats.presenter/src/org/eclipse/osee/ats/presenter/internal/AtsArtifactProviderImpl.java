/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.presenter.internal;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.search.AtsArtifactProvider;
import org.eclipse.osee.ats.api.tokens.AtsArtifactToken;
import org.eclipse.osee.ats.api.tokens.AtsAttributeTypes;
import org.eclipse.osee.ats.api.tokens.AtsRelationTypes;
import org.eclipse.osee.display.presenter.ArtifactProviderImpl;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author John Misinco
 */
public class AtsArtifactProviderImpl extends ArtifactProviderImpl implements AtsArtifactProvider {

   public AtsArtifactProviderImpl(Log logger, ExecutorAdmin executorAdmin, OrcsApi oseeApi, ApplicationContext context) {
      super(logger, executorAdmin, oseeApi, context);

   }

   @Override
   public Collection<ReadableArtifact> getPrograms() throws OseeCoreException {
      List<ReadableArtifact> programs = null;
      ReadableArtifact webProgramsArtifact =
         getArtifactByArtifactToken(CoreBranches.COMMON, AtsArtifactToken.WebPrograms);
      if (webProgramsArtifact != null) {
         programs = getRelatedArtifacts(webProgramsArtifact, CoreRelationTypes.Universal_Grouping__Members);
      }
      return programs;
   }

   @Override
   public Collection<ReadableArtifact> getBuilds(String programGuid) throws OseeCoreException {
      Collection<ReadableArtifact> relatedArtifacts = null;
      ReadableArtifact teamDef = null;
      ReadableArtifact programArtifact = getArtifactByGuid(CoreBranches.COMMON, programGuid);
      if (programArtifact != null) {
         teamDef = getRelatedArtifact(programArtifact, CoreRelationTypes.SupportingInfo_SupportingInfo);
      }
      if (teamDef != null) {
         relatedArtifacts = getRelatedArtifacts(teamDef, AtsRelationTypes.TeamDefinitionToVersion_Version);
      }
      return relatedArtifacts;
   }

   @Override
   public String getBaselineBranchGuid(String buildArtGuid) throws OseeCoreException {
      String guid = null;
      ReadableArtifact buildArtifact = getArtifactByGuid(CoreBranches.COMMON, buildArtGuid);
      if (buildArtifact != null) {
         guid = buildArtifact.getSoleAttributeAsString(AtsAttributeTypes.BaselineBranchGuid);
      }
      return guid;
   }

}
