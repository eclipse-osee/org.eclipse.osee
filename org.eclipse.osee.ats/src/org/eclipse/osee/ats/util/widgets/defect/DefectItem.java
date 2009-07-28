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

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class DefectItem {

   private Date date = new Date();
   private String description = "";
   private String location = "";
   private String resolution = "";
   private User user;
   private String guid = GUID.create();
   private Severity severity = Severity.None;
   private Disposition disposition = Disposition.None;
   private InjectionActivity injectionActivity = InjectionActivity.None;
   private boolean closed = false;
   public static enum Severity {
      None, Major, Minor, Issue;
      public static Collection<String> strValues() {
         Set<String> values = new HashSet<String>();
         for (Enum<Severity> e : values()) {
            values.add(e.name());
         }
         return values;
      }

      public static Image getImage(Severity sev) {
         if (sev == Major)
            return ImageManager.getImage(FrameworkImage.SEVERITY_MAJOR);
         else if (sev == Minor)
            return ImageManager.getImage(FrameworkImage.SEVERITY_MINOR);
         else if (sev == Issue) return ImageManager.getImage(FrameworkImage.SEVERITY_ISSUE);
         return null;
      }
   };

   public DefectItem() throws OseeCoreException {
      user = UserManager.getUser();
   }

   public DefectItem(User user, Severity severity, Disposition disposition, InjectionActivity injectionActivity, String description, String resolution, String location, Date date) {
      this.user = user;
      if (severity != null) this.severity = severity;
      if (disposition != null) this.disposition = disposition;
      if (injectionActivity != null) this.injectionActivity = injectionActivity;
      if (description != null) this.description = description;
      if (resolution != null) this.resolution = resolution;
      if (location != null) this.location = location;
      if (date != null) this.date = date;
   }

   public DefectItem(String xml) throws OseeCoreException {
      fromXml(xml);
   }

   public void update(DefectItem dItem) throws OseeCoreException {
      fromXml(dItem.toXml());
   }

   public static enum Disposition {
      None, Accept, Reject, Duplicate;
      public static Collection<String> strValues() {
         Set<String> values = new HashSet<String>();
         for (Enum<Disposition> e : values()) {
            values.add(e.name());
         }
         return values;
      }

      public static Image getImage(Disposition sev) {
         if (sev == Accept)
            return ImageManager.getImage(FrameworkImage.ACCEPT);
         else if (sev == Reject)
            return ImageManager.getImage(FrameworkImage.REJECT);
         else if (sev == Duplicate) return ImageManager.getImage(FrameworkImage.DUPLICATE);
         return null;
      }
   };
   public static enum InjectionActivity {
      None,
      Planning,
      System_Level_Requirements,
      System_Design,
      Software_Requirements,
      Software_Design,
      Code,
      Test,
      Other;
      public static Collection<String> strValues() {
         Set<String> values = new HashSet<String>();
         for (Enum<InjectionActivity> e : values()) {
            values.add(e.name());
         }
         return values;
      }
   };

   public String getDate(String pattern) {
      if (pattern != null) return (new SimpleDateFormat(pattern)).format(date);
      return date.toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof DefectItem) {
         DefectItem di = (DefectItem) obj;
         return di.guid.equals(getGuid());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return guid.hashCode();
   }

   public String toXml() throws OseeCoreException {
      return "<severity>" + severity.name() + "</severity><disposition>" + disposition.name() +
      //
      "</disposition><injectionActivity>" + injectionActivity.name() + "</injectionActivity><date>" + date.getTime() +
      //
      "</date><user>" + user.getUserId() + "</user><description>" + description + "</description><location>" + location +
      //
      "</location><resolution>" + resolution + "</resolution><closed>" + closed + "</closed><guid>" + guid + "</guid>";
   }

   private void fromXml(String xml) throws OseeCoreException {
      this.severity = Severity.valueOf(AXml.getTagData(xml, "severity"));
      this.disposition = Disposition.valueOf(AXml.getTagData(xml, "disposition"));
      this.injectionActivity = InjectionActivity.valueOf(AXml.getTagData(xml, "injectionActivity"));
      Date date = new Date();
      date.setTime(new Long(AXml.getTagData(xml, "date")));
      this.date = date;
      this.user = UserManager.getUserByUserId(AXml.getTagData(xml, "user"));
      this.description = AXml.getTagData(xml, "description");
      this.location = AXml.getTagData(xml, "location");
      this.resolution = AXml.getTagData(xml, "resolution");
      this.closed = AXml.getTagBooleanData(xml, "closed");
      this.guid = AXml.getTagData(xml, "guid");
   }

   public Date getDate() {
      return date;
   }

   public String getCreatedDate(String pattern) {
      if (pattern != null) return (new SimpleDateFormat(pattern)).format(date);
      return date.toString();
   }

   public void setDate(Date date) {
      this.date = date;
   }

   @Override
   public String toString() {
      return severity + " - " + disposition + " - " + injectionActivity + " - " + user + " on " + getCreatedDate(XDate.MMDDYYHHMM) + "\n";
   }

   public User getUser() {
      return user;
   }

   public String toHTML(String labelFont) {
      return "DEFECT (" + severity + "): " + description + " (" + user.getName() + ")";
   }

   /**
    * @param user The user to set.
    */
   public void setUser(User user) {
      this.user = user;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getResolution() {
      return resolution;
   }

   public void setResolution(String resolution) {
      this.resolution = resolution;
   }

   public Severity getSeverity() {
      return severity;
   }

   public void setSeverity(Severity severity) {
      this.severity = severity;
   }

   public Disposition getDisposition() {
      return disposition;
   }

   public void setDisposition(Disposition disposition) {
      this.disposition = disposition;
   }

   public InjectionActivity getInjectionActivity() {
      return injectionActivity;
   }

   public void setInjectionActivity(InjectionActivity injectionActivity) {
      this.injectionActivity = injectionActivity;
   }

   public boolean isClosed() {
      return closed;
   }

   public void setClosed(boolean closed) {
      this.closed = closed;
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

}
