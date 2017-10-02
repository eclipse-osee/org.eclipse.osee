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
package org.eclipse.osee.ats.core.client.review.defect;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.User;

/**
 * @author Donald G. Dunne
 */
public class ReviewDefectItem {

   private Date date = new Date();
   private String description = "";
   private String location = "";
   private String resolution = "";
   private String userId;
   private String guid = GUID.create();
   private Severity severity = Severity.None;
   private Disposition disposition = Disposition.None;
   private InjectionActivity injectionActivity = InjectionActivity.None;
   private boolean closed = false;
   public static enum Severity {
      None,
      Major,
      Minor,
      Issue;
      public static Collection<String> strValues() {
         Set<String> values = new HashSet<>();
         for (Enum<Severity> e : values()) {
            values.add(e.name());
         }
         return values;
      }

   };

   public ReviewDefectItem()  {
      userId = AtsClientService.get().getUserService().getCurrentUser().getUserId();
   }

   public ReviewDefectItem(IAtsUser user, Severity severity, Disposition disposition, InjectionActivity injectionActivity, String description, String resolution, String location, Date date)  {
      this(user.getUserId(), severity, disposition, injectionActivity, description, resolution, location, date);
   }

   public ReviewDefectItem(String userId, Severity severity, Disposition disposition, InjectionActivity injectionActivity, String description, String resolution, String location, Date date) {
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
   }

   public ReviewDefectItem(String xml) {
      fromXml(xml);
   }

   public void update(ReviewDefectItem dItem) {
      fromXml(dItem.toXml());
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
         return di.guid.equals(getGuid());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return guid.hashCode();
   }

   public String toXml() {
      return "<severity>" + severity.name() + "</severity><disposition>" + disposition.name() +
      //
      "</disposition><injectionActivity>" + injectionActivity.name() + "</injectionActivity><date>" + date.getTime() +
      //
      "</date><user>" + userId + "</user><description>" + description + "</description><location>" + location +
      //
      "</location><resolution>" + resolution + "</resolution><closed>" + closed + "</closed><guid>" + guid + "</guid>";
   }

   private void fromXml(String xml) {
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
      this.guid = AXml.getTagData(xml, "guid");
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

   public IAtsUser getUser()  {
      return AtsClientService.get().getUserService().getUserById(userId);
   }

   public String getUserId() {
      return userId;
   }

   public String toHTML(String labelFont)  {
      return "DEFECT (" + severity + "): " + description + " (" + getUser().getName() + ")";
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

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public void setUser(User user)  {
      this.userId = user.getUserId();
   }

}
