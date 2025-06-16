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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

public class PrBitNameStateColumn extends AbstractPrBitColumnUI {

   public static PrBitNameStateColumn instance = new PrBitNameStateColumn();

   public static PrBitNameStateColumn getInstance() {
      return instance;
   }

   public PrBitNameStateColumn() {
      super(AtsColumnTokensDefault.PrBitNamesStatesColumn, CoreAttributeTypes.Name);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PrBitNameStateColumn copy() {
      PrBitNameStateColumn newXCol = new PrBitNameStateColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      try {
         if (workItem.isOfType(ProblemReportTeamWorkflow)) {
            List<String> values = new ArrayList<>();
            for (Artifact bidArt : getRelatedBidArts(workItem.getStoreObject())) {
               String name = bidArt.getSoleAttributeValue(attrType);
               String state = bidArt.getSoleAttributeValue(AtsAttributeTypes.BitState);
               values.add(String.format("%s (%s)", name, state));
            }
            if (!values.isEmpty()) {
               values.sort(Comparator.naturalOrder());
               return Collections.toString(", ", values);
            }
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

}
