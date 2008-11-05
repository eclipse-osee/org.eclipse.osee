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
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ReportAttributeTypesUsageBlam extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Report Usage of AttributeTypes on Branch", IProgressMonitor.UNKNOWN);

      XResultData xResultData = new XResultData();
      Branch branch = variableMap.getBranch("Branch");
      xResultData.log(getName() + " " + branch.getBranchName());
      xResultData.addRaw(AHTML.beginMultiColumnTable(100, 1));
      xResultData.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"AttributeType", "Occurances"}));
      for (AttributeType attributeType : AttributeTypeManager.getAllTypes()) {
         Collection<Artifact> arts = ArtifactQuery.getArtifactsFromAttributeType(attributeType.getName(), branch);
         xResultData.addRaw(AHTML.addRowMultiColumnTable(attributeType.getName(), String.valueOf(arts.size())));
      }
      xResultData.addRaw(AHTML.endMultiColumnTable());
      xResultData.report(getName() + " " + branch.getBranchName());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getDescriptionUsage()
    */
   @Override
   public String getDescriptionUsage() {
      return "Performs ArtifactQuery search on all attribyte types for a selected branch and reports usage by \"current\" artifacts.";
   }

}