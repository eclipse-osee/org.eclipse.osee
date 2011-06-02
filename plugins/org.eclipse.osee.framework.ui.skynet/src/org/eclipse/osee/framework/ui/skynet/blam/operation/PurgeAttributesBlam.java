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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeAttribute;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;

/**
 * @author Jeff C. Phillips
 */
public class PurgeAttributesBlam extends AbstractBlam {
   @Override
   public String getName() {
      return "Purge Attributes";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Collection<AttributeType> purgeAttributeTypes =
         variableMap.getCollection(AttributeType.class, "Attribute Type(s) to purge");

      List<Artifact> artifacts = variableMap.getArtifacts("artifacts");

      StringBuilder strB = new StringBuilder();
      List<Attribute<?>> attributesToPurge = new ArrayList<Attribute<?>>();

      for (Artifact artifact : artifacts) {
         for (IAttributeType attributeType : purgeAttributeTypes) {
            //if attribute type is invalid purge them
            if (!artifact.isAttributeTypeValid(attributeType)) {
               for (Attribute<?> attribute : artifact.getAllAttributesIncludingHardDeleted(attributeType)) {
                  strB.append(attribute.getAttributeType());
                  strB.append(";");
                  strB.append(artifact.getArtId());
                  strB.append(";");
                  strB.append(attribute.getDisplayableString());
                  strB.append("\n");

                  attribute.purge();
                  attributesToPurge.add(attribute);
               }
            }
         }
         artifact.persist();
         new PurgeAttribute(attributesToPurge).execute();
      }

      IFile iFile = OseeData.getIFile("Purge Attributes" + Lib.getDateTimeString() + ".txt");
      AIFile.writeToFile(iFile, strB.toString());
      Program.launch(iFile.getLocation().toOSString());
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifacts\" /> " + //
      "<XWidget xwidgetType=\"XAttributeTypeMultiChoiceSelect\" displayName=\"Attribute Type(s) to purge\" multiSelect=\"true\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Purge specified attribute from selected artifacts.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}