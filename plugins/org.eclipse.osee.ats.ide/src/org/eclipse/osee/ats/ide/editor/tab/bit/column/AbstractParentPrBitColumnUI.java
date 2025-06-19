/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.bit.column;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ProblemReportTeamWorkflow;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamWorkflow;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault.CoreCodeColumnTokenDefault;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.column.BackgroundLoadingPreComputedColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * Columns for PR's CR/Team Workflows to show BIT data
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractParentPrBitColumnUI extends BackgroundLoadingPreComputedColumnUI {

   protected final AttributeTypeToken attrType;

   public AbstractParentPrBitColumnUI(CoreCodeColumnTokenDefault colTok, AttributeTypeToken attrType) {
      super(colTok);
      this.attrType = attrType;
   }

   protected Collection<Artifact> getRelatedParentBidArts(Object element) {
      // Only PRs have BIDs, skip other Team Workflows
      if (Artifacts.isOfType(element, TeamWorkflow) && !Artifacts.isOfType(element, ProblemReportTeamWorkflow)) {
         Collection<ArtifactToken> parentBids = AtsApiService.get().getRelationResolver().getRelated((Artifact) element,
            AtsRelationTypes.BuildImpactDataToTeamWf_Bid);
         return Collections.castAll(parentBids);
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      try {
         if (workItem.isOfType(TeamWorkflow) && !workItem.isOfType(ProblemReportTeamWorkflow)) {
            List<String> values = new ArrayList<>();
            for (Artifact bidArt : getRelatedParentBidArts(workItem.getStoreObject())) {
               String value = bidArt.getSoleAttributeValue(attrType);
               if (!values.contains(value)) {
                  values.add(value);
               }
            }
            values.sort(Comparator.naturalOrder());
            return Collections.toString(", ", values);
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

}
