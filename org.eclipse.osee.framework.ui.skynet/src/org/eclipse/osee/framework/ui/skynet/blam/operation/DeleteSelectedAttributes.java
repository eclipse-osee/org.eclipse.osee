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
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class DeleteSelectedAttributes implements BlamOperation {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, Branch branch, IProgressMonitor monitor) throws Exception {
      //    <XWidget xwidgetType="XListDropViewer" displayName="artifacts" />
      //    <XWidget xwidgetType="XAttributeTypeListViewer" displayName="AttributeTypeDescriptor" />

      List<Artifact> artifacts = variableMap.getArtifacts("artifacts");
      DynamicAttributeDescriptor attributeDescriptor = variableMap.getAttributeDescriptor("AttributeTypeDescriptor");

      monitor.beginTask("Delete Unspecified Partitions", IProgressMonitor.UNKNOWN);

      for (Artifact artifact : artifacts) {
         Collection<Attribute> attributes = artifact.getAttributeManager(attributeDescriptor).getAttributes();

         if (attributes.size() > 1) {
            for (Attribute attribute : attributes) {
               if (attribute.getStringData().equals("Unspecified")) {
                  System.out.println(artifact.getDescriptiveName());
                  attribute.delete();
               }
            }
         }
      }
      monitor.done();
   }
}