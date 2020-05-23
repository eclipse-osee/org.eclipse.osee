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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;

public class WholeWordCompare extends AbstractWordCompare {
   public WholeWordCompare(FileSystemRenderer renderer) {
      super(renderer, CoreAttributeTypes.WholeWordContent);
   }

   @Override
   public void compareArtifacts(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, Collection<ArtifactDelta> artifactDeltas, String diffPrefix) {
      for (ArtifactDelta entry : artifactDeltas) {
         compare(monitor, collector, presentationType, entry, diffPrefix);
      }
   }
}