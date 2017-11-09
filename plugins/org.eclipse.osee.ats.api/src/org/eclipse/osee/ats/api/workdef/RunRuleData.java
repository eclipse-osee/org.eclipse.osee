/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mark Joy
 */
@XmlRootElement
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
