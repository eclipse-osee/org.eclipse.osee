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

import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.pages.ArtifactFormPage;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDragAndDrop;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class RelationsFormSection extends ArtifactEditorFormSection {

   private RelationsComposite relationComposite;
   private boolean sectionCreated = false;
   private final boolean expandOnCreate;

   public RelationsFormSection(AbstractArtifactEditor editor, Composite parent, FormToolkit toolkit, int style, boolean expandOnCreate) {
      super(editor, parent, toolkit, style);
      this.expandOnCreate = expandOnCreate;
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      final FormToolkit toolkit = form.getToolkit();

      final Section section = getSection();
      section.setText("Relations");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      if (expandOnCreate) {
         createSection(section, toolkit);
      } else {
         // Only load when users selects section
         section.addListener(SWT.Activate, new Listener() {

            @Override
            public void handleEvent(Event e) {
               createSection(section, toolkit);
            }
         });
      }
   }

   protected synchronized void createSection(Section section, FormToolkit toolkit) {
      if (sectionCreated) {
         return;
      }
      Composite sectionBody = toolkit.createComposite(section, toolkit.getBorderStyle());
      sectionBody.setLayout(ALayout.getZeroMarginLayout());
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Label dragDropLabel = new Label(sectionBody, SWT.BORDER);
      dragDropLabel.setText("Click here to drag this \"" + getEditorInput().getArtifact().getArtifactTypeName() + "\"");
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 25;
      dragDropLabel.setLayoutData(gd);
      addDragAndDrop(dragDropLabel);
      toolkit.adapt(dragDropLabel, true, true);

      relationComposite = new RelationsComposite(getEditor(), sectionBody, SWT.BORDER, getEditorInput().getArtifact());
      relationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      section.setClient(sectionBody);
      toolkit.paintBordersFor(section);

      HelpUtil.setHelp(sectionBody, OseeHelpContext.ARTIFACT_EDITOR__RELATIONS);

      relationComposite.getTreeViewer().getTree().addMouseListener(new MouseListener() {

         private void redrawPage() {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  relationComposite.layout();
                  relationComposite.getParent().layout();
                  getManagedForm().reflow(true);
               }
            });
         }

         @Override
         public void mouseDoubleClick(MouseEvent e) {
            // do nothing
         }

         @Override
         public void mouseDown(MouseEvent e) {
            redrawPage();
         }

         @Override
         public void mouseUp(MouseEvent e) {
            // do nothing
         }
      });
      sectionCreated = true;
   }

   protected void handleExpandAndCollapse() {
      ((ArtifactFormPage) getEditor().getSelectedPage()).refresh();
   }

   protected void addDragAndDrop(Control dropArea) {
      new ArtifactDragAndDrop(dropArea, getEditorInput().getArtifact(), ArtifactEditor.EDITOR_ID);
   }

   public RelationsComposite getRelationComposite() {
      return relationComposite;
   }

   @Override
   public void refresh() {
      super.refresh();
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(relationComposite)) {
               relationComposite.refresh();
            }
         }
      });
   }

   @Override
   public void dispose() {
      if (Widgets.isAccessible(relationComposite)) {
         relationComposite.dispose();
      }
      super.dispose();
   }
}
