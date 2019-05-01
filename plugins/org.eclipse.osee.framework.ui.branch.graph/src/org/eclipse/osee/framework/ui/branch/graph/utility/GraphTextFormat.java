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
package org.eclipse.osee.framework.ui.branch.graph.utility;

import java.text.DateFormat;
import java.util.Date;

/**
 * @author Roberto E. Escobar
 */
public class GraphTextFormat {

   private static DateFormat dateFormat = null;

   private GraphTextFormat() {
   }

   static {
      dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
   }

   public static synchronized String formatDate(Date date) {
      return dateFormat.format(date);
   }
}
