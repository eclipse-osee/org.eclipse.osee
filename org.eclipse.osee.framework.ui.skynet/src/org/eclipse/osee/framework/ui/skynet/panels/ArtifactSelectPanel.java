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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.dialogs.ArtifactSelectionDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * @author Roberto E. Escobar
 * @author Ryan C. Schmitt
 */
public class ArtifactSelectPanel {

   private Artifact defaultArtifact;
   private Artifact lastSelectedArtifact;
   private Text destinationArtifactText;

   public ArtifactSelectPanel() {
   }

   public void createControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      destinationArtifactText = new Text(composite, SWT.BORDER | SWT.SINGLE);
      destinationArtifactText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Button button = new Button(composite, SWT.PUSH);
      button.setText("Browse");
      button.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               createArtifactSelectDialog();
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
      updateArtifactText();
   }

   private void updateArtifactText() {
      if (Widgets.isAccessible(destinationArtifactText)) {
         Artifact artifact = getArtifact();
         if (artifact == null) {
            artifact = getDefaultArtifact();
         }
         destinationArtifactText.setText(artifact != null ? artifact.toString() : "");
      }
   }

   public void setDefaultArtifact(Artifact artifact) {
      this.defaultArtifact = artifact;
      setArtifact(defaultArtifact);
   }

   public Artifact getDefaultArtifact() {
      return defaultArtifact;
   }

   public void setArtifact(Artifact artifact) {
      this.lastSelectedArtifact = artifact;
   }

   public Artifact getArtifact() {
      return lastSelectedArtifact;
   }

   private void createArtifactSelectDialog() throws OseeCoreException {
      Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();

      ArtifactSelectionDialog dialog = new ArtifactSelectionDialog(shell);
      dialog.setTitle("Select Destination Artifact");
      dialog.setMessage("Select a destination artifact. Imported items will be added as children of the selected artifact.");
      dialog.setImage(ImageManager.getImage(FrameworkImage.ARTIFACT_EXPLORER));
      dialog.setValidator(new ArtifactSelectionStatusValidator());

      Artifact lastSelected = getArtifact();
      if (lastSelected == null) {
         lastSelected = getDefaultArtifact();
      }

      Branch branch = lastSelected != null ? lastSelected.getBranch() : BranchManager.getCommonBranch();
      dialog.setInput(branch);

      if (lastSelected != null) {
         dialog.setInitialSelections(new Object[] {lastSelected});
      }

      int result = dialog.open();
      if (result == Window.OK) {
         Artifact selected = dialog.getFirstResult();
         if (selected != null) {
            setArtifact(selected);
            updateArtifactText();
         }
      }
   }
   private final class ArtifactSelectionStatusValidator implements ISelectionStatusValidator {

      @Override
      public IStatus validate(Object[] selection) {
         IStatus status;
         if (selection == null || selection.length != 1) {
            status =
                  new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, IStatus.ERROR, "Must select (1) artifact", null);
         } else {
            status = new Status(IStatus.OK, SkynetGuiPlugin.PLUGIN_ID, 0, "", null);
         }
         return status;
      }

   }
}
