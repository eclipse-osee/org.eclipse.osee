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

import org.eclipse.osee.ats.core.workdef.RuleDefinition;

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

   public static String getPeerToPeerParameterValue(RuleDefinition ruleDefinition, PeerToPeerParameter decisionParameter) {
      return ruleDefinition.getWorkDataValue(decisionParameter.name());
   }

}
