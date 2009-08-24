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

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class SMARelationsSection extends SectionPart {

   private RelationsComposite relationComposite;
   private final SMAEditor editor;

   public SMARelationsSection(SMAEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style | Section.TWISTIE | Section.TITLE_BAR);
      this.editor = editor;
   }

   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);
      final FormToolkit toolkit = form.getToolkit();

      Section section = getSection();
      section.setText("Relations");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      final Composite sectionBody = toolkit.createComposite(section, toolkit.getBorderStyle());
      sectionBody.setLayout(ALayout.getZeroMarginLayout(1, false));
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      try {
         Label dragDropLabel = new Label(sectionBody, SWT.BORDER);
         dragDropLabel.setText("Click here to drag this \"" + editor.getSmaMgr().getSma().getArtifactTypeName() + "\"");
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.heightHint = 25;
         dragDropLabel.setLayoutData(gd);
         new SMADragAndDrop(dragDropLabel, editor.getSmaMgr().getSma(), SMAEditor.EDITOR_ID);
         toolkit.adapt(dragDropLabel, true, true);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }

      try {
         relationComposite = new RelationsComposite(editor, sectionBody, SWT.NONE, editor.getSmaMgr().getSma());
         relationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }

      // Don't allow users to see all relations
      if (!AtsUtil.isAtsAdmin()) {
         relationComposite.getTreeViewer().addFilter(userRelationsFilter);
      }

      section.setClient(sectionBody);
      toolkit.paintBordersFor(section);

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

   private static ViewerFilter userRelationsFilter = new ViewerFilter() {
      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
         if (element instanceof RelationType) {
            return !filteredRelationTypeNames.contains(((RelationType) element).getTypeName());
         }
         return true;
      }
   };

   private static List<String> filteredRelationTypeNames =
         Arrays.asList(AtsRelation.ActionToWorkflow_Action.getTypeName(), AtsRelation.SmaToTask_Sma.getTypeName(),
               AtsRelation.TeamActionableItem_ActionableItem.getTypeName(),
               AtsRelation.TeamWorkflowTargetedForVersion_Version.getTypeName(),
               AtsRelation.TeamLead_Lead.getTypeName(), AtsRelation.TeamMember_Member.getTypeName(),
               AtsRelation.TeamWorkflowToReview_Review.getTypeName(), AtsRelation.WorkItem__Child.getTypeName(),
               CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD.getTypeName(),
               CoreRelationEnumeration.Users_Artifact.getTypeName());

}
