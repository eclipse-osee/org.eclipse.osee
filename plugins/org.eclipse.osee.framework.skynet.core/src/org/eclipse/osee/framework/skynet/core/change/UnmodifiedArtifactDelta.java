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

package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Branden W. Phillips
 */
public class UnmodifiedArtifactDelta extends ArtifactDelta {

   /**
    * ArtifactDelta's are objects to contain new and old versions of artifacts, sometimes including the transaction id's
    * of that change. These are used to assist in creating word file comparisons. This class, UnmodifiedArtifactDelta,
    * is used to make a mock version of those ArtifactDeltas that is just the same exact artifact as the start and end
    * artifact. This is primarily used for adding in contextual artifacts for word comparisons that only have 1 version
    * of the artifact. Utilizing this method tells the processing that the artifact content only needs to be rendered
    * once instead of twice.
    */
   public UnmodifiedArtifactDelta(Artifact artifact) {
      super(artifact, artifact);
   }
}
