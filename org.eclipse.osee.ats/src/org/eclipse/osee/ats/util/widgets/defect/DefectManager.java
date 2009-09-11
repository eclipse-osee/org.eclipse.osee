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
package org.eclipse.osee.ats.util.widgets.defect;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Severity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class DefectManager {

   private WeakReference<Artifact> artifactRef;
   private boolean enabled = true;
   private static String DEFECT_ITEM_TAG = "Item";
   private static String REVIEW_DEFECT_ATTRIBUTE_NAME = "ats.Review Defect";
   private final Matcher defectMatcher =
         java.util.regex.Pattern.compile("<" + DEFECT_ITEM_TAG + ">(.*?)</" + DEFECT_ITEM_TAG + ">",
               Pattern.DOTALL | Pattern.MULTILINE).matcher("");

   public DefectManager(Artifact artifact) {
      this.artifactRef = new WeakReference<Artifact>(artifact);
   }

   public Artifact getArtifact() throws OseeStateException {
      if (artifactRef.get() == null) {
         throw new OseeStateException("Artifact has been garbage collected");
      }
      return artifactRef.get();
   }

   public String getHtml() throws OseeCoreException {
      if (getDefectItems().size() == 0) {
         return "";
      }
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "Defects"));
      sb.append(getTable());
      return sb.toString();
   }

   public Set<DefectItem> getDefectItems() throws OseeCoreException {
      Set<DefectItem> defectItems = new HashSet<DefectItem>();
      for (String xml : getArtifact().getAttributesToStringList(REVIEW_DEFECT_ATTRIBUTE_NAME)) {
         defectMatcher.reset(xml);
         while (defectMatcher.find()) {
            DefectItem item = new DefectItem(defectMatcher.group());
            defectItems.add(item);
         }
      }
      return defectItems;
   }

   public int getNumMajor() throws OseeCoreException {
      int x = 0;
      for (DefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Major) {
            x++;
         }
      }
      return x;
   }

   public int getNumMinor() throws OseeCoreException {
      int x = 0;
      for (DefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Minor) {
            x++;
         }
      }
      return x;
   }

   public int getNumIssues() throws OseeCoreException {
      int x = 0;
      for (DefectItem dItem : getDefectItems()) {
         if (dItem.getSeverity() == Severity.Issue) {
            x++;
         }
      }
      return x;
   }

   private void saveDefectItems(Set<DefectItem> defectItems, boolean persist, SkynetTransaction transaction) {
      try {
         // Change existing ones
         for (Attribute<?> attr : getArtifact().getAttributes(REVIEW_DEFECT_ATTRIBUTE_NAME)) {
            DefectItem dbPromoteItem = new DefectItem((String) attr.getValue());
            for (DefectItem pItem : defectItems) {
               if (pItem.equals(dbPromoteItem)) {
                  attr.setFromString(AXml.addTagData(DEFECT_ITEM_TAG, pItem.toXml()));
               }
            }
         }
         Set<DefectItem> dbPromoteItems = getDefectItems();
         // Remove deleted ones; items in dbPromoteItems that are not in promoteItems
         for (DefectItem delPromoteItem : org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(
               dbPromoteItems, defectItems)) {
            for (Attribute<?> attr : getArtifact().getAttributes(REVIEW_DEFECT_ATTRIBUTE_NAME)) {
               DefectItem dbPromoteItem = new DefectItem((String) attr.getValue());
               if (dbPromoteItem.equals(delPromoteItem)) {
                  attr.delete();
               }
            }
         }
         // Add new ones: items in promoteItems that are not in dbPromoteItems
         for (DefectItem newPromoteItem : org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(
               defectItems, dbPromoteItems)) {
            getArtifact().addAttributeFromString(REVIEW_DEFECT_ATTRIBUTE_NAME,
                  AXml.addTagData(DEFECT_ITEM_TAG, newPromoteItem.toXml()));
         }
         if (persist) {
            getArtifact().persist(transaction);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't create ats review defect document", ex);
      }
   }

   public void addOrUpdateDefectItem(DefectItem defectItem, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      Set<DefectItem> defectItems = getDefectItems();
      boolean found = false;
      for (DefectItem dItem : defectItems) {
         if (defectItem.equals(dItem)) {
            dItem.update(defectItem);
            found = true;
         }
      }
      if (!found) {
         defectItems.add(defectItem);
      }
      saveDefectItems(defectItems, persist, transaction);
   }

   public void removeDefectItem(DefectItem defectItem, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      Set<DefectItem> defectItems = getDefectItems();
      defectItems.remove(defectItem);
      saveDefectItems(defectItems, persist, transaction);
   }

   public void addDefectItem(String description, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      DefectItem item = new DefectItem();
      item.setDescription(description);
      addOrUpdateDefectItem(item, persist, transaction);
   }

   public String getTable() throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      builder.append("<TABLE BORDER=\"1\" cellspacing=\"1\" cellpadding=\"3%\" width=\"100%\"><THEAD><TR><TH>Severity</TH>" + "<TH>Disposition</TH><TH>Injection</TH><TH>User</TH><TH>Date</TH><TH>Description</TH><TH>Location</TH>" + "<TH>Resolution</TH><TH>Guid</TH><TH>Completed</TH></THEAD></TR>");
      for (DefectItem item : getDefectItems()) {
         User user = item.getUser();
         builder.append("<TR>");
         builder.append("<TD>" + item.getSeverity() + "</TD>");
         builder.append("<TD>" + item.getDisposition() + "</TD>");
         builder.append("<TD>" + item.getInjectionActivity() + "</TD>");
         if (user != null && user.equals(UserManager.getUser())) {
            builder.append("<TD bgcolor=\"#CCCCCC\">" + user.getName() + "</TD>");
         } else {
            builder.append("<TD>NONE</TD>");
         }
         builder.append("<TD>" + item.getCreatedDate(XDate.MMDDYYHHMM) + "</TD>");
         builder.append("<TD>" + item.getDescription() + "</TD>");
         builder.append("<TD>" + item.getLocation() + "</TD>");
         builder.append("<TD>" + item.getResolution() + "</TD>");
         builder.append("<TD>" + item.getGuid() + "</TD>");
         builder.append("<TD>" + item.isClosed() + "</TD>");
         builder.append("</TR>");

      }
      builder.append("</TABLE>");
      return builder.toString();
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

}