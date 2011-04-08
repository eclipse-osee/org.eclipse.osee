/*
 * Created on Apr 12, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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