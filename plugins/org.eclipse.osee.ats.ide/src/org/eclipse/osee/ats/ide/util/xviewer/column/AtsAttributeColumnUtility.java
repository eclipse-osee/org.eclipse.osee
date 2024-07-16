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

package org.eclipse.osee.ats.ide.util.xviewer.column;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.column.IPersistAltLeftClickProvider;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.PromptChangeUtil;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IAttributeColumn;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeColumnUtility {

   public static boolean handleAltLeftClick(Object columnData, Object item, boolean persist) {
      try {
         if (columnData instanceof IAttributeColumn) {
            IAttributeColumn attrColumn = (IAttributeColumn) columnData;
            if (item instanceof Artifact) {
               Artifact useArt = AtsApiService.get().getQueryServiceIde().getArtifact(item);
               if (useArt.getArtifactType().getMax(attrColumn.getAttributeType()) != 1) {
                  if (useArt.getAttributeCount(attrColumn.getAttributeType()) > 1) {
                     return false;
                  }
               }
               if (useArt.isOfType(AtsArtifactTypes.Action)) {
                  if (AtsApiService.get().getWorkItemService().getTeams(useArt).size() == 1) {
                     useArt = (AbstractWorkflowArtifact) AtsApiService.get().getWorkItemService().getFirstTeam(
                        useArt).getStoreObject();
                  } else {
                     return false;
                  }
               }
               boolean modified = PromptChangeUtil.promptChangeAttribute((AbstractWorkflowArtifact) useArt,
                  attrColumn.getAttributeType(), false);
               if (modified && persist) {
                  useArt.persist("persist attribute via alt-left-click");
               }
               if (modified) {
                  ((XViewer) ((XViewerColumn) columnData).getXViewer()).update(useArt, null);
                  return true;
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean isPersistViewer(XViewer xViewer) {
      return xViewer != null && //
         xViewer instanceof IPersistAltLeftClickProvider //
         && ((IPersistAltLeftClickProvider) xViewer).isAltLeftClickPersist();
   }

}
