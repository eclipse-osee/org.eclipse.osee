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
package org.eclipse.osee.ats.ide.navigate;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.core.util.RecentlyVisistedItem;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class RecentlyVisitedNavigateItem extends XNavigateItemAction {

   private final RecentlyVisistedItem item;

   public RecentlyVisitedNavigateItem(XNavigateItem parent, RecentlyVisistedItem item) {
      super(parent, item.getIdToken().getName(), FrameworkImage.OPEN);
      ArtifactTypeId id = item.getTypeId();
      if (id != null) {
         ArtifactTypeToken type = ArtifactTypeManager.getType(id);
         if (type.isValid()) {
            oseeImage = ArtifactImageManager.getArtifactTypeImage(type);
         }
      }
      this.item = item;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      // Load artifact to check for deleted
      Artifact workItemArt = (Artifact) AtsClientService.get().getQueryService().getArtifact(item.getIdToken(),
         AtsClientService.get().getAtsBranch(), DeletionFlag.INCLUDE_DELETED);
      if (workItemArt != null) {
         if (workItemArt.isDeleted()) {
            AWorkbench.popup("Item has been deleted.");
            return;
         }
         IAtsWorkItem workItem = AtsClientService.get().getWorkItemService().getWorkItem(workItemArt);
         if (workItem == null) {
            AWorkbench.popupf("Item %s can not be found.", item.getIdToken());
            return;
         }
         WorkflowEditor.edit(workItem);
      }
   }

}
