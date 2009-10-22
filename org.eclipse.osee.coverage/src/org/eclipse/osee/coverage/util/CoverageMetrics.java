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
package org.eclipse.osee.coverage.util;

import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public class CoverageMetrics {

   public static Pair<Integer, String> getPercent(int complete, int total) {
      if (total == 0 || complete == 0) return new Pair<Integer, String>(0, getPercentString(0, complete, total));
      Double percent = new Double(complete);
      percent = percent / total;
      percent = percent * 100;
      return new Pair<Integer, String>(percent.intValue(), getPercentString(percent.intValue(), complete, total));
   }

   public static String getPercentString(int percent, int complete, int total) {
      return String.format("%d%% %d/%d", percent, complete, total);
   }
}
