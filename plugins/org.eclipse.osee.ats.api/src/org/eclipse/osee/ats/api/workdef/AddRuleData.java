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

/**
 * @author Mark Joy
 */
public class AddRuleData {
   Long configId;
   String ruleName;

   public Long getConfigItemId() {
      return configId;
   }

   public void setConfigItemId(Long configItemIds) {
      this.configId = configItemIds;
   }

   public String getRuleName() {
      return ruleName;
   }

   public void setRuleName(String ruleName) {
      this.ruleName = ruleName;
   }

}
