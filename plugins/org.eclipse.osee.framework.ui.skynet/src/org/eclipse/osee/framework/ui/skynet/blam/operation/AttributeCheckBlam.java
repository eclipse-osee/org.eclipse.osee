/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Angel Avila
 */

public class AttributeCheckBlam extends AbstractBlam {
   private static final String ARTIFACTS = "Drag in Artifacts to look in";
   private static final String ATTRIBUTE = "Attribute Type to check";
   private static final String MULTIPLE_VALUES = "Multiple values allowed for this attribute?";
   private static final String CHANGE_INCONSISTENT_VALUES = "Change artifacts with inconsistent values?";

   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      AttributeTypeId attributeToCheck = variableMap.getAttributeType(ATTRIBUTE);
      List<Artifact> artifacts = variableMap.getArtifacts(ARTIFACTS);
      boolean changeValues = variableMap.getBoolean(CHANGE_INCONSISTENT_VALUES);
      boolean multipleValues = variableMap.getBoolean(MULTIPLE_VALUES);

      return new AttributeCheckOperation(logger, artifacts, attributeToCheck, changeValues, multipleValues);
   }

   @Override
   public String getDescriptionUsage() {
      return "Check the consitency of Attribute values under a specified Folder Artifact.";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder sb = new StringBuilder();
      sb.append("<xWidgets>");
      sb.append("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"");
      sb.append(ARTIFACTS);
      sb.append("\" />");

      sb.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      sb.append(CHANGE_INCONSISTENT_VALUES);
      sb.append("\" labelAfter=\"true\" horizontalLabel=\"true\" />");

      sb.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      sb.append(MULTIPLE_VALUES);
      sb.append("\" labelAfter=\"true\" horizontalLabel=\"true\" />");

      sb.append("<XWidget xwidgetType=\"XAttributeTypeMultiChoiceSelect\" displayName=\"");
      sb.append(ATTRIBUTE);
      sb.append("\" multiSelect=\"false\" />");

      sb.append("</xWidgets>");
      return sb.toString();
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Reports");
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Collections.singleton(CoreUserGroups.Everyone);
   }

}