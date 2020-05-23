/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.util.time;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Jeff C. Phillips
 */
public class GlobalTime {

   public static Timestamp GreenwichMeanTimestamp() {
      return new Timestamp(GreenwichMeanTimeMillis());
   }

   public static long GreenwichMeanTimeMillis() {
      return Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
   }

}
