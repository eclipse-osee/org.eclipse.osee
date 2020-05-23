/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.define.api;

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