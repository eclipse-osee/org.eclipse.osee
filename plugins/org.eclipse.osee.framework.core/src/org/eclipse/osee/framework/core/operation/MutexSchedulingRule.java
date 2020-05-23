/*********************************************************************
 * Copyright (c) 2011 Boeing
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