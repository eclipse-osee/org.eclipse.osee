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
package org.eclipse.osee.framework.ui.plugin.event;

import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;

/**
 * @author Donald G. Dunne
 */
public class UnloadedArtifact extends DefaultBasicGuidArtifact {

   private int artifactTypeId;
   private int artifactId;

   public UnloadedArtifact(String branchGuid, int artifactTypeId, String artTypeGuid, int artifactId, String artifactGuid) {
      this(branchGuid, artTypeGuid, artifactGuid);
      this.artifactTypeId = artifactTypeId;
      this.artifactId = artifactId;
   }

   public UnloadedArtifact(String branchGuid, String artTypeGuid, String guid) {
      super(branchGuid, artTypeGuid, guid);
   }

   public int getArtifactId() {
      return artifactId;
   }

   public void setArtifactId(int artifactId) {
      this.artifactId = artifactId;
   }

   public int getArtifactTypeId() {
      return artifactTypeId;
   }

   public void setArtifactTypeId(int artifactTypeId) {
      this.artifactTypeId = artifactTypeId;
   }

}
