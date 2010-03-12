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
