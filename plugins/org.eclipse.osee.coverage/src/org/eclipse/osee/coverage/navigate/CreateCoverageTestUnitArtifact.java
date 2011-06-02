/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.navigate;

import org.eclipse.osee.coverage.store.ArtifactTestUnitStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author John Misinco
 */
public class CreateCoverageTestUnitArtifact extends XNavigateItem {

   public CreateCoverageTestUnitArtifact(XNavigateItem parent) {
      super(parent, "Create Coverage Test Unit Artifact", FrameworkImage.GEAR);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {

      if (CoverageUtil.getBranch() == null) {
         if (!CoverageUtil.getBranchFromUser(false)) {
            return;
         }
      }

      IOseeStatement chStmt = ConnectionHandler.getStatement();
      StringBuilder sb = new StringBuilder();
      try {
         String sql = "select * from osee_cvg_testunits order by name_id asc";
         chStmt.runPreparedQuery(sql);
         boolean firstTime = true;
         while (chStmt.next()) {
            if (!firstTime) {
               sb.append("\n");
            }
            sb.append(chStmt.getInt("name_id"));
            sb.append("|");
            sb.append(chStmt.getString("name"));
            firstTime = false;
         }
      } finally {
         chStmt.close();
      }
      Artifact art =
         ArtifactQuery.getOrCreate(ArtifactTestUnitStore.COVERAGE_GUID, null, CoreArtifactTypes.GeneralData,
            CoverageUtil.getBranch());
      art.setSoleAttributeFromString(CoreAttributeTypes.GeneralStringData, sb.toString());
      art.persist();
      AWorkbench.popup("Completed", "Complete");
   }
}
