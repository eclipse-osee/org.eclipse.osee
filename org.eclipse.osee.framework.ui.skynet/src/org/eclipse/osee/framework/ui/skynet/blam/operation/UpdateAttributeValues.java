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

import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class UpdateAttributeValues implements BlamOperation {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      List<Artifact> artifacts = variableMap.getArtifacts("Artifacts");
      DynamicAttributeDescriptor attributeDescriptor = variableMap.getAttributeDescriptor("Attribute Type");
      String newValue = variableMap.getString("Attribute Value");

      monitor.beginTask("Update Attribute Values", IProgressMonitor.UNKNOWN);

      for (Artifact artifact : artifacts) {
         Collection<Attribute> attributes = artifact.getAttributeManager(attributeDescriptor).getAttributes();
         for (Attribute attribute : attributes) {
            attribute.setStringData(newValue);
         }
         artifact.persistAttributes();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Artifacts\" /><XWidget xwidgetType=\"XAttributeTypeListViewer\" displayName=\"Attribute Type\" /><XWidget xwidgetType=\"XText\" displayName=\"Attribute Value\" /></xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "Select parameters below and click the play button at the top right.";
   }
}