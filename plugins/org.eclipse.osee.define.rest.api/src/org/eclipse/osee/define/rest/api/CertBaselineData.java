/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.define.rest.api;

import java.util.Date;
import java.util.List;

/**
 * @author Ryan D. Brooks
 */
public final class CertBaselineData implements Comparable<CertBaselineData> {
   public String changeId;
   public String baselinedByUserId;
   public Date baselinedTimestamp;
   public Integer reviewId;
   public String reviewStoryId;
   public List<String> files;
   public String eventName;

   @Override
   public int compareTo(CertBaselineData other) {
      return baselinedTimestamp.compareTo(other.baselinedTimestamp);
   }
}