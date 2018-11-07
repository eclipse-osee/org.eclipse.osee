/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.define.api;

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