/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.agile;

import java.util.Date;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public class AgileUtil {

   public static final String AGILE_DATE_FORMAT = DateUtil.MMDDYY;

   /**
    * @return date string in format used throughout Agile Web
    */
   public static String getDateStr(Date date) {
      return DateUtil.getDateNow(date, AGILE_DATE_FORMAT);
   }

}
