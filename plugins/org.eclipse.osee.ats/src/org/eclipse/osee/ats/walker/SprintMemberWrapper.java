/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.walker;

import org.eclipse.osee.ats.AtsArtifactImageProvider;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class SprintMemberWrapper implements IActionWalkerItem {

   private final IAgileSprint sprint;

   public SprintMemberWrapper(IAgileSprint sprint) {
      this.sprint = sprint;
   }

   @Override
   public String toString() {
      try {
         return String.format(AtsClientService.get().getAgileService().getItems(sprint).size() + " Members");
      } catch (OseeCoreException ex) {
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (sprint == null ? 0 : sprint.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      SprintMemberWrapper other = (SprintMemberWrapper) obj;
      if (sprint == null) {
         if (other.sprint != null) {
            return false;
         }
      } else if (!sprint.equals(other.sprint)) {
         return false;
      }
      return true;
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_SPRINT));
   }

   @Override
   public String getName() {
      return toString();
   }

   @Override
   public void handleDoubleClick() {
      try {

         AtsUtil.openInAtsWorldEditor(String.format("Goal [%s] Members", sprint.getName()), Collections.castAll(
            Artifact.class, AtsObjects.getArtifacts(AtsClientService.get().getAgileService().getItems(sprint))));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
