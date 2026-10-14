/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.operation.EmptyOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;

/**
 * @author Angel Avila
 */

@Component(service = AbstractBlam.class, immediate = true)
public class AttributeCheckBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Attribute Check";
   static final String ARTIFACTS = "Drag in Artifacts to look in";
   static final String ATTRIBUTE_TYPE = "Attribute Type to check";
   static final String MULTIPLE_VALUES = "Multiple values allowed for this attribute?";
   static final String CHANGE_INCONSISTENT_VALUES = "Change artifacts with inconsistent values?";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public AttributeCheckBlam() {
      super(BLAM_NAME, CoreImage.BLAM);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andListDropWidget(ARTIFACTS);
      wb.andCheckBoxWidget(CHANGE_INCONSISTENT_VALUES);
      wb.andCheckBoxWidget(MULTIPLE_VALUES);
      wb.andAttributeTypeWidget(ATTRIBUTE_TYPE).andValueSingleSelect();
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

      AttributeTypeId attributeToCheck = variableMap.getAttributeType(ATTRIBUTE_TYPE);
      List<Artifact> artifacts = variableMap.getArtifacts(ARTIFACTS);
      boolean changeValues = variableMap.getBoolean(CHANGE_INCONSISTENT_VALUES);
      boolean multipleValues = variableMap.getBoolean(MULTIPLE_VALUES);

      return new AttributeCheckOperation(logger, artifacts, attributeToCheck, changeValues, multipleValues);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_HEALTH);
   }

   @Override
   public String getDescriptionUsage() {
      return "Check the consitency of Attribute values under a specified Folder Artifact.";
   }

}
