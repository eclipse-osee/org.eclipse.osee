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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.DELETE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.NEW;
import java.sql.SQLException;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Paul K. Waldfogel
 */
public class WordChangesMadeToHandler extends AbstractSelectionHandler {
   private static final ArtifactPersistenceManager myArtifactPersistenceManager =
         ArtifactPersistenceManager.getInstance();
   private static final String DIFF_ARTIFACT = "DIFF_ARTIFACT";
   private static final AccessControlManager myAccessControlManager = AccessControlManager.getInstance();

   public WordChangesMadeToHandler() {
      super(new String[] {});
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      List<ArtifactChange> mySelectedArtifactChangeList = super.getArtifactChangeList();
      if (mySelectedArtifactChangeList.size() > 0) {
         ArtifactChange selectedArtifactChange = mySelectedArtifactChangeList.get(0);
         try {
            Artifact firstArtifact =
                  selectedArtifactChange.getModType() == NEW ? null : myArtifactPersistenceManager.getArtifactFromId(
                        selectedArtifactChange.getArtifact().getArtId(),
                        selectedArtifactChange.getBaselineTransactionId());
            Artifact secondArtifact =
                  selectedArtifactChange.getModType() == DELETE ? null : myArtifactPersistenceManager.getArtifactFromId(
                        selectedArtifactChange.getArtifact().getArtId(), selectedArtifactChange.getToTransactionId());

            RendererManager.getInstance().compareInJob(firstArtifact, secondArtifact, DIFF_ARTIFACT);

         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, false);
         }
      }

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
         boolean readPermission = myAccessControlManager.checkObjectPermission(changedArtifact, PermissionEnum.READ);
         boolean wordArtifactSelected = changedArtifact instanceof WordArtifact;
         return readPermission && wordArtifactSelected;
      } catch (SQLException ex) {
         OSEELog.logException(getClass(), ex, true);
         return (false);
      }
   }
}
