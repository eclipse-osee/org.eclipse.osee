/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.define.api.report;

import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author David W. Miller
 */
public abstract class ReportColumn {
   private final String name;

   public ReportColumn(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public abstract String getReportData(ArtifactReadable artifact);
}