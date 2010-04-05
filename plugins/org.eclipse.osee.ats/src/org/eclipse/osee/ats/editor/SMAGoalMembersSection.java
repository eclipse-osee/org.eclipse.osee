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
package org.eclipse.osee.ats.editor;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.goal.GoalXViewerFactory;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.world.IWorldEditor;
import org.eclipse.osee.ats.world.IWorldEditorProvider;
import org.eclipse.osee.ats.world.WorldComposite;
import org.eclipse.osee.ats.world.WorldLabelProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDragAndDrop;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class SMAGoalMembersSection extends SectionPart implements IWorldEditor {

   private final SMAEditor editor;
   private WorldComposite worldComposite;

   public SMAGoalMembersSection(SMAEditor editor, Composite parent, XFormToolkit toolkit, int style) {
      super(parent, toolkit, style | Section.TITLE_BAR);
      this.editor = editor;
   }

   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);
      final FormToolkit toolkit = form.getToolkit();

      Section section = getSection();
      section.setText("Members");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Composite sectionBody = toolkit.createComposite(section, toolkit.getBorderStyle());
      sectionBody.setLayout(ALayout.getZeroMarginLayout(2, true));
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      addDropToAddLabel(toolkit, sectionBody);
      addDropToRemoveLabel(toolkit, sectionBody);

      worldComposite = new WorldComposite(this, new GoalXViewerFactory(), sectionBody, SWT.BORDER);
      try {
         CustomizeData customizeData = worldComposite.getCustomizeDataCopy();
         worldComposite.load("Members", editor.getSma().getRelatedArtifacts(AtsRelationTypes.Goal_Member),
               customizeData, TableLoadOption.None);
         ((WorldLabelProvider) worldComposite.getXViewer().getLabelProvider()).setParentGoal((GoalArtifact) editor.getSma());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.horizontalSpan = 2;
      worldComposite.setLayoutData(gd);

      section.setClient(sectionBody);
      toolkit.paintBordersFor(section);
   }

   protected void addDropToAddLabel(FormToolkit toolkit, Composite sectionBody) {
      Label dropToAddLabel = new Label(sectionBody, SWT.BORDER);
      dropToAddLabel.setText(" Drop New Members Here");
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 25;
      dropToAddLabel.setLayoutData(gd);
      toolkit.adapt(dropToAddLabel, true, true);

      new ArtifactDragAndDrop(dropToAddLabel, editor.getSma(), ArtifactEditor.EDITOR_ID) {
         @Override
         public void performArtifactDrop(Artifact[] dropArtifacts) {
            super.performArtifactDrop(dropArtifacts);
            try {
               Collection<Artifact> members = ((GoalArtifact) editor.getSma()).getMembers();
               for (Artifact art : dropArtifacts) {
                  if (!members.contains(art)) {
                     editor.getSma().addRelation(AtsRelationTypes.Goal_Member, art);
                  }
               }
               editor.onDirtied();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
   }

   protected void addDropToRemoveLabel(FormToolkit toolkit, Composite sectionBody) {
      Label dropToAddLabel = new Label(sectionBody, SWT.BORDER);
      dropToAddLabel.setText(" Drop Members to Remove");
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 25;
      dropToAddLabel.setLayoutData(gd);
      toolkit.adapt(dropToAddLabel, true, true);

      new ArtifactDragAndDrop(dropToAddLabel, editor.getSma(), ArtifactEditor.EDITOR_ID) {
         @Override
         public void performArtifactDrop(Artifact[] dropArtifacts) {
            super.performArtifactDrop(dropArtifacts);
            try {
               for (Artifact art : dropArtifacts) {
                  editor.getSma().deleteRelation(AtsRelationTypes.Goal_Member, art);
               }
               editor.onDirtied();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
   }

   @Override
   public void refresh() {
      super.refresh();
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            if (worldComposite != null && !worldComposite.isDisposed()) {
               worldComposite.getXViewer().refresh();
            }
         }
      });
   }

   @Override
   public void dispose() {
      if (worldComposite != null && !worldComposite.isDisposed()) {
         worldComposite.dispose();
      }
      super.dispose();
   }

   @Override
   public void createToolBarPulldown(Menu menu) {
   }

   @Override
   public String getCurrentTitleLabel() {
      return "";
   }

   @Override
   public IActionable getIActionable() {
      return null;
   }

   @Override
   public IWorldEditorProvider getWorldEditorProvider() {
      return null;
   }

   @Override
   public void reSearch() throws OseeCoreException {
   }

   @Override
   public void reflow() {
   }

   @Override
   public void setTableTitle(String title, boolean warning) {
   }

}
