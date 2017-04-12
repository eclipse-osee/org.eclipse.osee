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
package org.eclipse.osee.ats.util.xviewer.column;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.column.IPersistAltLeftClickProvider;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IAttributeColumn;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeColumnUtility {

   public static boolean handleAltLeftClick(Object columnData, Object item, boolean multiLineStringAttribute, boolean persist) {
      try {
         if (columnData instanceof IAttributeColumn) {
            IAttributeColumn attrColumn = (IAttributeColumn) columnData;
            // Only prompt change for sole attribute types
            if (AttributeTypeManager.getMaxOccurrences(attrColumn.getAttributeType()) != 1) {
               return false;
            }
            if (item instanceof Artifact) {
               Artifact useArt = (Artifact) item;
               if (useArt.isOfType(AtsArtifactTypes.Action)) {
                  if (AtsClientService.get().getWorkItemService().getTeams(useArt).size() == 1) {
                     useArt = (AbstractWorkflowArtifact) AtsClientService.get().getWorkItemService().getFirstTeam(
                        useArt).getStoreObject();
                  } else {
                     return false;
                  }
               }
               boolean modified = PromptChangeUtil.promptChangeAttribute((AbstractWorkflowArtifact) useArt,
                  attrColumn.getAttributeType(), false, multiLineStringAttribute);
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
