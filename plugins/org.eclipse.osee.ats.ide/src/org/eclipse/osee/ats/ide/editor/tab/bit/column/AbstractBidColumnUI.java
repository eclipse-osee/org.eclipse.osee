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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault.CoreCodeColumnTokenDefault;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
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
 * Columns for PR Workflows to show BID data
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractBidColumnUI extends BackgroundLoadingPreComputedColumnUI {

   protected final AttributeTypeToken attrType;

   public AbstractBidColumnUI(CoreCodeColumnTokenDefault colTok, AttributeTypeToken attrType) {
      super(colTok);
      this.attrType = attrType;
   }

   // TBD - Change this to AtsActionEndpointApi.getBidsById once that performs better
   protected Collection<Artifact> getRelatedBidArts(Object element) {
      // Only PRs have BIDs, skip other Team Workflows
      if (Artifacts.isOfType(element, AtsArtifactTypes.ProblemReportTeamWorkflow)) {
         Collection<ArtifactToken> related = AtsApiService.get().getRelationResolver().getRelated((Artifact) element,
            AtsRelationTypes.ProblemReportToBid_Bid);
         return Collections.castAll(related);
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      try {
         if (workItem.isOfType(AtsArtifactTypes.ProblemReportTeamWorkflow)) {
            List<String> values = new ArrayList<>();
            for (Artifact bidArt : getRelatedBidArts(workItem.getStoreObject())) {
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
