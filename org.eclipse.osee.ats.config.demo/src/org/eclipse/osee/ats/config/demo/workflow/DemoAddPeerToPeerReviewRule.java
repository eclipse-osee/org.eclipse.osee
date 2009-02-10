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
package org.eclipse.osee.ats.config.demo.workflow;

import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.workflow.item.AtsAddDecisionReviewRule;
import org.eclipse.osee.ats.workflow.item.StateEventType;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class DemoAddPeerToPeerReviewRule extends AtsAddDecisionReviewRule {

   public static String ID = "atsAddPeerToPeerReview.test.addPeerToPeerReview";

   public DemoAddPeerToPeerReviewRule(String forState, ReviewBlockType reviewBlockType, StateEventType stateEventType) {
      super(ID + "." + forState + "." + reviewBlockType.name() + "." + stateEventType,
            ID + "." + forState + "." + reviewBlockType.name() + "." + stateEventType);
      setDescription("This is a rule created to test the Review rules.");
      setDecisionParameterValue(this, DecisionParameter.title, "Auto-created Decision Review from ruleId " + getId());
      setDecisionParameterValue(this, DecisionParameter.reviewBlockingType, reviewBlockType.name());
      setDecisionParameterValue(this, DecisionParameter.forState, forState);
      setDecisionParameterValue(this, DecisionParameter.forEvent, stateEventType.name());
      try {
         setDecisionParameterValue(this, DecisionParameter.assignees, "<99999997>");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }
}
