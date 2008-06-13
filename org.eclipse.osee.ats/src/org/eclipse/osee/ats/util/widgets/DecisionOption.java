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
package org.eclipse.osee.ats.util.widgets;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

public class DecisionOption {
   private String name;
   private Collection<User> assignees = new HashSet<User>();
   private boolean followupRequired;

   public DecisionOption(String name, Collection<User> assignees, boolean followup) {
      this.name = name;
      this.followupRequired = followup;
      if (assignees != null) this.assignees = assignees;
   }

   public DecisionOption(String name, User assignee, boolean followup) {
      this.name = name;
      this.followupRequired = followup;
      if (assignee != null) this.assignees.add(assignee);
   }

   public DecisionOption(String name) {
      this(name, (User) null, false);
   }

   public DecisionOption() {
      this("", (User) null, false);
   }

   public String toString() {
      return name;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof DecisionOption) {
         DecisionOption state = (DecisionOption) obj;
         if (!state.name.equals(name)) return false;
         return true;
      }
      return super.equals(obj);
   }

   public Collection<User> getAssignees() {
      return assignees;
   }

   /**
    * Sets the assigness but DOES NOT write to SMA. This method should NOT be called outside the SMAManager.
    * 
    * @param assignees
    */
   public void setAssignees(Collection<User> assignees) {
      this.assignees.clear();
      if (assignees != null) this.assignees.addAll(assignees);
   }

   /**
    * Sets the assignes but DOES NOT write to SMA. This method should NOT be called outside the SMAManager.
    * 
    * @param assignee
    */
   public void setAssignee(User assignee) {
      this.assignees.clear();
      if (assignee != null) this.assignees.add(assignee);
   }

   /**
    * @param assignee
    */
   public void addAssignee(User assignee) {
      if (assignee != null) this.assignees.add(assignee);
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * @param name The name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

   public String toXml() throws SQLException, MultipleAttributesExist {
      StringBuffer sb = new StringBuffer(name);
      sb.append(";");
      for (User u : assignees)
         sb.append("<" + u.getUserId() + ">");
      sb.append(";");
      sb.append(followupRequired);
      return sb.toString();
   }

   public Result setFromXml(String xml) {
      Matcher m = Pattern.compile("^(.*?);(.*?);(.*?)$").matcher(xml);
      if (m.find()) {
         name = m.group(1);
         if (name.equals("")) return new Result("Invalid name");
         if (m.group(2).toLowerCase().equals("followup"))
            followupRequired = true;
         else if (m.group(2).toLowerCase().equals("completed"))
            followupRequired = false;
         else
            return new Result("Invalid followup string \"" + m.group(2) + "\"\nShould be followup or completed");
         m = Pattern.compile("<(.*?)>").matcher(m.group(2));
         while (m.find()) {
            try {
               assignees.add(SkynetAuthentication.getUserByUserId(m.group(3)));
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, false);
            }
         }
         if (followupRequired && assignees.size() == 0)
            return new Result("If followup is specified, must set assignees.\nShould be: <userid><userid>");
         else if (!followupRequired && assignees.size() > 0) return new Result(
               "If completed is specified, don't specify assigness.  Leave blank.\n");
      } else
         new Result(
               "Can't unpack decision option data => " + xml + "\n\n" + "must be in format: \"Name;(followup|completed);<userid1><userid2>\"" + "where true if followup is required; false if not.  If followup required, assignees will be userid1, userid2.");
      return Result.TrueResult;
   }

   /**
    * @return the followupRequired
    */
   public boolean isFollowupRequired() {
      return followupRequired;
   }

   /**
    * @param followupRequired the followupRequired to set
    */
   public void setFollowupRequired(boolean followupRequired) {
      this.followupRequired = followupRequired;
   }

}
