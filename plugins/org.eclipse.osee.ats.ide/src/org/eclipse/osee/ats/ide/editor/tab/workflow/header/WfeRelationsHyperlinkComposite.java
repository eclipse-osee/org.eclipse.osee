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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.event.IWfeEventHandle;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
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
public class WfeRelationsHyperlinkComposite extends Composite implements IWfeEventHandle {

   private static RelationTypeSide[] sides = new RelationTypeSide[] {
      AtsRelationTypes.TeamWorkflowToReview_Review,
      AtsRelationTypes.TeamWorkflowToReview_TeamWorkflow,
      CoreRelationTypes.Supercedes_SupercededBy,
      CoreRelationTypes.Supercedes_Supercedes,
      CoreRelationTypes.SupportingInfo_IsSupportedBy,
      CoreRelationTypes.SupportingInfo_SupportingInfo,
      AtsRelationTypes.Derive_From,
      AtsRelationTypes.Derive_To,
      CoreRelationTypes.SupportingInfo_SupportingInfo,
      CoreRelationTypes.Dependency_Artifact,
      CoreRelationTypes.Dependency_Dependency};
   private final WorkflowEditor editor;
   private final IAtsWorkItem workItem;
   private final Map<Long, Hyperlink> relIdToHyperlink = new HashMap<>();
   private final Map<Long, Label> relIdToLabel = new HashMap<>();
   private final Set<Long> existingRels = new HashSet<>();

   public WfeRelationsHyperlinkComposite(Composite parent, int style, WorkflowEditor editor) {
      super(parent, style);
      this.editor = editor;
      this.workItem = editor.getWorkItem();
   }

   public void create() {
      setLayout(ALayout.getZeroMarginLayout(2, false));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 500;
      setLayoutData(gd);
      editor.getToolkit().adapt(this);

      createUpdateLinks();

      editor.registerEvent(this, sides);
   }

   private void createUpdateLinks() {
      AbstractWorkflowArtifact workItemArt = (AbstractWorkflowArtifact) workItem;
      existingRels.addAll(relIdToHyperlink.keySet());

      // Create all hyperlinks from this artifact to others of interest
      if (workItemArt.isTeamWorkflow() && workItemArt.getWorkDefinition().getHeaderDef().isShowSiblingLinks()) {
         for (RelationLink relation : ((Artifact) workItemArt.getParentAction()).getRelations(
            AtsRelationTypes.ActionToWorkflow_TeamWorkflow)) {
            if (!relation.getArtifactB().equals(workItemArt)) {
               if (existingRels.contains(relation)) {
                  existingRels.remove(relation);
               } else {
                  createLink("This", workItemArt, " has sibling ", relation.getArtifactB(),
                     AtsRelationTypes.ActionToWorkflow_TeamWorkflow, relation);
                  editor.registerEvent(this, relation.getArtifactB());
               }
            }
         }
      }
      createArtifactRelationHyperlinks("This", workItemArt, "is reviewed by",
         AtsRelationTypes.TeamWorkflowToReview_Review);
      createArtifactRelationHyperlinks("This", workItemArt, "reviews",
         AtsRelationTypes.TeamWorkflowToReview_TeamWorkflow);
      createArtifactRelationHyperlinks("This", workItemArt, "is superceded by",
         CoreRelationTypes.Supercedes_SupercededBy);
      createArtifactRelationHyperlinks("This", workItemArt, "supercedes", CoreRelationTypes.Supercedes_Supercedes);
      createArtifactRelationHyperlinks("This", workItemArt, "depends on", CoreRelationTypes.Dependency_Dependency);
      createArtifactRelationHyperlinks("This", workItemArt, "is dependency of", CoreRelationTypes.Dependency_Artifact);

      createArtifactRelationHyperlinks("This", workItemArt, "is derived from", AtsRelationTypes.Derive_From);
      createArtifactRelationHyperlinks("This", workItemArt, "derived", AtsRelationTypes.Derive_To);

      createArtifactRelationHyperlinks("This", workItemArt, "is supported info for",
         CoreRelationTypes.SupportingInfo_IsSupportedBy);
      createArtifactRelationHyperlinks("This", workItemArt, "has supporting info",
         CoreRelationTypes.SupportingInfo_SupportingInfo);

      if (!existingRels.isEmpty()) {
         removeRelations(existingRels);
      }
      layout(true, true);
      getParent().layout(true, true);
      editor.getWorkFlowTab().getManagedForm().reflow(true);
   }

   @Override
   public void refresh() {
      createUpdateLinks();
   }

   private void removeRelations(final Set<Long> existingRels) {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            for (Long relationId : existingRels) {
               Hyperlink link = relIdToHyperlink.get(relationId);
               if (link != null) {
                  link.dispose();
               }
               relIdToHyperlink.remove(relationId);
               Label label = relIdToLabel.get(relationId);
               if (link != null) {
                  label.dispose();
               }
               relIdToLabel.remove(relationId);
            }
         }
      });
   }

   public static boolean relationExists(AbstractWorkflowArtifact workItem) {
      for (RelationTypeSide side : sides) {
         if (workItem.getRelatedArtifacts(side).size() > 0) {
            return true;
         }
      }
      if (workItem instanceof AbstractReviewArtifact && AtsClientService.get().getActionableItemService().hasActionableItems(
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

   private void createArtifactRelationHyperlinks(String prefix, Artifact thisArt, String action, RelationTypeSide relationSide) {
      for (final RelationLink relation : thisArt.getRelations(relationSide)) {
         if (existingRels.contains(relation)) {
            existingRels.remove(relation);
         } else {
            Artifact thatArt = relation.getArtifactA();
            if (relation.getArtifactA().equals(thisArt)) {
               thatArt = relation.getArtifactB();
            }
            createLink(prefix, thisArt, action, thatArt, relationSide, relation);
         }
      }
   }

   /**
    * @param relationSide or null if sibling relation
    */
   private void createLink(String prefix, Artifact thisArt, String action, final Artifact thatArt, RelationTypeSide relationSide, RelationLink relation) {
      final Composite fComp = this;
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            try {
               Label label = editor.getToolkit().createLabel(fComp,
                  prefix + " \"" + getObjectName(thisArt) + "\" " + action + getCompletedCancelledString(
                     thatArt) + " \"" + getObjectName(thatArt) + "\" ");
               Hyperlink link = editor.getToolkit().createHyperlink(fComp,
                  String.format("\"%s\" - %s",
                     thatArt.getName().length() < 60 ? thatArt.getName() : thatArt.getName().substring(0, 60),
                     AtsClientService.get().getAtsId(thatArt)),
                  SWT.NONE);
               relIdToHyperlink.put(Long.valueOf(relation.getId()), link);
               relIdToLabel.put(Long.valueOf(relation.getId()), label);
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
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });
   }

   private String getObjectName(Artifact art) {
      if (art instanceof IAtsTeamWorkflow) {
         return ((IAtsTeamWorkflow) art).getTeamDefinition().getName();
      } else {
         return art.getArtifactTypeName();
      }
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return workItem;
   }

}
