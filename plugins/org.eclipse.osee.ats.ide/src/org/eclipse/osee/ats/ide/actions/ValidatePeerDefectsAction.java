/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ValidatePeerDefectsAction extends Action {

   public ValidatePeerDefectsAction() {
      this("Validate Peer Review by ID(s)");
   }

   public ValidatePeerDefectsAction(String name) {
      super(name);
      setToolTipText(getText());
   }

   @Override
   public void run() {

      EntryDialog ed = new EntryDialog(getText(), "Enter Peer Review Id(s) (comma delimited");
      if (ed.open() == Window.OK) {
         XResultData rd = new XResultData();
         rd.log(getText() + "\n\n");
         String idsStr = ed.getEntry();
         List<IAtsWorkItem> workItems = AtsClientService.get().getQueryService().getWorkItemsByIds(idsStr);
         for (IAtsWorkItem workItem : workItems) {
            if (workItem.isPeerReview()) {
               validateReviewDefects(workItem, rd);
            } else {
               rd.logf("WorkItem not Peer Review %s", workItem.toStringWithId());
            }
         }
         XResultDataUI.report(rd, getText());
      }
   }

   private void validateReviewDefects(IAtsWorkItem workItem, XResultData rd) {
      Artifact art =
         ArtifactQuery.reloadArtifactFromId(workItem.getArtifactId(), AtsClientService.get().getAtsBranch());
      IAtsPeerToPeerReview review = (IAtsPeerToPeerReview) AtsClientService.get().getWorkItemService().getReview(art);
      Map<String, Attribute<?>> guids = new HashMap<String, Attribute<?>>();
      Map<Long, Attribute<?>> ids = new HashMap<>();
      for (Attribute<?> attr : art.getAttributes(AtsAttributeTypes.ReviewDefect)) {
         String xml = (String) attr.getValue();
         ReviewDefectItem item = AtsClientService.get().getReviewService().getDefectItem(xml, review);
         if (guids.keySet().contains(item.getGuid())) {
            rd.errorf("Duplicate guid %s on attr id %s and attr id %s\n", item.getGuid(),
               guids.get(item.getGuid()).getIdString(), attr.getIdString());
         } else {
            guids.put(item.getGuid(), attr);
         }
         if (ids.keySet().contains(item.getId())) {
            rd.errorf("Duplicate id %s on attr id %s and attr id %s\n", item.getId(),
               ids.get(item.getId()).getIdString(), attr.getIdString());
         } else {
            ids.put(item.getId(), attr);
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.PEER_REVIEW);
   }

}
