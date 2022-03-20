/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.world;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;

/**
 * @author Donald G. Dunne
 */
public interface IWorldViewerEventHandler {

   default public WorldXViewer getWorldXViewer() {
      return null;
   }

   public void relationsModifed(Collection<Artifact> relModifiedArts, Collection<Artifact> goalMemberReordered, Collection<Artifact> sprintMemberReordered);

   public boolean isDisposed();

   public void handleColumnEvents(ArtifactEvent artifactEvent, WorldXViewer worldXViewer);

}
