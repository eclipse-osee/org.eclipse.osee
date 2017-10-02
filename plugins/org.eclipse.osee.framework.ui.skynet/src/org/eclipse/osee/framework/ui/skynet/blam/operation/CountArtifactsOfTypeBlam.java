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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * Counts artifact of type or inherited type
 * 
 * @author Donald G. Dunne
 */
public class CountArtifactsOfTypeBlam extends AbstractBlam {

   @Override
   public String getName() {
      return "Count Artifacts of Type";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      if (variableMap.getBoolean("All Types")) {
         XResultData rd = new XResultData(false);
         rd.log(String.format("Aritfact Count for Type for Branch [%s]\n", variableMap.getBranch("Branch")));
         rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
         rd.addRaw(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Type", "Count")));
         for (IArtifactType artType : ArtifactTypeManager.getAllTypes()) {
            int count = ArtifactQuery.getArtifactCountFromTypeWithInheritence(artType, variableMap.getBranch("Branch"),
               DeletionFlag.EXCLUDE_DELETED);
            rd.addRaw(AHTML.addRowMultiColumnTable(artType.toString(), String.valueOf(count)));
         }
         rd.addRaw(AHTML.endMultiColumnTable());
         XResultDataUI.report(rd, "Artifact Type Count");
      } else {
         IArtifactType artType = variableMap.getArtifactType("Artifact Type");
         int count = ArtifactQuery.getArtifactCountFromTypeWithInheritence(artType, variableMap.getBranch("Branch"),
            DeletionFlag.EXCLUDE_DELETED);
         String str = String.format("\nAritfact Count for Type [%s] = %d\n\n", artType, count);
         logf(str);
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets>" + //
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /> " + //
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"All Types\" /> " + //
         "<XWidget xwidgetType=\"XArtifactTypeComboViewer\" displayName=\"Artifact Type\" />" + //
         "</xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Reports");
   }

   @Override
   public String getDescriptionUsage() {
      return "Count artifacts on specified branch by inherited type.";
   }
}