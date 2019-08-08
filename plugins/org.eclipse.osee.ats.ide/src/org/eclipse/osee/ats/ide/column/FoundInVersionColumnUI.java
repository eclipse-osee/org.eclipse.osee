/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.column;

import java.util.Arrays;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumnIdColumn;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Jeremy A. Midvidy
 */
public class FoundInVersionColumnUI extends XViewerAtsColumnIdColumn {

   public static FoundInVersionColumnUI instance = new FoundInVersionColumnUI();

   public FoundInVersionColumnUI() {
      super(AtsColumnToken.FoundInVersionColumn);
   }

   public static FoundInVersionColumnUI getInstance() {
      return instance;
   }

   public static boolean promptFoundInVersion(IAtsWorkItem art, RelationTypeSide toRelate) {
      IAtsVersion oldVersion = AtsClientService.get().getVersionService().getFoundInVersion(art);
      if (TargetedVersionColumnUI.promptChangeVersion(Arrays.asList((TeamWorkFlowArtifact) art), null, null)) {
         if (TargetedVersionColumnUI.getSelectedVersion() == null) { // hit "cancel" on version select popup
            return false;
         }
         if (oldVersion == null || oldVersion.notEqual(TargetedVersionColumnUI.getSelectedVersion())) {
            IAtsChangeSet changes = AtsClientService.get().createChangeSet("Update Found-In-Version");
            changes.setRelation(art, toRelate, TargetedVersionColumnUI.getSelectedVersion());
            changes.executeIfNeeded();
            return true;

         }
      }
      return false;
   }
}
