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
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.AIFile;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class PurgeDeletedAttributes extends AbstractBlam {
   @Override
   public String getName() {
      return "Purge Deleted Attributes";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Branch branch = variableMap.getBranch("Branch");
      Collection<AttributeType> purgeAttributeTypes =
            variableMap.getCollection(AttributeType.class, "Attribute Type(s) to purge");

      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromBranch(branch, ArtifactLoad.ALL_CURRENT, true);

      StringBuilder strB = new StringBuilder();

      for (Artifact artifact : artifacts) {
         for (AttributeType attributeType : purgeAttributeTypes) {
            for (Attribute<?> attribute : artifact.getAllAttributesIncludingHardDeleted(attributeType.getName())) {

               strB.append(attribute.getAttributeType());
               strB.append(";");
               strB.append(artifact.getArtId());
               strB.append(";");
               strB.append(attribute.getDisplayableString());
               strB.append("\n");
               attribute.purge();

            }
         }
      }

      IFile iFile = OseeData.getIFile("PurgeDeletedAttributes" + Lib.getDateTimeString() + ".txt");
      AIFile.writeToFile(iFile, strB.toString());
      Program.launch(iFile.getLocation().toOSString());
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\"  defaultValue=\"" + CoreBranches.COMMON.getGuid() + "\" displayName=\"Branch\" /><XWidget xwidgetType=\"XAttributeTypeListViewer\" displayName=\"Attribute Type(s) to purge\" multiSelect=\"true\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Purge all deleted attributes of specified type(s).";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}