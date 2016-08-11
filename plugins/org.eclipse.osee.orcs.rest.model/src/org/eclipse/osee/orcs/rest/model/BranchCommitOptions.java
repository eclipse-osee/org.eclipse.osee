/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class BranchCommitOptions {
   private ArtifactId committer = ArtifactId.SENTINEL;
   private boolean archive;

   public ArtifactId getCommitter() {
      return committer;
   }

   public void setCommitter(ArtifactId committer) {
      this.committer = committer;
   }

   public boolean isArchive() {
      return archive;
   }

   public void setArchive(boolean archive) {
      this.archive = archive;
   }
}