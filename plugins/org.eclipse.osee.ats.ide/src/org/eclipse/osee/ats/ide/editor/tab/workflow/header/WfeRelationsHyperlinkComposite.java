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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import static org.eclipse.osee.ats.api.data.AtsRelationTypes.Derive_From;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.Derive_To;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamWorkflowToReview_Review;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamWorkflowToReview_TeamWorkflow;
import static org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeRelationsHyperlinkComposite.OpenType.Delete;
import static org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeRelationsHyperlinkComposite.OpenType.Edit;
import static org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeRelationsHyperlinkComposite.OpenType.Open;
import static org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeRelationsHyperlinkComposite.OpenType.Read;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Dependency_Artifact;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Dependency_Dependency;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Supercedes_SupercededBy;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Supercedes_Supercedes;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.SupportingInfo_IsSupportedBy;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.SupportingInfo_SupportingInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeRelationsHyperlinkComposite extends Composite {

   private static RelationTypeSide[] sides = new RelationTypeSide[] {
      AtsRelationTypes.TeamWorkflowToReview_Review,
      AtsRelationTypes.TeamWorkflowToReview_TeamWorkflow,
      CoreRelationTypes.Supercedes_SupercededBy,
      CoreRelationTypes.Supercedes_Supercedes,
      CoreRelationTypes.SupportingInfo_IsSupportedBy,
      CoreRelationTypes.SupportingInfo_SupportingInfo,
      AtsRelationTypes.Derive_From,
      AtsRelationTypes.Derive_To,
      CoreRelationTypes.Dependency_Artifact,
      CoreRelationTypes.Dependency_Dependency};
   private final WorkflowEditor editor;
   private final IAtsWorkItem workItem;
   private final HashCollection<Long, Hyperlink> relIdToHyperlink = new HashCollection<>();
   private final Map<Long, Label> relIdToLabel = new HashMap<>();
   private final Set<Long> existingRels = new HashSet<>();

   public WfeRelationsHyperlinkComposite(Composite parent, int style, WorkflowEditor editor) {
      super(parent, style);
      this.editor = editor;
      this.workItem = editor.getWorkItem();
   }

   public void create() {
      setLayout(ALayout.getZeroMarginLayout(1, false));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 300;
      setLayoutData(gd);
      editor.getToolkit().adapt(this);

      createUpdateLinks();
   }

   private void createUpdateLinks() {
      AbstractWorkflowArtifact workItemArt = (AbstractWorkflowArtifact) workItem;
      existingRels.addAll(relIdToHyperlink.keySet());

      // Create all hyperlinks from this artifact to others of interest
      if (workItemArt.isTeamWorkflow() && workItemArt.getWorkDefinition().getHeaderDef().isShowSiblingLinks()) {
         for (RelationLink relation : ((Artifact) workItemArt.getParentAction()).getRelations(
            AtsRelationTypes.ActionToWorkflow_TeamWorkflow)) {
            if (!relation.getArtifactB().equals(workItemArt)) {
               if (existingRels.contains(relation.getId())) {
                  existingRels.remove(relation.getId());
               } else {
                  createLink(workItemArt, " has sibling ", relation.getArtifactB(),
                     AtsRelationTypes.ActionToWorkflow_TeamWorkflow, relation, OpenType.Open);
               }
            }
         }
      }
      createLink(workItemArt, "is reviewed by", TeamWorkflowToReview_Review, Open);
      createLink(workItemArt, "reviews", TeamWorkflowToReview_TeamWorkflow, Open);

      createLink(workItemArt, "is superceded by", Supercedes_SupercededBy, Open);
      createLink(workItemArt, "supercedes", Supercedes_Supercedes, Open);

      createLink(workItemArt, "depends on", Dependency_Dependency, Open);
      createLink(workItemArt, "is dependency of", Dependency_Artifact, Open);

      createLink(workItemArt, "is derived from", Derive_From, Open);
      createLink(workItemArt, "derived", Derive_To, Open);

      createLink(workItemArt, "is supported info for", SupportingInfo_IsSupportedBy, Read, Edit, Delete);
      createLink(workItemArt, "has supporting info", SupportingInfo_SupportingInfo, Read, Edit, Delete);

      if (!existingRels.isEmpty()) {
         removeRelations(existingRels);
      }
      layout(true, true);
      getParent().layout(true, true);
      editor.getWorkFlowTab().getManagedForm().reflow(true);
   }

   public void refresh() {
      if (Widgets.isAccessible(this)) {
         createUpdateLinks();
      }
   }

   private void removeRelations(final Set<Long> existingRels) {
      final Composite fComp = this;

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (Widgets.isAccessible(fComp) && relIdToHyperlink != null) {
               for (Long relationId : existingRels) {
                  if (relIdToHyperlink.getValues(relationId) == null) {
                     continue;
                  }
                  for (Hyperlink link : relIdToHyperlink.getValues(relationId)) {
                     if (link != null) {
                        link.dispose();
                     }
                  }
                  relIdToHyperlink.removeValues(relationId);
                  Label label = relIdToLabel.get(relationId);
                  if (label != null) {
                     label.dispose();
                  }
                  relIdToLabel.remove(relationId);
               }
            }
         }
      });
   }

   public static boolean relationExists(AbstractWorkflowArtifact workItem) {
      if (workItem.isTeamWorkflow()) {
         boolean siblings = ((IAtsTeamWorkflow) workItem).getParentAction().getTeamWorkflows().size() > 1;
         if (siblings) {
            return true;
         }
      }
      for (RelationTypeSide side : sides) {
         if (workItem.getRelatedArtifacts(side).size() > 0) {
            return true;
         }
      }
      if (workItem instanceof AbstractReviewArtifact && AtsApiService.get().getActionableItemService().hasActionableItems(
         workItem)) {
         return true;
      }
      return false;
   }

   private String getCompletedCancelledString(Artifact art) {
      if (art instanceof AbstractWorkflowArtifact && ((AbstractWorkflowArtifact) art).isCompletedOrCancelled()) {
         return " " + ((AbstractWorkflowArtifact) art).getStateMgr().getCurrentStateName() + " ";
      }
      return "";
   }

   public void createLink(Artifact thisArt, String action, RelationTypeSide relationSide, OpenType... openType) {
      for (final RelationLink relation : thisArt.getRelations(relationSide)) {
         if (existingRels.contains(relation.getId())) {
            existingRels.remove(relation.getId());
         } else {
            Artifact thatArt = relation.getArtifactA();
            if (relation.getArtifactA().equals(thisArt)) {
               thatArt = relation.getArtifactB();
            }
            createLink(thisArt, action, thatArt, relationSide, relation, openType);
         }
      }
   }

   /**
    * @param relationSide or null if sibling relation
    */
   private void createLink(Artifact thisArt, String action, final Artifact thatArt, RelationTypeSide relationSide, RelationLink relation, OpenType... openType) {
      final Composite fComp = this;
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            try {
               Composite lComp = new Composite(fComp, SWT.NONE);
               lComp.setLayout(ALayout.getZeroMarginLayout(4, false));
               GridData gd = new GridData(GridData.FILL_HORIZONTAL);
               gd.widthHint = 500;
               lComp.setLayoutData(gd);
               editor.getToolkit().adapt(lComp);
               lComp.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));

               String labelStr = "This " + action + getCompletedCancelledString(thatArt) + " \"" + getObjectTeamOrType(
                  thatArt) + "\" " + thatArt.toStringWithId(60);

               Label label = editor.getToolkit().createLabel(lComp, labelStr);

               Set<OpenType> openTypes = Collections.asHashSet(openType);

               if (openTypes.contains(OpenType.Read)) {
                  Hyperlink link = createReadHyperlink(thisArt, thatArt, lComp, editor, OpenType.Read.name());
                  relIdToLabel.put(Long.valueOf(relation.getId()), label);
                  relIdToHyperlink.put(Long.valueOf(relation.getId()), link);

               }
               if (openTypes.contains(OpenType.Open)) {
                  Hyperlink link = createReadHyperlink(thisArt, thatArt, lComp, editor, OpenType.Open.name());
                  relIdToLabel.put(Long.valueOf(relation.getId()), label);
                  relIdToHyperlink.put(Long.valueOf(relation.getId()), link);

               }
               if (openTypes.contains(OpenType.Edit)) {
                  Hyperlink link = createEditHyperlink(thatArt, lComp, editor);
                  relIdToHyperlink.put(Long.valueOf(relation.getId()), link);
                  relIdToLabel.put(Long.valueOf(relation.getId()), label);
               }
               if (openTypes.contains(OpenType.Delete)) {
                  Hyperlink link = createDeleteHyperlink(thisArt, thatArt, relation, lComp, editor);
                  relIdToHyperlink.put(Long.valueOf(relation.getId()), link);
                  relIdToLabel.put(Long.valueOf(relation.getId()), label);

               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }

      });
   }

   private String getObjectTeamOrType(Artifact art) {
      if (art instanceof IAtsTeamWorkflow) {
         return ((IAtsTeamWorkflow) art).getTeamDefinition().getName();
      } else {
         return art.getArtifactTypeName();
      }
   }

   public static enum OpenType {
      Open,
      Read,
      Edit,
      Delete,
      ArtifactEditor;
   }

   public static Hyperlink createDeleteHyperlink(Artifact thisArt, final Artifact thatArt, final RelationLink relation, Composite lComp, WorkflowEditor editor) {
      Hyperlink link = editor.getToolkit().createHyperlink(lComp, "Delete", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         @Override
         public void linkEntered(HyperlinkEvent e) {
            // do nothing
         }

         @Override
         public void linkExited(HyperlinkEvent e) {
            // do nothing
         }

         @Override
         public void linkActivated(HyperlinkEvent e) {
            Artifact delArt = thatArt;
            if (thatArt instanceof IAtsWorkItem) {
               delArt = thisArt;
            }

            Collection<ArtifactToken> related = Arrays.asList(thatArt);
            if (thatArt instanceof IAtsObject) {
               if (MessageDialog.openConfirm(Displays.getActiveShell(), "Delete Related",
                  "You do not have permissions to delete ATS Objects.  Would you Like to Un-Relate?")) {
                  IAtsChangeSet changes = AtsApiService.get().createChangeSet("Delete Relation");
                  changes.deleteRelation(relation);
                  changes.execute();
               }
               return;
            }
            XResultData results =
               AtsApiService.get().getAccessControlService().isDeleteable(related, new XResultData());
            if (results.isErrors()) {
               AWorkbench.popup(results.toString());
               return;
            }
            if (MessageDialog.openConfirm(Displays.getActiveShell(), "Delete Related",
               "Are you sure you want to delete related artifact\n\n" + delArt.toStringWithId() + " ?")) {
               IAtsChangeSet changes = AtsApiService.get().createChangeSet("Delete Related Artifact");
               changes.deleteArtifact(delArt);
               changes.execute();
            }
         }
      });
      return link;
   }

   public static Hyperlink createEditHyperlink(final Artifact thatArt, Composite lComp, WorkflowEditor editor) {
      Hyperlink link = editor.getToolkit().createHyperlink(lComp, "Edit", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         @Override
         public void linkEntered(HyperlinkEvent e) {
            // do nothing
         }

         @Override
         public void linkExited(HyperlinkEvent e) {
            // do nothing
         }

         @Override
         public void linkActivated(HyperlinkEvent e) {
            if (AtsObjects.isAtsWorkItemOrAction(thatArt)) {
               AtsEditors.openATSAction(thatArt, AtsOpenOption.OpenOneOrPopupSelect);
            } else {
               try {
                  RendererManager.open(thatArt, PresentationType.SPECIALIZED_EDIT);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
      return link;
   }

   public static Hyperlink createReadHyperlink(Artifact thisArt, final Artifact thatArt, Composite lComp, WorkflowEditor editor, String label) {
      Hyperlink link = editor.getToolkit().createHyperlink(lComp, label, SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         @Override
         public void linkEntered(HyperlinkEvent e) {
            // do nothing
         }

         @Override
         public void linkExited(HyperlinkEvent e) {
            // do nothing
         }

         @Override
         public void linkActivated(HyperlinkEvent e) {
            if (AtsObjects.isAtsWorkItemOrAction(thatArt)) {
               AtsEditors.openATSAction(thatArt, AtsOpenOption.OpenOneOrPopupSelect);
            } else {
               try {
                  RendererManager.open(thatArt, PresentationType.DEFAULT_OPEN);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
      return link;
   }

}
