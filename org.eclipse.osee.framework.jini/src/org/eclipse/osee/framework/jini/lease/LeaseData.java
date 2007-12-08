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
package org.eclipse.osee.framework.jini.lease;

public class LeaseData {

   private long duration;
   private long startTime;

   public LeaseData(long duration) {
      super();
      this.duration = duration;
      setStartTime();
   }

   /* package */void setDuration(long duration) {
      this.duration = duration;
   }

   /* package */void setStartTime() {
      this.startTime = (System.currentTimeMillis());
   }

   public long getDuration() {
      return duration;
   }

   public long getStartTime() {
      return startTime;
   }

   public boolean isExpired() {
      return System.currentTimeMillis() > startTime + duration;
   }

}
