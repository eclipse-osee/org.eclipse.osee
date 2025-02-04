/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.testscript;

import java.util.Date;
import org.eclipse.osee.framework.core.data.ArtifactId;

public class ResultToPurge {
   private ArtifactId artId;
   private String name;
   private Date executionDate;
   private String filePath;
   private ArtifactId ciSet;

   public ArtifactId getArtId() {
      return artId;
   }

   public void setArtId(ArtifactId artId) {
      this.artId = artId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Date getExecutionDate() {
      return executionDate;
   }

   public void setExecutionDate(Date executionDate) {
      this.executionDate = executionDate;
   }

   public String getFilePath() {
      return filePath;
   }

   public void setFilePath(String filePath) {
      this.filePath = filePath;
   }

   public ArtifactId getCiSet() {
      return ciSet;
   }

   public void setCiSet(ArtifactId ciSet) {
      this.ciSet = ciSet;
   }

}
