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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class WorldAssigneeFilter extends ViewerFilter {

   Pattern p;

   public WorldAssigneeFilter() throws OseeCoreException {
      p = Pattern.compile(UserManager.getUser().getName());
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      try {
         Artifact art = (Artifact) element;
         if (art instanceof IWorldViewArtifact) {
            return p.matcher(((IWorldViewArtifact) art).getWorldViewActivePoc()).find();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return true;
   }

}
