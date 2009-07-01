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
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class PurgeAttributeType extends AbstractBlam {
   @Override
   public String getName() {
      return "Purge Attribute Type";
   }

   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Collection<AttributeType> purgeAttributeTypes =
            variableMap.getCollection(AttributeType.class, "Attribute Type(s) to purge");

      for (AttributeType attributeType : purgeAttributeTypes) {
         AttributeTypeManager.purgeAttributeType(attributeType);
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XAttributeTypeListViewer\" displayName=\"Attribute Type(s) to purge\" multiSelect=\"true\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Purge an attribute type.";
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}