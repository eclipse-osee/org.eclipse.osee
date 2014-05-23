/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.review;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.actions.OpenWorkflowByIdAction;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem.ReviewState;
import org.eclipse.osee.ats.world.search.ShowOpenWorkflowsByArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.OpenPerspectiveNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateCommonItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateCommonItems;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;

/**
 * @author Donald G. Dunne
 */
public class ReviewNavigateViewItems implements XNavigateViewItems, IXNavigateCommonItem {

   private final static ReviewNavigateViewItems instance = new ReviewNavigateViewItems();
   private final List<XNavigateItem> items = new CopyOnWriteArrayList<XNavigateItem>();
   private boolean ensurePopulatedRanOnce = false;

   public static ReviewNavigateViewItems getInstance() {
      return instance;
   }

   @Override
   public List<XNavigateItem> getSearchNavigateItems() {
      ensurePopulated();
      return items;
   }

   private synchronized void ensurePopulated() {
      if (!ensurePopulatedRanOnce) {
         if (DbConnectionUtility.areOSEEServicesAvailable().isFalse()) {
            return;
         }
         this.ensurePopulatedRanOnce = true;

         try {
            addOseePeerSectionChildren(null);

            XNavigateCommonItems.addCommonNavigateItems(items, Arrays.asList(getSectionId()));
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public void addOseePeerSectionChildren(XNavigateItem item) throws OseeCoreException {
      try {
         IAtsUser user = AtsClientService.get().getUserService().getCurrentUser();
         items.add(new SearchNavigateItem(item, new MyReviewWorkflowItem("My Reviews", user, ReviewState.InWork)));
         items.add(new SearchNavigateItem(item, new MyReviewWorkflowItem("User's Reviews", null, ReviewState.InWork)));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      items.add(new SearchNavigateItem(item, new ReviewSearchWorkflowSearchItem()));
      items.add(new XNavigateItemAction(item, new OpenWorkflowByIdAction("Open Review by ID"), AtsImage.REVIEW));
      items.add(new SearchNavigateItem(item, new ShowOpenWorkflowsByArtifactType(
         "Show Open " + AtsArtifactTypes.DecisionReview.getName() + "s", AtsArtifactTypes.DecisionReview, false, false,
         AtsImage.DECISION_REVIEW)));
      items.add(new SearchNavigateItem(item, new ShowOpenWorkflowsByArtifactType(
         "Show Workflows Waiting " + AtsArtifactTypes.DecisionReview.getName() + "s", AtsArtifactTypes.DecisionReview,
         false, true, AtsImage.DECISION_REVIEW)));
      items.add(new SearchNavigateItem(item, new ShowOpenWorkflowsByArtifactType(
         "Show Open " + AtsArtifactTypes.PeerToPeerReview.getName() + "s", AtsArtifactTypes.PeerToPeerReview, false,
         false, AtsImage.PEER_REVIEW)));
      items.add(new SearchNavigateItem(item, new ShowOpenWorkflowsByArtifactType(
         "Show Workflows Waiting " + AtsArtifactTypes.PeerToPeerReview.getName() + "s",
         AtsArtifactTypes.PeerToPeerReview, false, true, AtsImage.PEER_REVIEW)));
      items.add(new NewPeerToPeerReviewItem(item));
      items.add(new GenerateReviewParticipationReport(item));

   }

   @Override
   public void createCommonSection(List<XNavigateItem> items, List<String> excludeSectionIds) {
      try {
         XNavigateItem reviewItem = new XNavigateItem(null, "OSEE Review", AtsImage.REVIEW);
         new OpenPerspectiveNavigateItem(reviewItem, "Review", ReviewPerspective.ID, AtsImage.REVIEW);
         addOseePeerSectionChildren(reviewItem);
         items.add(reviewItem);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create OSEE Review section");
      }
   }

   @Override
   public String getSectionId() {
      return "Peer";
   }
}
