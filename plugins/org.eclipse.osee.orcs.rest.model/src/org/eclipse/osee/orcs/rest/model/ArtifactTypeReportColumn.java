/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author David W. Miller
 */
public class ArtifactTypeReportColumn extends ReportColumn {

   public ArtifactTypeReportColumn(String name) {
      super(name);
   }

   @Override
   public String getReportData(ArtifactReadable artifact) {
      if (artifact == null) {
         return "";
      }
      return artifact.getArtifactType().getName();
   }
}