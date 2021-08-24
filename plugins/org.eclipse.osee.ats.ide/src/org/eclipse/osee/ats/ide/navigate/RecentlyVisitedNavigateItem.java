/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.core.util.RecentlyVisistedItem;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class RecentlyVisitedNavigateItem extends XNavigateItemAction {

   private final RecentlyVisistedItem item;

   public RecentlyVisitedNavigateItem(RecentlyVisistedItem item) {
      super(item.getIdToken().getName(), FrameworkImage.OPEN, XNavItemCat.TOP);
      ArtifactTypeToken artifactType = item.getArtifactType();
      if (artifactType != null && artifactType.isValid()) {
         oseeImage = ArtifactImageManager.getArtifactTypeImage(artifactType);
      }
      this.item = item;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      // Load artifact to check for deleted
      Artifact workItemArt = (Artifact) AtsApiService.get().getQueryService().getArtifact(item.getIdToken(),
         AtsApiService.get().getAtsBranch(), DeletionFlag.INCLUDE_DELETED);
      if (workItemArt != null) {
         if (workItemArt.isDeleted()) {
            AWorkbench.popup("Item has been deleted.");
            return;
         }
         IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(workItemArt);
         if (workItem == null) {
            AWorkbench.popupf("Item %s can not be found.", item.getIdToken());
            return;
         }
         WorkflowEditor.edit(workItem);
      }
   }

}
