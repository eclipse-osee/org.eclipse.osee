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
package org.eclipse.osee.framework.skynet.core.usage;

import java.sql.Timestamp;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;

/**
 * @author Robert A. Fisher
 */
abstract class UsageEntry {
   private final Timestamp eventTime;
   private final String details;

   protected UsageEntry() {
      this(null);
   }

   protected UsageEntry(String details) {
      this(GlobalTime.GreenwichMeanTimestamp(), details);
   }

   /**
    * @param eventTime
    */
   protected UsageEntry(final Timestamp eventTime, String details) {
      this.eventTime = eventTime;
      this.details = details == null ? "" : Strings.truncate(details, 4000);
   }

   /**
    * @return the eventTime
    */
   public final Timestamp getEventTime() {
      return eventTime;
   }

   protected abstract int getEventOrdinal();

   public String getDetails() {
      return details;
   }

   public abstract String getDescription();
}
