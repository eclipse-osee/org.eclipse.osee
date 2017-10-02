/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author John Misinco
 */
public class CreateBranchGuidToUuidArtifactBlam extends AbstractBlam {

   private static final String MAPPING_ART = "ABKY9QDQLSaHQBiRC7wA";

   @Override
   public String getXWidgetsXml()  {
      return "<xWidgets></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return String.format("Creates and/or updates artifact [%s] with branch guid to uuid mapping", MAPPING_ART);
   }

   @Override
   public String getName() {
      return "Create/Update Branch Guid to Uuid Artifact";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      StringBuilder sb = new StringBuilder();
      try {
         chStmt.runPreparedQuery("select branch_guid, branch_id from osee_branch");
         while (chStmt.next()) {
            sb.append(chStmt.getString("branch_guid"));
            sb.append(":");
            sb.append(chStmt.getString("branch_id"));
            sb.append(";");
         }
      } finally {
         chStmt.close();
      }
      Artifact mappingArt = ArtifactQuery.getOrCreate(MAPPING_ART, CoreArtifactTypes.GeneralData, CoreBranches.COMMON);
      mappingArt.setSoleAttributeFromString(CoreAttributeTypes.GeneralStringData, sb.toString());
      mappingArt.persist("Update Branch Guid to Uuid");
   }

   @Override
   public Collection<String> getCategories() {
      return Collections.singletonList("Admin");
   }

}
