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

package org.eclipse.osee.framework.ui.skynet.render.compare;

import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;

public interface IComparator {

   void compare(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, ArtifactDelta artifactDelta, String pathPrefix);

   void compare(CompareDataCollector collector, Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, String pathPrefix);

   void compareArtifacts(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, Collection<ArtifactDelta> artifactDeltas, String pathPrefix);

}
