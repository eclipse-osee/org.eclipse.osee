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

package org.eclipse.osee.ats.ide.editor.tab.workflow.section;

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.WfeWorkFlowTab;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeDragAndDrop;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.RelationsFormSection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class WfeRelationsSection extends RelationsFormSection {
   IAtsWorkItem workItem;

   public WfeRelationsSection(WorkflowEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(editor, parent, toolkit, style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, true);
      workItem = editor.getWorkItem();
   }

   @Override
   public WorkflowEditor getEditor() {
      return (WorkflowEditor) super.getEditor();
   }

   @Override
   protected synchronized void createSection(Section section, FormToolkit toolkit) {
      super.createSection(section, toolkit);
      // Don't allow users to see all relations
      if (!AtsApiService.get().getUserService().isAtsAdmin()) {
         getRelationComposite().getTreeViewer().addFilter(userRelationsFilter);
      }
   }

   @Override
   protected void handleExpandAndCollapse() {
      if (getEditor().getSelectedPage() != null) {
         ((WfeWorkFlowTab) getEditor().getSelectedPage()).getManagedForm().getForm().layout();
      }
   }

   @Override
   protected void addDragAndDrop(Control dropArea) {
      new WfeDragAndDrop(dropArea, (AbstractWorkflowArtifact) getEditorInput().getArtifact(), WorkflowEditor.EDITOR_ID);
   }

   private static ViewerFilter userRelationsFilter = new ViewerFilter() {
      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
         if (element instanceof RelationTypeToken) {
            return !filteredRelationTypeNames.contains(((RelationTypeToken) element).getName());
         }
         return true;
      }
   };

   private static List<String> filteredRelationTypeNames = Arrays.asList(
      AtsRelationTypes.ActionToWorkflow_Action.getName(), AtsRelationTypes.TeamWfToTask_TeamWorkflow.getName(),
      AtsRelationTypes.TeamActionableItem_ActionableItem.getName(),
      AtsRelationTypes.TeamWorkflowTargetedForVersion_Version.getName(), AtsRelationTypes.TeamLead_Lead.getName(),
      AtsRelationTypes.TeamMember_Member.getName(), AtsRelationTypes.TeamWorkflowToReview_Review.getName(),
      CoreRelationTypes.DefaultHierarchical_Child.getName(), CoreRelationTypes.Users_Artifact.getName());

   public boolean isDisposed() {
      return getRelationComposite() == null || getRelationComposite().isDisposed();
   }

}
