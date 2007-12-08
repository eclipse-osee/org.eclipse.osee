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
package org.eclipse.osee.ats.world;

import java.util.regex.Pattern;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class WorldCompletedFilter extends ViewerFilter {

   Pattern p = Pattern.compile("(Completed|Cancelled)");

   public WorldCompletedFilter() {
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      WorldArtifactItem item = (WorldArtifactItem) element;
      Artifact art = item.getArtifact();
      if (art instanceof IWorldViewArtifact) {
         return !p.matcher(((IWorldViewArtifact) art).getWorldViewState()).find();
      }
      return true;
   }

}
