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

package org.eclipse.osee.ats.api.review;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ReviewDefectItem {

   private Date date = new Date();
   private String description = "";
   private String location = "";
   private String resolution = "";
   private String userId;
   private Long id;
   private String guid;
   private Severity severity = Severity.None;
   private Disposition disposition = Disposition.None;
   private InjectionActivity injectionActivity = InjectionActivity.None;
   private String notes = "";
   private boolean closed = false;
   public static enum Severity {
      None,
      Major,
      Minor,
      Issue;
      public static Collection<String> strValues() {
         Set<String> values = new HashSet<>();
         for (Enum<Severity> e : values()) {
            if (!e.equals(Severity.None)) {
               values.add(e.name());
            }
         }
         return values;
      }

   };

   public ReviewDefectItem(AtsUser user, Severity severity, Disposition disposition, InjectionActivity injectionActivity, String description, String resolution, String location, Date date, String notes) {
      this(user.getUserId(), severity, disposition, injectionActivity, description, resolution, location, date, notes);
   }

   public ReviewDefectItem(String userId, Severity severity, Disposition disposition, InjectionActivity injectionActivity, String description, String resolution, String location, Date date, String notes) {
      this.userId = userId;
      if (severity != null) {
         this.severity = severity;
      }
      if (disposition != null) {
         this.disposition = disposition;
      }
      if (injectionActivity != null) {
         this.injectionActivity = injectionActivity;
      }
      if (description != null) {
         this.description = description;
      }
      if (resolution != null) {
         this.resolution = resolution;
      }
      if (location != null) {
         this.location = location;
      }
      if (date != null) {
         this.date = date;
      }
      if (notes != null) {
         this.notes = notes;
      }
      id = Lib.generateId();
      this.guid = String.valueOf(id);
   }

   public ReviewDefectItem(String xml, boolean andGuid, IAtsPeerToPeerReview review) {
      fromXml(xml, andGuid, review);
   }

   public ReviewDefectItem() {
      id = Lib.generateId();
      this.guid = String.valueOf(id);
   }

   public void update(ReviewDefectItem dItem, boolean andGuid, IAtsPeerToPeerReview review) {
      fromXml(dItem.toXml(andGuid), andGuid, review);
   }

   public static enum Disposition {
      None,
      Accept,
      Reject,
      Duplicate;
      public static Collection<String> strValues() {
         Set<String> values = new HashSet<>();
         for (Enum<Disposition> e : values()) {
            values.add(e.name());
         }
         return values;
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
         Set<String> values = new HashSet<>();
         for (Enum<InjectionActivity> e : values()) {
            values.add(e.name());
         }
         return values;
      }
   };

   public String getDate(String pattern) {
      if (pattern != null) {
         return new SimpleDateFormat(pattern).format(date);
      }
      return date.toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof ReviewDefectItem) {
         ReviewDefectItem di = (ReviewDefectItem) obj;
         return di.id.equals(getId());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return id.hashCode();
   }

   public String toXml(boolean andGuid) {
      StringBuilder sb =
         new StringBuilder("<severity>" + severity.name() + "</severity><disposition>" + disposition.name() +
         //
            "</disposition><injectionActivity>" + injectionActivity.name() + "</injectionActivity><date>" + date.getTime() +
            //
            "</date><user>" + userId + "</user><description>" + description + "</description><location>" + location +
            //
            "</location><resolution>" + resolution + "</resolution><closed>" + closed + "</closed><notes>" + notes + "</notes><id>" + id + "</id>");
      if (andGuid) {
         sb.append("<guid>" + guid + "</guid>");
      }
      return sb.toString();
   }

   private void fromXml(String xml, boolean andGuid, IAtsPeerToPeerReview review) {
      this.severity = Severity.valueOf(AXml.getTagData(xml, "severity"));
      this.disposition = Disposition.valueOf(AXml.getTagData(xml, "disposition"));
      this.injectionActivity = InjectionActivity.valueOf(AXml.getTagData(xml, "injectionActivity"));
      Date date = new Date();
      date.setTime(new Long(AXml.getTagData(xml, "date")));
      this.date = date;
      this.userId = AXml.getTagData(xml, "user");
      this.description = AXml.getTagData(xml, "description");
      this.location = AXml.getTagData(xml, "location");
      this.resolution = AXml.getTagData(xml, "resolution");
      this.closed = AXml.getTagBooleanData(xml, "closed");
      this.notes = AXml.getTagData(xml, "notes");
      String idStr = AXml.getTagData(xml, "id");
      if (Strings.isNumeric(idStr)) {
         this.id = Long.valueOf(idStr);
         if (this.id < 0) {
            this.id = this.id * -1;
         }
      }
      this.guid = AXml.getTagData(xml, "guid");
      /**
       * Handle backward compatibility of guid in db. Turn into unique int if guid exists, else id is long and guid is
       * hashcode of long. After release of 26.0, either db can be converted to longs and this code removed, or leave
       * this in.
       */
      if (id == null && Strings.isValid(guid)) {
         id = Long.valueOf(guid.hashCode());
         if (this.id < 0) {
            this.id = this.id * -1;
         }
      }
      if (Strings.isInValid(guid)) {
         guid = String.valueOf(id);
      }
      if (Strings.isInValid(guid) && (id == null || id <= 0)) {
         throw new OseeArgumentException("Invalid guid/id in review %s and xml [%x]", review.toStringWithId(), xml);
      }
   }

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   @Override
   public String toString() {
      return severity + " - " + disposition + " - " + injectionActivity + " - " + userId + " on " + DateUtil.getMMDDYYHHMM(
         date) + "\n";
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
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

   public String getNotes() {
      return notes;
   }

   public void setNotes(String notes) {
      this.notes = notes;
   }

   public boolean isClosed() {
      return closed;
   }

   public void setClosed(boolean closed) {
      this.closed = closed;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setUser(AtsUser user) {
      this.userId = user.getUserId();
   }

   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

}
