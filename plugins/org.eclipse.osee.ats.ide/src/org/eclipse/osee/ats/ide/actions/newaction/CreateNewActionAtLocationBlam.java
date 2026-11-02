/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.actions.newaction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.ats.ide.editor.tab.members.IMemberProvider;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXActionableItemWidget;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class CreateNewActionAtLocationBlam extends CreateNewActionBlam {

   private static final String BLAM_NAME = "Create New Action At This Location";

   private IMemberProvider memberProvider;
   private List<IAtsActionableItem> aias;
   private Artifact dropTarget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public CreateNewActionAtLocationBlam() {
      this(null, Collections.emptyList(), null);
   }

   public CreateNewActionAtLocationBlam(IMemberProvider memberProvider, List<IAtsActionableItem> aias, Artifact dropTarget) {
      super(BLAM_NAME, AtsImage.NEW_ACTION);
      this.memberProvider = memberProvider;
      this.aias = aias;
      this.dropTarget = dropTarget;
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   @Override
   public void inputSectionCreated(XWidgetPage widgetPage) {
      XXActionableItemWidget aiWidget = (XXActionableItemWidget) widgetPage.getXWidget("Actionable Item(s)");
      if (aias == null) {
         aias = AtsApiService.get().getActionableItemService().getTopLevelActionableItems(Active.Active);
      }
      aiWidget.setSelectable(aias);
   }

   @Override
   public void actionCreated(NewActionResult results, TransactionId tx) {
      Artifact collectorArt = memberProvider.getArtifact();
      collectorArt.reloadAttributesAndRelations();

      if (collectorArt.isOfType(AtsArtifactTypes.Goal)) {
         AtsApiService.get().getGoalMembersCache().decache((GoalArtifact) collectorArt);

         IAtsWorkItem goalWorkItem = atsApi.getWorkItemService().getWorkItem(collectorArt);
         atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED,
            Collections.singleton(goalWorkItem), tx);
      }
   }

   @Override
   public void createActionData(NewActionData data) {
      data.andMemberData(memberProvider.getArtifact(), memberProvider.getMemberRelationTypeSide().getRelationType(),
         dropTarget);
   }

   public IMemberProvider getMemberProvider() {
      return memberProvider;
   }

   public void setMemberProvider(IMemberProvider memberProvider) {
      this.memberProvider = memberProvider;
   }

   public List<IAtsActionableItem> getAias() {
      return aias;
   }

   public void setAias(List<IAtsActionableItem> aias) {
      this.aias = aias;
   }

   public Artifact getDropTarget() {
      return dropTarget;
   }

   public void setDropTarget(Artifact dropTarget) {
      this.dropTarget = dropTarget;
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavItemCat.NO_NAV);
   }

   @Override
   public String getDescriptionUsage() {
      if (memberProvider != null && dropTarget != null) {
         return BLAM_DESC + " and add to [" + //
            (memberProvider.getArtifact() != null ? memberProvider.getArtifact().getName() : " artifact ") + "] " + //
            (dropTarget == null ? "to the end" : "before item " + dropTarget.toStringWithId());
      }
      return BLAM_DESC;

   }

}
