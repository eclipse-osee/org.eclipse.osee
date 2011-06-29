/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.operation;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * The Mutex scheduling rule is so named because every Job run with a given instance of this rule is mutually exclusive.
 * In other words all such jobs run sequentially (one at a time).
 * 
 * @author Ryan D. Brooks
 */
public class MutexSchedulingRule implements ISchedulingRule {

   @Override
   public boolean contains(ISchedulingRule rule) {
      return rule == this;
   }

   @Override
   public boolean isConflicting(ISchedulingRule rule) {
      return rule == this;
   }

}