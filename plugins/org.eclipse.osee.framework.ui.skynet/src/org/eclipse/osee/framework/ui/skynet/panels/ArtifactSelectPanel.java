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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.dialogs.ArtifactSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactSelectPanel extends AbstractItemSelectPanel<Artifact> {

   private String title;
   private String message;

   public ArtifactSelectPanel() {
      super(new ArtifactLabelProvider(), new ArrayContentProvider());
      this.title = "";
      this.message = "";
   }

   public void setDialogTitle(String title) {
      this.title = title;
   }

   public void setDialogMessage(String message) {
      this.message = message;
   }

   @Override
   protected Dialog createSelectDialog(Shell shell, Artifact lastSelected)  {
      ArtifactSelectionDialog dialog = new ArtifactSelectionDialog(shell);
      dialog.setTitle(title);
      dialog.setMessage(message);
      dialog.setImage(ImageManager.getImage(FrameworkImage.ARTIFACT_EXPLORER));
      dialog.setValidator(new SingleSelectionStatusValidator());
      BranchId branch = lastSelected != null ? lastSelected.getBranch() : COMMON;
      dialog.setInput(branch);
      if (lastSelected != null) {
         dialog.setInitialSelections(new Object[] {lastSelected});
      }
      return dialog;
   }

   @Override
   protected boolean updateFromDialogResult(Dialog dialog) {
      boolean wasUpdated = false;
      ArtifactSelectionDialog castedDialog = (ArtifactSelectionDialog) dialog;
      Artifact artifact = castedDialog.getFirstResult();
      if (artifact != null) {
         setSelected(artifact);
         wasUpdated = true;
      }
      return wasUpdated;
   }

   private final class SingleSelectionStatusValidator implements ISelectionStatusValidator {

      @Override
      public IStatus validate(Object[] selection) {
         IStatus status;
         if (selection == null || selection.length != 1) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, "Must select 1 item", null);
         } else {
            status = new Status(IStatus.OK, Activator.PLUGIN_ID, 0, "", null);
         }
         return status;
      }
   }
}
