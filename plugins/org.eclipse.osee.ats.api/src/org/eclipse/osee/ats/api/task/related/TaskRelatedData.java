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
package org.eclipse.osee.ats.api.task.related;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public class TaskRelatedData {
   private final boolean deleted;
   private final ArtifactToken headArtifact;
   private final ArtifactToken latestArt;
   private final Result result;

   public TaskRelatedData(Result result) {
      this(false, null, null, result);
   }

   public TaskRelatedData(boolean deleted, ArtifactToken headArtifact, ArtifactToken latestArt, Result result) {
      this.deleted = deleted;
      this.headArtifact = headArtifact;
      this.latestArt = latestArt;
      this.result = result;
   }

   public boolean isDeleted() {
      return deleted;
   }

   public ArtifactToken getHeadArtifact() {
      return headArtifact;
   }

   public Result getResult() {
      return result;
   }

   public ArtifactToken getLatestArt() {
      return latestArt;
   }
}
