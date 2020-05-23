/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.workdef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mark Joy
 */
public class RunRuleData {

   List<Long> workItemIds;
   RuleEventType ruleEventType;

   public RuleEventType getRuleEventType() {
      return ruleEventType;
   }

   public void setRuleEventType(RuleEventType ruleEventType) {
      this.ruleEventType = ruleEventType;
   }

   public List<Long> getWorkItemIds() {
      if (workItemIds == null) {
         workItemIds = new ArrayList<>();
      }
      return workItemIds;
   }

   public void setWorkItemIds(List<Long> workItemIds) {
      this.workItemIds = workItemIds;
   }

}
