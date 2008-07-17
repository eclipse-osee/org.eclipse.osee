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
package org.eclipse.osee.framework.search.engine.internal;

import org.eclipse.osee.framework.search.engine.ITaskStatistics;

/**
 * @author Roberto E. Escobar
 */
public class TaskStatistics implements Cloneable, ITaskStatistics {
   private long gammaId;
   private int totalTags;
   private long processingTime;
   private long waitTime;

   TaskStatistics(long gammaId, int totalTags, long processingTime, long waitTime) {
      super();
      this.gammaId = gammaId;
      this.totalTags = totalTags;
      this.processingTime = processingTime;
      this.waitTime = waitTime;
   }

   public long getGammaId() {
      return gammaId;
   }

   public int getTotalTags() {
      return totalTags;
   }

   public long getProcessingTime() {
      return processingTime;
   }

   public long getWaitTime() {
      return waitTime;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#clone()
    */
   @Override
   protected TaskStatistics clone() throws CloneNotSupportedException {
      TaskStatistics other = (TaskStatistics) super.clone();
      other.gammaId = this.gammaId;
      other.totalTags = this.totalTags;
      other.processingTime = this.processingTime;
      other.waitTime = this.waitTime;
      return other;
   }
}
