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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDragAndDrop;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class RelationsFormSection extends ArtifactEditorFormSection {

   private RelationsComposite relationComposite;

   public RelationsFormSection(ArtifactEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(editor, parent, toolkit, style);
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      final FormToolkit toolkit = form.getToolkit();

      Section section = getSection();
      section.setText("Relations");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Composite sectionBody = toolkit.createComposite(section, toolkit.getBorderStyle());
      sectionBody.setLayout(ALayout.getZeroMarginLayout());
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Label dragDropLabel = new Label(sectionBody, SWT.BORDER);
      dragDropLabel.setText("Click here to drag this \"" + getEditor().getEditorInput().getArtifact().getArtifactTypeName() + "\"");
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 25;
      dragDropLabel.setLayoutData(gd);
      new ArtifactDragAndDrop(dragDropLabel, getEditor().getEditorInput().getArtifact(), ArtifactEditor.EDITOR_ID);
      toolkit.adapt(dragDropLabel, true, true);

      relationComposite =
            new RelationsComposite(getEditor(),sectionBody, SWT.NONE, getEditor().getEditorInput().getArtifact());
   
      relationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      section.setClient(sectionBody);
      toolkit.paintBordersFor(section);

      relationComposite.getTreeViewer().getTree().addTreeListener(new TreeListener() {

         @Override
         public void treeCollapsed(TreeEvent e) {
            getManagedForm().getForm().getBody().layout();
         }

         @Override
         public void treeExpanded(TreeEvent e) {
            getManagedForm().getForm().getBody().layout();
         }
      });
   }

   public RelationsComposite getRelationComposite() {
      return relationComposite;
   }

   @Override
   public void refresh() {
      super.refresh();
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            if (relationComposite != null && !relationComposite.isDisposed()) {
               relationComposite.refresh();
            }
         }
      });
   }

   @Override
   public void dispose() {
      if (relationComposite != null && !relationComposite.isDisposed()) {
         relationComposite.dispose();
      }
      super.dispose();
   }
}
