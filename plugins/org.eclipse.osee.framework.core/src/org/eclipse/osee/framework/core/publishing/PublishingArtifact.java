/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.publishing;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * An extension of the {@link ArtifactReadable} interface used by shared Client/Server publishing code.
 *
 * @author Loren K. Ashley
 */

public interface PublishingArtifact extends ArtifactReadable, ToMessage {

   /**
    * Clears the end of section flag for an artifact.
    */

   void clearEndOfSection();

   /**
    * Clears the start of section flag for an artifact.
    */

   void clearStartOfSection();

   /**
    * Gets the immediate hierarchical children of the artifact as {@link PublishingArtifact}s. The outline level of the
    * returned artifacts will be one greater than the parent artifact. The first child's start of section flag will be
    * set and last child's end of section flags will be set.
    *
    * @return a list of the immediate hierarchical children.
    */

   public List<PublishingArtifact> getChildrenAsPublishingArtifacts();

   /**
    * The outlining level is the hierarchical depth of the artifact. The top level is level 0.
    *
    * @return the artifact outline level.
    */

   int getOutlineLevel();

   /**
    * Predicate to determine if the artifact is the last artifact under a parent at it's hierarchy level.
    *
    * @return end of section flag.
    */

   boolean isEndOfSection();

   /**
    * Predicate to determine if the artifact is the first artifact under a parent at it's hierarchy level.
    *
    * @return start of section flag.
    */

   boolean isStartOfSection();

   /**
    * Sets the artifact's end of section flag.
    */

   void setEndOfSection();

   /**
    * Sets the artifact's outlining level.
    *
    * @param level the outlining level to set.
    */

   void setOutlineLevel(int level);

   /**
    * Sets the artifacts start of section flag.
    */

   void setStartOfSection();
}

/* EOF */
