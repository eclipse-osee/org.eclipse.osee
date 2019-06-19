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
package org.eclipse.osee.ats.api.review;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public class DecisionReviewOption implements IAtsDecisionReviewOption {
   private String name;
   private final Collection<String> assignees = new HashSet<>();
   private final Collection<String> names = new HashSet<>();
   private boolean followupRequired;

   @Override
   public int hashCode() {
      int result = 17;
      result = 31 * result + name.hashCode();

      return result;
   }

   public DecisionReviewOption(String name) {
      this(name, (IAtsUser) null, false);
   }

   public DecisionReviewOption(String name, IAtsUser assignee, boolean followup) {
      this(name, assignee == null ? Collections.emptyList() : Collections.singleton(assignee), followup);
   }

   public DecisionReviewOption(String name, boolean followup, Collection<String> userIds) {
      this.name = name;
      this.followupRequired = followup;
      if (userIds != null) {
         this.assignees.addAll(userIds);
      }
   }

   public DecisionReviewOption(String name, Collection<IAtsUser> assignees, boolean followup) {
      this.name = name;
      this.followupRequired = followup;
      if (assignees != null) {
         for (IAtsUser user : assignees) {
            this.assignees.add(user.getUserId());
         }
      }
   }

   @Override
   public String toString() {
      return name;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof DecisionReviewOption) {
         DecisionReviewOption state = (DecisionReviewOption) obj;
         if (!state.name.equals(name)) {
            return false;
         }
         return true;
      }
      return super.equals(obj);
   }

   public Collection<String> getAssignees() {
      return assignees;
   }

   /**
    * Sets the assigness but DOES NOT write to SMA. This method should NOT be called outside the StateMachineArtifact.
    */
   public void setAssignees(Collection<IAtsUser> assignees) {
      this.assignees.clear();
      if (assignees != null) {
         for (IAtsUser user : assignees) {
            this.assignees.add(user.getUserId());
         }
      }
   }

   /**
    * Sets the assignes but DOES NOT write to SMA. This method should NOT be called outside the StateMachineArtifact.
    */
   public void setAssignee(IAtsUser assignee) {
      this.assignees.clear();
      if (assignee != null) {
         this.assignees.add(assignee.getUserId());
      }
   }

   public void addAssignee(IAtsUser assignee) {
      if (assignee != null) {
         this.assignees.add(assignee.getUserId());
      }
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public void setName(String name) {
      this.name = name;
   }

   public String toXml() {
      StringBuffer sb = new StringBuffer(name);
      sb.append(";");
      for (String userId : assignees) {
         sb.append("<" + userId + ">");
      }
      sb.append(";");
      sb.append(followupRequired);
      return sb.toString();
   }

   public Result setFromXml(String xml) {
      Matcher m = Pattern.compile("^(.*?);(.*?);(.*)$").matcher(xml);
      if (m.find()) {
         String toState = m.group(2).toLowerCase();
         name = m.group(1);
         if (name.equals("")) {
            return new Result("Invalid name");
         }
         if (toState.equals("followup")) {
            followupRequired = true;
         } else if (toState.equals("completed")) {
            followupRequired = false;
         } else {
            return new Result("Invalid followup string \"" + m.group(2) + "\"\nShould be followup or completed");
         }
         m = Pattern.compile("<(.*?)>").matcher(m.group(3));
         while (m.find()) {
            assignees.add(m.group(1));
         }
         if (followupRequired && assignees.isEmpty()) {
            return new Result("If followup is specified, must set assignees.\nShould be: <userid><userid>");
         } else if (!followupRequired && assignees.size() > 0) {
            return new Result("If completed is specified, don't specify assigness.  Leave blank.\n");
         }
      } else {
         return new Result(
            "Can't unpack decision option data => " + xml + "\n\n" + "must be in format: \"Name;(followup|completed);<userid1><userid2>\"" + "where true if followup is required; false if not.  If followup required, assignees will be userid1, userid2.");
      }
      return Result.TrueResult;
   }

   @Override
   public boolean isFollowupRequired() {
      return followupRequired;
   }

   @Override
   public void setFollowupRequired(boolean followupRequired) {
      this.followupRequired = followupRequired;
   }

   @Override
   public Collection<String> getUserIds() {
      return assignees;
   }

   @Override
   public void setUserIds(List<String> userIds) {
      this.assignees.clear();
      this.assignees.addAll(userIds);
   }

   @Override
   public Collection<String> getUserNames() {
      return names;
   }

   @Override
   public void setUserNames(List<String> userNames) {
      this.names.clear();
      this.names.addAll(userNames);
   }

}
