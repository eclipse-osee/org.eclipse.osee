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

import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;

/**
 * @author Mark Joy
 */
public interface IAtsRuleDefinition {

   public String getName();

   public String getTitle();

   public String getDescription();

   public List<IAtsUser> getAssignees();

   public List<RuleLocations> getRuleLocs();

   public List<RuleEventType> getRuleEvents();

   void addRuleEvent(RuleEventType ruleEventType);

}
