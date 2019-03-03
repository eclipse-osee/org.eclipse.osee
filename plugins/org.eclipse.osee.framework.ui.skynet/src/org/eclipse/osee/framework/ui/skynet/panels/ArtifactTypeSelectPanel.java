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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactTypeDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeSelectPanel extends AbstractItemSelectPanel<ArtifactTypeToken> {

   private Collection<ArtifactTypeToken> artifactTypes;
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

   public void setAllowedArtifactTypes(Collection<ArtifactTypeToken> artifactTypes) {
      this.artifactTypes = artifactTypes;
      ArtifactTypeToken currentSelection = getSelected();
      if (!artifactTypes.contains(currentSelection)) {
         this.setSelected(null);
         this.updateCurrentItemWidget();
      }
   }

   @Override
   protected Dialog createSelectDialog(Shell shell, ArtifactTypeToken lastSelected) {
      FilteredTreeArtifactTypeDialog dialog =
         new FilteredTreeArtifactTypeDialog(title, message, this.artifactTypes, new ArtifactTypeLabelProvider());
      dialog.setMultiSelect(false);
      if (lastSelected != null) {
         dialog.setInitialSelections(Arrays.asList(lastSelected));
      }
      try {
         dialog.setInput(artifactTypes);
      } catch (Exception ex) {
         ErrorDialog.openError(shell, title, null, // no special message
            new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Unable to create artifact type selectiong dialog", ex));
      }
      return dialog;
   }

   @Override
   protected boolean updateFromDialogResult(Dialog dialog) {
      boolean wasUpdated = false;
      FilteredTreeArtifactTypeDialog castedDialog = (FilteredTreeArtifactTypeDialog) dialog;
      ArtifactTypeToken artifactType = castedDialog.getSelectedFirst();
      if (artifactType != null) {
         setSelected(artifactType);
         wasUpdated = true;
      }
      return wasUpdated;
   }
}
