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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class ArtifactViewerSorter extends ViewerSorter {

   private final boolean reverse;

   public ArtifactViewerSorter() {
      this(false);
   }

   public ArtifactViewerSorter(boolean reverse) {
      super();
      this.reverse = reverse;
   }

   @SuppressWarnings("unchecked")
   public int compare(Viewer viewer, Object o1, Object o2) {
      if (reverse)
         return getComparator().compare(((Artifact) o2).getDescriptiveName(), ((Artifact) o1).getDescriptiveName());
      else
         return getComparator().compare(((Artifact) o1).getDescriptiveName(), ((Artifact) o2).getDescriptiveName());
   }
}