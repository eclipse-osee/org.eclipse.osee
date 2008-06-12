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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

public class WorldCompletedFilter extends ViewerFilter {

   Pattern p = Pattern.compile("(Completed|Cancelled)");

   public WorldCompletedFilter() {
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      try {
         Artifact art = (Artifact) element;
         if (art instanceof IWorldViewArtifact) {
            return !p.matcher(((IWorldViewArtifact) art).getWorldViewState()).find();
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return true;
   }

}
