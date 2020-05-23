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

package org.eclipse.osee.define.api;

import java.util.Date;
import java.util.List;

/**
 * @author Ryan D. Brooks
 */
public final class CertFileData {
   public String path;
   public List<BaselineData> baselinedInfo;

   public final class BaselineData {
      public String baselinedChangeId;
      public Date baselinedTimestamp;
   }
}