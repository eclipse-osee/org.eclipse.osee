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

package org.eclipse.osee.framework.ui.skynet.dialogs;

import java.util.Collection;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactPasteConfiguration;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactPasteSpecialDialog extends TitleAreaDialog {

   private final ArtifactPasteConfiguration config;

   public ArtifactPasteSpecialDialog(Shell parentShell, ArtifactPasteConfiguration config, Artifact destinationArtifact, Collection<Artifact> copiedArtifacts) {
      super(parentShell);
      this.config = config;
      setShellStyle(SWT.RESIZE | getShellStyle());
      setTitle("Artifact Paste Special");
      setTitleImage(ImageManager.getImage(FrameworkImage.PASTE_SPECIAL_WIZ));
      setDefaultImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_PASTE));
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      Composite composite = (Composite) super.createDialogArea(parent);
      getShell().setText("Artifact Paste Special");
      setMessage("Select from artifact paste options below.\nSelect \"Ok\" to perform the paste operation.");

      Composite mainComposite = new Composite(composite, SWT.NONE);
      mainComposite.setLayout(new GridLayout());
      mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Group group = new Group(mainComposite, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginWidth = 10;

      group.setLayout(layout);
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setText("Paste Options");

      Button button = new Button(group, SWT.CHECK);
      button.setText("Include children of copied artifacts");
      button.setSelection(config.isIncludeChildrenOfCopiedElements());
      button.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Object source = e.getSource();
            if (source instanceof Button) {
               Button button = (Button) source;
               config.setIncludeChildrenOfCopiedElements(button.getSelection());
            }
         }
      });
      button = new Button(group, SWT.CHECK);
      button.setText("Keep relation order settings");
      button.setSelection(config.isKeepRelationOrderSettings());
      button.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Object source = e.getSource();
            if (source instanceof Button) {
               Button button = (Button) source;
               config.setKeepRelationOrderSettings(button.getSelection());
            }
         }
      });
      return composite;
   }
}
