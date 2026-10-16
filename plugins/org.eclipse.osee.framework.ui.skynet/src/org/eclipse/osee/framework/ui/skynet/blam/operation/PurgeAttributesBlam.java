/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.EmptyOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationBuilder;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeAttributes;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.program.Program;
import org.osgi.service.component.annotations.Component;

/**
 * @author Jeff C. Phillips
 */
@Component(service = AbstractBlam.class, immediate = true)
public class PurgeAttributesBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Purge Invalid Attribute Types";
   private static final String ATTRIBUTE_TYPES_TO_PURGE = "Attribute Type(s) to purge";
   private static final String ARTIFACTS = "Artifacts";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public PurgeAttributesBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andListDropWidget(ARTIFACTS);
      wb.andWidget(ATTRIBUTE_TYPES_TO_PURGE, WidgetId.XXAttributeTypeWidget).andValueMultiSelect();
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      // Remove when demo operation test is implemented in Blam
      if (DemoUtilCore.isDemoDbBlamTest()) {
         return EmptyOperation.instance;
      }

      List<AttributeTypeToken> purgeAttributeTypes = variableMap.getAttributeTypes(ATTRIBUTE_TYPES_TO_PURGE);

      List<Artifact> artifacts = variableMap.getArtifacts(ARTIFACTS);

      List<Attribute<?>> attributesToPurge = new ArrayList<>();

      for (Artifact artifact : artifacts) {
         for (AttributeTypeToken attributeType : purgeAttributeTypes) {
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

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   private class ReportPurgedAttributes extends AbstractOperation {
      private final List<Attribute<?>> attributesToPurge;

      public ReportPurgedAttributes(List<Attribute<?>> attributesToPurge) {
         super(BLAM_NAME, Activator.PLUGIN_ID);
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

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(CoreUserGroups.OseeAdmin);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_ADMIN);
   }

   @Override
   public String getDescriptionUsage() {
      return "Purge invalid specified attribute types from selected artifacts.";
   }

}
