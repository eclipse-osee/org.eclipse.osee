/*
 * Created on Jul 15, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.walker;

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class TaskWrapper implements IActionWalkerItem {

   private final TeamWorkFlowArtifact teamArt;

   public TaskWrapper(TeamWorkFlowArtifact teamArt) {
      this.teamArt = teamArt;
   }

   @Override
   public String toString() {
      try {
         return String.format(teamArt.getTaskArtifacts().size() + " Tasks");
      } catch (OseeCoreException ex) {
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((teamArt == null) ? 0 : teamArt.hashCode());
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
      TaskWrapper other = (TaskWrapper) obj;
      if (teamArt == null) {
         if (other.teamArt != null) {
            return false;
         }
      } else if (!teamArt.equals(other.teamArt)) {
         return false;
      }
      return true;
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsImage.TASK);
   }

   @Override
   public String getName() {
      return toString();
   }

   @Override
   public void handleDoubleClick() {
      try {
         AtsUtil.openInAtsTaskEditor("Tasks", Collections.castAll(Artifact.class, teamArt.getTaskArtifacts()));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public TeamWorkFlowArtifact getTeamArt() {
      return teamArt;
   }

}
