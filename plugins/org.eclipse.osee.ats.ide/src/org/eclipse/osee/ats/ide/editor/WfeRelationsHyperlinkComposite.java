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

package org.eclipse.osee.ats.ide.editor;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
      AtsRelationTypes.TeamWorkflowToReview_Team,
      CoreRelationTypes.Supercedes_Superceded,
      CoreRelationTypes.Supercedes_Supercedes,
      CoreRelationTypes.SupportingInfo_SupportedBy,
      CoreRelationTypes.SupportingInfo_SupportingInfo,
      AtsRelationTypes.Derive_From,
      AtsRelationTypes.Derive_To,
      CoreRelationTypes.SupportingInfo_SupportingInfo,
      CoreRelationTypes.Dependency__Artifact,
      CoreRelationTypes.Dependency__Dependency};
   private final WorkflowEditor editor;
   private final IAtsWorkItem workItem;
   private final CompositeKeyHashMap<Artifact, RelationTypeSide, Hyperlink> artAndRelToHyperlink =
      new CompositeKeyHashMap<Artifact, RelationTypeSide, Hyperlink>();
   private final CompositeKeyHashMap<Artifact, RelationTypeSide, Label> artAndRelToLabel =
      new CompositeKeyHashMap<Artifact, RelationTypeSide, Label>();

   public WfeRelationsHyperlinkComposite(Composite parent, int style, WorkflowEditor editor) {
      super(parent, style);
      this.editor = editor;
      this.workItem = editor.getWorkItem();
   }

   public void create(AbstractWorkflowArtifact workItem) {
      setLayout(ALayout.getZeroMarginLayout(2, false));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 500;
      setLayoutData(gd);
      editor.getToolkit().adapt(this);

      // Create all hyperlinks from this artifact to others of interest
      if (workItem.isTeamWorkflow()) {
         for (IAtsTeamWorkflow teamWf : workItem.getParentAction().getTeamWorkflows()) {
            if (!teamWf.equals(workItem)) {
               createLink("This", AtsClientService.get().getQueryServiceClient().getArtifact(teamWf), " has sibling ",
                  workItem, null);
               if (teamWf instanceof Artifact) {
                  editor.registerEvent(this, (Artifact) teamWf);
               }
            }
         }
      }
      createArtifactRelationHyperlinks("This", workItem, "is reviewed by",
         AtsRelationTypes.TeamWorkflowToReview_Review);
      createArtifactRelationHyperlinks("This", workItem, "reviews", AtsRelationTypes.TeamWorkflowToReview_Team);
      createArtifactRelationHyperlinks("This", workItem, "is superceded by", CoreRelationTypes.Supercedes_Superceded);
      createArtifactRelationHyperlinks("This", workItem, "supercedes", CoreRelationTypes.Supercedes_Supercedes);
      createArtifactRelationHyperlinks("This", workItem, "depends on", CoreRelationTypes.Dependency__Dependency);
      createArtifactRelationHyperlinks("This", workItem, "is dependency of", CoreRelationTypes.Dependency__Artifact);

      createArtifactRelationHyperlinks("This", workItem, "is derived from", AtsRelationTypes.Derive_From);
      createArtifactRelationHyperlinks("This", workItem, "derived", AtsRelationTypes.Derive_To);

      createArtifactRelationHyperlinks("This", workItem, "is supported info for",
         CoreRelationTypes.SupportingInfo_SupportedBy);
      createArtifactRelationHyperlinks("This", workItem, "has supporting info",
         CoreRelationTypes.SupportingInfo_SupportingInfo);

      editor.registerEvent(this, sides);
   }

   @Override
   public void refresh() {
      boolean found = false;
      for (Pair<Artifact, RelationTypeSide> keyPair : artAndRelToHyperlink.keySet()) {
         Artifact artifact = keyPair.getFirst();
         RelationTypeSide relationTypeSide = keyPair.getSecond();
         boolean needDel = false;
         if (relationTypeSide == null) {
            if (artifact.isDeleted()) {
               needDel = true;
            }
         } else {
            needDel = !AtsClientService.get().getRelationResolver().areRelated(workItem.getStoreObject(),
               relationTypeSide, artifact);
         }
         if (needDel) {
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  Hyperlink link = artAndRelToHyperlink.get(artifact, relationTypeSide);
                  link.dispose();
                  Label label = artAndRelToLabel.get(artifact, relationTypeSide);
                  label.dispose();
               }
            });
            found = true;
         }
      }
      if (found) {
         getParent().layout();
      }
   }

   public static boolean relationExists(AbstractWorkflowArtifact smaArt) {
      for (RelationTypeSide side : sides) {
         if (smaArt.getRelatedArtifacts(side).size() > 0) {
            return true;
         }
      }
      if (smaArt instanceof AbstractReviewArtifact && AtsClientService.get().getWorkItemService().getActionableItemService().hasActionableItems(
         smaArt)) {
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

   private void createArtifactRelationHyperlinks(String prefix, Artifact thisArt, String action, RelationTypeSide relation) {
      for (final Artifact art : thisArt.getRelatedArtifacts(relation)) {
         createLink(prefix, art, action, thisArt, relation);
      }
   }

   /**
    * @param relation or null if sibling relation ship
    */
   private void createLink(String prefix, final Artifact art, String action, Artifact thisArt, RelationTypeSide relation) {
      try {
         Label label = editor.getToolkit().createLabel(this,
            prefix + " \"" + thisArt.getArtifactTypeName() + "\" " + action + getCompletedCancelledString(
               art) + " \"" + art.getArtifactTypeName() + "\" ");
         Hyperlink link = editor.getToolkit().createHyperlink(this,
            String.format("\"%s\" - %s", art.getName().length() < 60 ? art.getName() : art.getName().substring(0, 60),
               AtsClientService.get().getAtsId(art)),
            SWT.NONE);
         if (art.equals(thisArt)) {
            artAndRelToHyperlink.put(thisArt, relation, link);
            artAndRelToLabel.put(thisArt, relation, label);
         } else {
            artAndRelToHyperlink.put(art, relation, link);
            artAndRelToLabel.put(art, relation, label);
         }
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
               if (AtsObjects.isAtsWorkItemOrAction(art)) {
                  AtsEditors.openATSAction(art, AtsOpenOption.OpenOneOrPopupSelect);
               } else {
                  try {
                     RendererManager.open(art, PresentationType.DEFAULT_OPEN);
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

   @Override
   public IAtsWorkItem getWorkItem() {
      return workItem;
   }

}
