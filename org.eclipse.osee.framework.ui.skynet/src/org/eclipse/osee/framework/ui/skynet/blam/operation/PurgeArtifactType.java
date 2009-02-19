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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class PurgeArtifactType extends AbstractBlam {
   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      Collection<ArtifactType> purgeArtifactTypes =
            variableMap.getCollection(ArtifactType.class, "Artifact Type(s) to purge");
      ArtifactType newArtifactType = variableMap.getArtifactType("New Artifact Type");

      ArtifactTypeManager.purgeArtifactTypesWithConversion(purgeArtifactTypes, newArtifactType);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XArtifactTypeListViewer\" displayName=\"Artifact Type(s) to purge\" /><XWidget xwidgetType=\"XArtifactTypeListViewer\" displayName=\"New Artifact Type\" /></xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getDescriptionUsage()
    */
   @Override
   public String getDescriptionUsage() {
      return "Purge an artifact type.  Will find artifacts (if any) of this type on all branches and switch their type to the specified type.  Then purge the artifact type ";
   }
}