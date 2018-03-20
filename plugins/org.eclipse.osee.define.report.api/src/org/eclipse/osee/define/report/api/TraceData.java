/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.api;

import java.util.List;
import java.util.Map;

/**
 * @author Ryan D. Brooks
 */
public final class TraceData {
   public final List<String> SRS;
   public final Map<String, String[]> IMPD;

   public TraceData(List<String> sRS, Map<String, String[]> iMPD) {
      this.SRS = sRS;
      this.IMPD = iMPD;
   }
}