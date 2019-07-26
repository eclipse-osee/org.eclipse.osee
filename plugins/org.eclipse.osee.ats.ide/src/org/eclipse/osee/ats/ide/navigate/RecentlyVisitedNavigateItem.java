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
      super(parent, item.getIdToken().getName(),
         ArtifactImageManager.getArtifactTypeImage(
            item.getTypeToken()) != null ? ArtifactImageManager.getArtifactTypeImage(
               item.getTypeToken()) : FrameworkImage.OPEN);
      this.item = item;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      IAtsWorkItem workItem = AtsClientService.get().getWorkItemService().getWorkItem(item.getIdToken());
      if (workItem != null && !AtsClientService.get().getStoreService().isDeleted(workItem)) {
         WorkflowEditor.edit(workItem);
      }
   }

}
