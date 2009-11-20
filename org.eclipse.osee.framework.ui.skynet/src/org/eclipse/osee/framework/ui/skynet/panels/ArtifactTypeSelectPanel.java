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
package org.eclipse.osee.framework.ui.skynet.panels;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTypeFilteredTreeDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeSelectPanel extends AbstractItemSelectPanel<ArtifactType> {

   private Collection<ArtifactType> artifactTypes;
   private String title;
   private String message;

   public ArtifactTypeSelectPanel() {
      super(new ArtifactTypeLabelProvider(), new ArrayContentProvider());
      this.title = "";
      this.message = "";
   }

   public void setDialogTitle(String title) {
      this.title = title;
   }

   public void setDialogMessage(String message) {
      this.message = message;
   }

   public void setAllowedArtifactTypes(Collection<ArtifactType> artifactTypes) {
      this.artifactTypes = artifactTypes;
      ArtifactType currentSelection = getSelected();
      if (!artifactTypes.contains(currentSelection)) {
         this.setSelected(null);
         this.updateCurrentItemWidget();
      }
   }

   @Override
   protected Dialog createSelectDialog(Shell shell, ArtifactType lastSelected) throws OseeCoreException {
      ArtifactTypeFilteredTreeDialog dialog = new ArtifactTypeFilteredTreeDialog(title, message);
      dialog.setMultiSelect(false);
      if (lastSelected != null) {
         dialog.setInitialSelections(Arrays.asList(lastSelected));
      }
      try {
         dialog.setInput(artifactTypes);
      } catch (Exception ex) {
         ErrorDialog.openError(shell, title, null, // no special message
               new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, 0,
                     "Unable to create artifact type selectiong dialog", ex));
      }
      return dialog;
   }

   @Override
   protected boolean updateFromDialogResult(Dialog dialog) {
      boolean wasUpdated = false;
      ArtifactTypeFilteredTreeDialog castedDialog = (ArtifactTypeFilteredTreeDialog) dialog;
      ArtifactType artifactType = castedDialog.getSelection();
      if (artifactType != null) {
         setSelected(artifactType);
         wasUpdated = true;
      }
      return wasUpdated;
   }
}
