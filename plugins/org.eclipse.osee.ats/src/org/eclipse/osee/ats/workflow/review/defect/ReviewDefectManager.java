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
package org.eclipse.osee.ats.workflow.review.defect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.validate.ArtifactValueProvider;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem.Severity;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class ReviewDefectManager {

   private final static String DEFECT_ITEM_TAG = "Item";
   private static final AttributeTypeToken REVIEW_STORAGE_TYPE = AtsAttributeTypes.ReviewDefect;

   private final Matcher defectMatcher =
      java.util.regex.Pattern.compile("<" + DEFECT_ITEM_TAG + ">(.*?)</" + DEFECT_ITEM_TAG + ">",
         Pattern.DOTALL | Pattern.MULTILINE).matcher("");
   private final IValueProvider valueProvider;
   private Set<ReviewDefectItem> defectItems = null;

   public ReviewDefectManager(Artifact artifact) {
      this.valueProvider = new ArtifactValueProvider(artifact, REVIEW_STORAGE_TYPE);
   }

   public ReviewDefectManager(IValueProvider valueProvider) {
      this.valueProvider = valueProvider;
   }

   public String getHtml() {
      if (getDefectItems().isEmpty()) {
         return "";
      }
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "Defects"));
      sb.append(getTable());
      return sb.toString();
   }

   public static Set<ReviewDefectItem> getDefectItems(Artifact artifact) {
      return new ReviewDefectManager(artifact).getDefectItems();
   }

   public void ensureLoaded() {
      if (defectItems == null) {
         defectItems = new HashSet<>();
         for (String xml : valueProvider.getValues()) {
            defectMatcher.reset(xml);
            while (defectMatcher.find()) {
               ReviewDefectItem item = new ReviewDefectItem(defectMatcher.group());
               defectItems.add(item);
            }
         }
      }
   }

   public Set<ReviewDefectItem> getDefectItems() {
      ensureLoaded();
      return defectItems;
   }

   public int getNumMajor(IAtsUser user) {
      int x = 0;
      for (ReviewDefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Major && dItem.getUser().equals(user)) {
            x++;
         }
      }
      return x;
   }

   public int getNumMinor(IAtsUser user) {
      int x = 0;
      for (ReviewDefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Minor && dItem.getUser().equals(user)) {
            x++;
         }
      }
      return x;
   }

   public int getNumIssues(IAtsUser user) {
      int x = 0;
      for (ReviewDefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Issue && dItem.getUser().equals(user)) {
            x++;
         }
      }
      return x;
   }

   public int getNumMajor() {
      int x = 0;
      for (ReviewDefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Major) {
            x++;
         }
      }
      return x;
   }

   public int getNumMinor() {
      int x = 0;
      for (ReviewDefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Minor) {
            x++;
         }
      }
      return x;
   }

   public int getNumIssues() {
      int x = 0;
      for (ReviewDefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Issue) {
            x++;
         }
      }
      return x;
   }

   @SuppressWarnings("deprecation")
   private List<ReviewDefectItem> getStoredDefectItems(Artifact artifact) {
      // Add new ones: items in userRoles that are not in dbuserRoles
      List<ReviewDefectItem> storedDefectItems = new ArrayList<>();
      for (Attribute<?> attr : artifact.getAttributes(REVIEW_STORAGE_TYPE)) {
         ReviewDefectItem storedRole = new ReviewDefectItem((String) attr.getValue());
         storedDefectItems.add(storedRole);
      }
      return storedDefectItems;
   }

   @SuppressWarnings("deprecation")
   public void saveToArtifact(Artifact artifact) {
      // Change existing ones
      for (Attribute<?> attr : artifact.getAttributes(REVIEW_STORAGE_TYPE)) {
         ReviewDefectItem storedDefect = new ReviewDefectItem((String) attr.getValue());
         for (ReviewDefectItem defectItem : getDefectItems()) {
            if (defectItem.equals(storedDefect)) {
               attr.setFromString(AXml.addTagData(DEFECT_ITEM_TAG, defectItem.toXml()));
            }
         }
      }
      List<ReviewDefectItem> storedDefectITems = getStoredDefectItems(artifact);

      // Remove deleted ones; items in dbdefectItems that are not in defectItems
      for (ReviewDefectItem delItem : org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(
         storedDefectITems, getDefectItems())) {
         for (Attribute<?> attr : artifact.getAttributes(REVIEW_STORAGE_TYPE)) {
            ReviewDefectItem storedItem = new ReviewDefectItem((String) attr.getValue());
            if (storedItem.equals(delItem)) {
               attr.delete();
            }
         }
      }
      // Add new ones: items in defectItems that are not in dbdefectItems
      for (ReviewDefectItem newDefect : org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(
         getDefectItems(), storedDefectITems)) {
         artifact.addAttributeFromString(REVIEW_STORAGE_TYPE, AXml.addTagData(DEFECT_ITEM_TAG, newDefect.toXml()));
      }
   }

   public void addOrUpdateDefectItem(ReviewDefectItem defectItem) {
      Set<ReviewDefectItem> defectItems = getDefectItems();
      boolean found = false;
      for (ReviewDefectItem dItem : defectItems) {
         if (defectItem.equals(dItem)) {
            dItem.update(defectItem);
            found = true;
         }
      }
      if (!found) {
         defectItems.add(defectItem);
      }
   }

   public void removeDefectItem(ReviewDefectItem defectItem, boolean persist, SkynetTransaction transaction) {
      Set<ReviewDefectItem> defectItems = getDefectItems();
      defectItems.remove(defectItem);
   }

   public void addDefectItem(String description, boolean persist, SkynetTransaction transaction) {
      ReviewDefectItem item = new ReviewDefectItem();
      item.setDescription(description);
      addOrUpdateDefectItem(item);
   }

   public String getTable() {
      StringBuilder builder = new StringBuilder();
      builder.append(
         "<TABLE BORDER=\"1\" cellspacing=\"1\" cellpadding=\"3%\" width=\"100%\"><THEAD><TR><TH>Severity</TH>" + "<TH>Disposition</TH><TH>Injection</TH><TH>User</TH><TH>Date</TH><TH>Description</TH><TH>Location</TH>" + "<TH>Resolution</TH><TH>Guid</TH><TH>Completed</TH></THEAD></TR>");
      for (ReviewDefectItem item : getDefectItems()) {
         IAtsUser user = item.getUser();
         builder.append("<TR>");
         builder.append("<TD>" + item.getSeverity() + "</TD>");
         builder.append("<TD>" + item.getDisposition() + "</TD>");
         builder.append("<TD>" + item.getInjectionActivity() + "</TD>");
         if (user != null && user.equals(AtsClientService.get().getUserService().getCurrentUser())) {
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
         builder.append("</TR>");

      }
      builder.append("</TABLE>");
      return builder.toString();
   }

}