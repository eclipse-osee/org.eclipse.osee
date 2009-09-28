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
package org.eclipse.osee.coverage.editor.xcover;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class CoverageItem {

   private Date date = new Date();
   private Date promotedDate = new Date();
   private String notes = "";
   private String engBuildGuid = "";
   private String planCmBuildGuid = "";
   private String viewComparison = "";
   private User user;
   private String guid = GUID.create();
   private String viewComparisonGroup;
   private boolean promoted = false;
   private static Pattern subsystem = Pattern.compile("([A-Za-z_0-9]+)\\.ss/");
   private String subSystemCache = null;

   public CoverageItem() throws OseeCoreException {
      user = UserManager.getUser();
   }

   public void update(CoverageItem dItem) throws OseeCoreException {
      fromXml(dItem.toXml());
   }

   public CoverageItem(String xml) throws OseeCoreException {
      fromXml(xml);
   }

   public String getDate(String pattern) {
      if (pattern != null) return (new SimpleDateFormat(pattern)).format(date);
      return date.toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CoverageItem) {
         CoverageItem di = (CoverageItem) obj;
         return di.guid.equals(getGuid());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return guid.hashCode();
   }

   public String toXml() throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      sb.append(AXml.addTagData("guid", "" + guid));
      sb.append(AXml.addTagData("date", "" + date.getTime()));
      sb.append(AXml.addTagData("user", "" + user.getUserId()));
      sb.append(AXml.addTagData("planCmBuildGuid", "" + planCmBuildGuid));
      sb.append(AXml.addTagData("engBuildGuid", "" + engBuildGuid));
      sb.append(AXml.addTagData("promoted", "" + String.valueOf(promoted)));
      sb.append(AXml.addTagData("promotedDate", "" + promotedDate.getTime()));
      sb.append(AXml.addTagData("viewComp", "" + viewComparison.toString()));
      sb.append(AXml.addTagData("notes", "" + notes.toString()));
      return sb.toString();
   }

   private void fromXml(String xml) throws OseeCoreException {
      Date date = new Date();
      date.setTime(new Long(AXml.getTagData(xml, "date")));
      this.date = date;
      date.setTime(new Long(AXml.getTagData(xml, "promotedDate")));
      this.promotedDate = date;
      this.user = UserManager.getUserByUserId(AXml.getTagData(xml, "user"));
      this.notes = AXml.getTagData(xml, "notes");
      this.planCmBuildGuid = AXml.getTagData(xml, "planCmBuildGuid");
      this.engBuildGuid = AXml.getTagData(xml, "engBuildGuid");
      this.viewComparison = AXml.getTagData(xml, "viewComp");
      this.promoted = AXml.getTagBooleanData(xml, "promoted");
      this.guid = AXml.getTagData(xml, "guid");
   }

   public Date getDate() {
      return date;
   }

   public String getDateStr(String pattern) {
      if (pattern != null) return (new SimpleDateFormat(pattern)).format(date);
      return date.toString();
   }

   public String getPromotedDateStr(String pattern) {
      if (pattern != null) return (new SimpleDateFormat(pattern)).format(promotedDate);
      return promotedDate.toString();
   }

   public void setDate(Date date) {
      this.date = date;
   }

   @Override
   public String toString() {
      return engBuildGuid + promoted + " - " + user + " on " + getDateStr(XDate.MMDDYYHHMM) + "\n";
   }

   public User getUser() {
      return user;
   }

   public String toHTML(String labelFont) {
      return "PROMOTE: " + toString();
   }

   /**
    * @param user The user to set.
    */
   public void setUser(User user) {
      this.user = user;
   }

   /**
    * @return the guid
    */
   public String getGuid() {
      return guid;
   }

   /**
    * @param guid the guid to set
    */
   public void setGuid(String guid) {
      this.guid = guid;
   }

   /**
    * @return the promotedDate
    */
   public Date getPromotedDate() {
      return promotedDate;
   }

   /**
    * @param promotedDate the promotedDate to set
    */
   public void setPromotedDate(Date promotedDate) {
      this.promotedDate = promotedDate;
   }

   /**
    * @return the notes
    */
   public String getNotes() {
      return notes;
   }

   /**
    * @param notes the notes to set
    */
   public void setNotes(String notes) {
      this.notes = notes;
   }

   /**
    * @return the engBuildGuid
    */
   public String getEngBuildGuid() {
      return engBuildGuid;
   }

   /**
    * @param engBuildId the engBuildGuid to set
    */
   public void setEngBuildGuid(String engBuildGuid) {
      this.engBuildGuid = engBuildGuid;
   }

   /**
    * @return the viewComparison
    */
   public String getViewComparison() {
      return viewComparison;
   }

   /**
    * @param viewComparison the viewComparison to set
    */
   public void setViewComparison(String viewComparison) {
      this.viewComparison = viewComparison;
      subSystemCache = null;
   }

   public String getSubSystem() {
      if (subSystemCache == null) {
         String vc = getViewComparison();
         if (!vc.equals("")) {
            Matcher m = subsystem.matcher(vc);
            Set<String> subSystems = new HashSet<String>();
            while (m.find()) {
               subSystems.add(m.group(1));
            }
            subSystemCache = Collections.toString(", ", subSystems);
         } else {
            subSystemCache = "";
         }
      }
      return subSystemCache;
   }

   /**
    * @return the promoted
    */
   public boolean isPromoted() {
      return promoted;
   }

   /**
    * @param promoted the promoted to set
    */
   public void setPromoted(boolean promoted) {
      this.promoted = promoted;
   }

   public boolean isEditable() {
      return (isPromoted() ? false : true);
   }

   public String getViewComparisonGroup() {
      return viewComparisonGroup;
   }

   public void setViewComparisonGroup(String viewComparisonGroup) {
      this.viewComparisonGroup = viewComparisonGroup;
   }

   public String getPlanCmBuildGuid() {
      return planCmBuildGuid;
   }

   public void setPlanCmBuildGuid(String planCmBuildGuid) {
      this.planCmBuildGuid = planCmBuildGuid;
   }

}
