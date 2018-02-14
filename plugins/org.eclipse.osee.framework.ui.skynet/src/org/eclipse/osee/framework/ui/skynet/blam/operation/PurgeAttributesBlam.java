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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationBuilder;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeAttributes;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.program.Program;

/**
 * @author Jeff C. Phillips
 */
public class PurgeAttributesBlam extends AbstractBlam {
   @Override
   public String getName() {
      return "Purge Invalid Attribute Types";
   }

   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      Collection<AttributeType> purgeAttributeTypes =
         variableMap.getCollection(AttributeType.class, "Attribute Type(s) to purge");

      List<Artifact> artifacts = variableMap.getArtifacts("artifacts");

      List<Attribute<?>> attributesToPurge = new ArrayList<>();

      for (Artifact artifact : artifacts) {
         for (AttributeTypeId attributeType : purgeAttributeTypes) {
            //if attribute type is invalid purge them
            if (!artifact.isAttributeTypeValid(attributeType)) {
               for (Attribute<?> attribute : artifact.getAllAttributesIncludingHardDeleted(attributeType)) {
                  attributesToPurge.add(attribute);
               }
            }
         }
      }

      OperationBuilder builder = Operations.createBuilder(getName());
      builder.addOp(new PurgeAttributes(attributesToPurge));
      builder.addOp(new ReportPurgedAttributes(attributesToPurge));
      return builder.build();
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifacts\" /> " + //
         "<XWidget xwidgetType=\"XAttributeTypeMultiChoiceSelect\" displayName=\"Attribute Type(s) to purge\" multiSelect=\"true\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Purge invalid specified attribute types from selected artifacts.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

   private class ReportPurgedAttributes extends AbstractOperation {
      private final List<Attribute<?>> attributesToPurge;

      public ReportPurgedAttributes(List<Attribute<?>> attributesToPurge) {
         super("Purge Attributes", Activator.PLUGIN_ID);
         this.attributesToPurge = attributesToPurge;
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         StringBuilder strB = new StringBuilder();
         for (Attribute<?> attribute : attributesToPurge) {
            strB.append(attribute.getAttributeType());
            strB.append(";");
            strB.append(attribute.getArtifact().getIdString());
            strB.append(";");
            strB.append(attribute.getDisplayableString());
            strB.append("\n");
         }
         final IFile iFile = OseeData.getIFile("Purge Attributes" + Lib.getDateTimeString() + ".txt");
         AIFile.writeToFile(iFile, strB.toString());
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               Program.launch(iFile.getLocation().toOSString());
            }
         });
      }
   }
}