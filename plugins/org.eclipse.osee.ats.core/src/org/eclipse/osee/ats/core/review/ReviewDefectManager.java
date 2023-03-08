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

package org.eclipse.osee.ats.core.review;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsConfigKey;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewDefectManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.core.util.ArtifactValueProvider;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public class ReviewDefectManager implements IAtsPeerReviewDefectManager {

   private final static String DEFECT_ITEM_TAG = "Item";

   private final Matcher defectMatcher =
      java.util.regex.Pattern.compile("<" + DEFECT_ITEM_TAG + ">(.*?)</" + DEFECT_ITEM_TAG + ">",
         Pattern.DOTALL | Pattern.MULTILINE).matcher("");
   private final IValueProvider valueProvider;
   private Set<ReviewDefectItem> defectItems = null;
   private AtsApi atsApi;
   private Boolean asGuid;
   private IAtsPeerToPeerReview review;

   public ReviewDefectManager(IAtsPeerToPeerReview review, AtsApi atsApi) {
      this.atsApi = atsApi;
      this.valueProvider = new ArtifactValueProvider(review.getStoreObject(), AtsAttributeTypes.ReviewDefect, atsApi);
      this.review = review;
   }

   public ReviewDefectManager(IValueProvider valueProvider) {
      this.valueProvider = valueProvider;
   }

   @Override
   public String getHtml() {
      if (getDefectItems().isEmpty()) {
         return "";
      }
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "Defects"));
      sb.append(getTable());
      return sb.toString();
   }

   public void ensureLoaded() {
      if (defectItems == null) {
         defectItems = new HashSet<>();
         for (String xml : valueProvider.getValues()) {
            defectMatcher.reset(xml);
            while (defectMatcher.find()) {
               ReviewDefectItem item = new ReviewDefectItem(defectMatcher.group(), false, review);
               defectItems.add(item);
            }
         }
      }
   }

   @Override
   public Set<ReviewDefectItem> getDefectItems() {
      ensureLoaded();
      return defectItems;
   }

   @Override
   public int getNumMajor(AtsUser user) {
      int x = 0;
      for (ReviewDefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Major && dItem.getUserId().equals(user.getUserId())) {
            x++;
         }
      }
      return x;
   }

   @Override
   public int getNumMinor(AtsUser user) {
      int x = 0;
      for (ReviewDefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Minor && dItem.getUserId().equals(user.getUserId())) {
            x++;
         }
      }
      return x;
   }

   @Override
   public int getNumIssues(AtsUser user) {
      int x = 0;
      for (ReviewDefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Issue && dItem.getUserId().equals(user.getUserId())) {
            x++;
         }
      }
      return x;
   }

   @Override
   public int getNumMajor() {
      int x = 0;
      for (ReviewDefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Major) {
            x++;
         }
      }
      return x;
   }

   @Override
   public int getNumMinor() {
      int x = 0;
      for (ReviewDefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Minor) {
            x++;
         }
      }
      return x;
   }

   @Override
   public int getNumIssues() {
      int x = 0;
      for (ReviewDefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Issue) {
            x++;
         }
      }
      return x;
   }

   private boolean asGuid() {
      if (asGuid == null) {
         asGuid = "true".equals(atsApi.getConfigValue(AtsConfigKey.PeerDefectAsGuid, "false"));
      }
      return asGuid;
   }

   private List<ReviewDefectItem> getStoredDefectItems(IAtsPeerToPeerReview peerRev) {
      // Add new ones: items in userRoles that are not in dbuserRoles
      List<ReviewDefectItem> storedDefectItems = new ArrayList<>();
      for (IAttribute<?> attr : atsApi.getAttributeResolver().getAttributes(peerRev, AtsAttributeTypes.ReviewDefect)) {
         ReviewDefectItem storedRole = new ReviewDefectItem((String) attr.getValue(), asGuid(), peerRev);
         storedDefectItems.add(storedRole);
      }
      return storedDefectItems;
   }

   @Override
   public void saveToArtifact(IAtsPeerToPeerReview peerRev, IAtsChangeSet changes) {
      // Change existing ones
      for (IAttribute<?> attr : atsApi.getAttributeResolver().getAttributes(peerRev, AtsAttributeTypes.ReviewDefect)) {
         ReviewDefectItem storedDefect = new ReviewDefectItem((String) attr.getValue(), asGuid(), peerRev);
         for (ReviewDefectItem defectItem : getDefectItems()) {
            if (defectItem.equals(storedDefect)) {
               changes.setAttribute(peerRev, attr, AXml.addTagData(DEFECT_ITEM_TAG, defectItem.toXml(asGuid())));
            }
         }
      }
      List<ReviewDefectItem> storedDefectItems = getStoredDefectItems(peerRev);

      // Remove deleted ones; items in dbdefectItems that are not in defectItems
      for (ReviewDefectItem delItem : org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(
         storedDefectItems, getDefectItems())) {
         for (IAttribute<?> attr : atsApi.getAttributeResolver().getAttributes(peerRev,
            AtsAttributeTypes.ReviewDefect)) {
            ReviewDefectItem storedItem = new ReviewDefectItem((String) attr.getValue(), asGuid(), peerRev);
            if (storedItem.equals(delItem)) {
               changes.deleteAttribute(peerRev, attr);
            }
         }
      }
      // Add new ones: items in defectItems that are not in dbdefectItems
      for (ReviewDefectItem newDefect : org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(
         getDefectItems(), storedDefectItems)) {
         changes.addAttribute(peerRev, AtsAttributeTypes.ReviewDefect,
            AXml.addTagData(DEFECT_ITEM_TAG, newDefect.toXml(asGuid())));
      }
   }

   @Override
   public void addOrUpdateDefectItem(ReviewDefectItem defectItem) {
      Set<ReviewDefectItem> defectItems = getDefectItems();
      boolean found = false;
      for (ReviewDefectItem dItem : defectItems) {
         if (defectItem.equals(dItem)) {
            dItem.update(defectItem, asGuid(), review);
            found = true;
         }
      }
      if (!found) {
         defectItems.add(defectItem);
      }
   }

   @Override
   public void removeDefectItem(ReviewDefectItem defectItem) {
      Set<ReviewDefectItem> defectItems = getDefectItems();
      defectItems.remove(defectItem);
   }

   @Override
   public void addDefectItem(String description) {
      ReviewDefectItem item = new ReviewDefectItem();
      item.setUserId(atsApi.getUserService().getCurrentUserId());
      item.setDescription(description);
      addOrUpdateDefectItem(item);
   }

   @Override
   public String getTable() {
      StringBuilder builder = new StringBuilder();
      builder.append(
         "<TABLE BORDER=\"1\" cellspacing=\"1\" cellpadding=\"3%\" width=\"100%\"><THEAD><TR><TH>Severity</TH>" + "<TH>Disposition</TH><TH>Injection</TH><TH>User</TH><TH>Date</TH><TH>Description</TH><TH>Location</TH><TH>Resolution</TH><TH>Id</TH><TH>Closed</TH><TH>Closed</TH></THEAD></TR>");
      for (ReviewDefectItem item : getDefectItems()) {
         String userId = item.getUserId();
         AtsUser user = atsApi.getUserService().getUserByUserId(userId);
         builder.append("<TR>");
         builder.append("<TD>" + item.getSeverity() + "</TD>");
         builder.append("<TD>" + item.getDisposition() + "</TD>");
         builder.append("<TD>" + item.getInjectionActivity() + "</TD>");
         if (user != null && user.equals(atsApi.getUserService().getCurrentUser())) {
            builder.append("<TD bgcolor=\"#CCCCCC\">" + user.getName() + "</TD>");
         } else {
            builder.append("<TD>NONE</TD>");
         }
         builder.append("<TD>" + DateUtil.getMMDDYYHHMM(item.getDate()) + "</TD>");
         builder.append("<TD>" + item.getDescription() + "</TD>");
         builder.append("<TD>" + item.getLocation() + "</TD>");
         builder.append("<TD>" + item.getResolution() + "</TD>");
         builder.append("<TD>" + item.getId() + "</TD>");
         builder.append("<TD>" + item.isClosed() + "</TD>");
         String closedUserId = item.getClosedUserId();
         AtsUser closedUser = atsApi.getUserService().getUserByUserId(closedUserId);
         builder.append("<TD>" + closedUser.getName() + "</TD>");
         builder.append("</TR>");

      }
      builder.append("</TABLE>");
      return builder.toString();
   }

}