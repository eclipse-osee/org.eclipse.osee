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

import java.sql.SQLException;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Jeff C. Phillips
 * @author Paul K. Waldfogel
 */
public class OpenInEditorHandler extends AbstractSelectionHandler {
   private static final AccessControlManager myAccessControlManager = AccessControlManager.getInstance();

   public OpenInEditorHandler() {
      super(new String[] {});
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent myExecutionEvent) throws ExecutionException {
      ArtifactEditor.editArtifacts(super.getArtifactList());
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.commandHandlers.AbstractArtifactSelectionHandler#permissionLevel()
    */
   @Override
   protected PermissionEnum permissionLevel() {
      return PermissionEnum.READ;
   }

   @Override
   public boolean isEnabled() {
      List<ArtifactChange> mySelectedArtifactChangeList = super.getArtifactChangeList();
      ArtifactChange mySelectedArtifactChange = mySelectedArtifactChangeList.get(0);
      Artifact changedArtifact = null;
      try {
         changedArtifact = mySelectedArtifactChange.getArtifact();
         boolean artifactSelected = changedArtifact != null;
         boolean readPermission = myAccessControlManager.checkObjectPermission(changedArtifact, PermissionEnum.READ);
         //         boolean wordArtifactSelected = changedArtifact instanceof WordArtifact;
         //         boolean modifiedWordArtifactSelected = wordArtifactSelected && mySelectedArtifactChange.getModType() == CHANGE;
         //         boolean conflictedWordArtifactSelected = modifiedWordArtifactSelected && mySelectedArtifactChange.getChangeType() == ChangeType.CONFLICTING;
         return artifactSelected && readPermission;
      } catch (SQLException ex) {
         OSEELog.logException(getClass(), ex, true);
         return (false);
      }
   }

}
