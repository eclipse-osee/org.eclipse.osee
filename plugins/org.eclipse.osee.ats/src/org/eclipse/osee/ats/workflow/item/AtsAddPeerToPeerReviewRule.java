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
package org.eclipse.osee.ats.workflow.item;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.ats.core.workdef.RuleDefinition;
import org.eclipse.osee.ats.core.workdef.StateEventType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.utility.UsersByIds;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsAddPeerToPeerReviewRule {

   public final static String ID = "atsAddPeerToPeerReview";
   public static enum PeerToPeerParameter {
      title,
      forState,
      forEvent,
      reviewBlockingType,
      assignees,
      location,
      description
   };

   public static void setPeerToPeerParameterValue(WorkRuleDefinition workRuleDefinition, PeerToPeerParameter decisionParameter, String value) {
      workRuleDefinition.addWorkDataKeyValue(decisionParameter.name(), value);
   }

   public static String getPeerToPeerParameterValue(RuleDefinition ruleDefinition, PeerToPeerParameter decisionParameter) {
      return ruleDefinition.getWorkDataValue(decisionParameter.name());
   }

   public static Collection<IBasicUser> getAssigneesOrDefault(WorkRuleDefinition workRuleDefinition) throws OseeCoreException {
      String value = getPeerToPeerParameterValue(workRuleDefinition, PeerToPeerParameter.assignees);
      if (!Strings.isValid(value)) {
         return Arrays.asList(new IBasicUser[] {UserManager.getUser()});
      }
      Collection<IBasicUser> users = UsersByIds.getUsers(value);
      if (users.isEmpty()) {
         users.add(UserManager.getUser());
      }
      return users;
   }

   public static String getReviewTitle(WorkRuleDefinition workRuleDefinition) {
      return getPeerToPeerParameterValue(workRuleDefinition, PeerToPeerParameter.title);
   }

   public static String getRelatedToState(WorkRuleDefinition workRuleDefinition) {
      return getPeerToPeerParameterValue(workRuleDefinition, PeerToPeerParameter.forState);
   }

   public static String getLocation(WorkRuleDefinition workRuleDefinition) {
      return getPeerToPeerParameterValue(workRuleDefinition, PeerToPeerParameter.location);
   }

   public static StateEventType getStateEventType(WorkRuleDefinition workRuleDefinition) {
      String value = getPeerToPeerParameterValue(workRuleDefinition, PeerToPeerParameter.forEvent);
      if (!Strings.isValid(value)) {
         return null;
      }
      return StateEventType.valueOf(value);
   }

   public static ReviewBlockType getReviewBlockTypeOrDefault(WorkRuleDefinition workRuleDefinition) {
      String value = getPeerToPeerParameterValue(workRuleDefinition, PeerToPeerParameter.reviewBlockingType);
      if (!Strings.isValid(value)) {
         return null;
      }
      return ReviewBlockType.valueOf(value);
   }

   public static String getPeerToPeerParameterValue(WorkRuleDefinition workRuleDefinition, PeerToPeerParameter decisionParameter) {
      return workRuleDefinition.getWorkDataValue(decisionParameter.name());
   }

}
