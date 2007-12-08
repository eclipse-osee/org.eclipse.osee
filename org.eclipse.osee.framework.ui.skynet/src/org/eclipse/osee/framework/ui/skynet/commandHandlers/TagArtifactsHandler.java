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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.util.Collection;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.TagArtifactsJob;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Paul K. Waldfogel
 */
public class TagArtifactsHandler extends AbstractSelectionHandler {
   Collection<Artifact> mySelectedArtifactCollection = null;

   public TagArtifactsHandler() {
      super(new String[] {});
   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      List<Artifact> mySelectedArtifactList = super.getArtifactList();
      mySelectedArtifactCollection = mySelectedArtifactList;
      tagArtifactsAction.run();
      return null;
   }
   private final Action tagArtifactsAction = new Action("&Tag Artifact(s)", Action.AS_PUSH_BUTTON) {
      @Override
      public void run() {
         Jobs.startJob(new TagArtifactsJob(mySelectedArtifactCollection));
      }
   };

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.commandHandlers.AbstractArtifactSelectionHandler#permissionLevel()
    */
   @Override
   protected PermissionEnum permissionLevel() {
      return PermissionEnum.READ;
   }

   //   tagMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isFullAccess());
   @Override
   public boolean isEnabled() {
      try {
         List<Artifact> mySelectedArtifactList = super.getArtifactList();
         return mySelectedArtifactList.size() > 0;
      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, true);
         return false;
      }
   }

}
