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
import java.util.Collections;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class ReportAttributeTypesUsageBlam extends AbstractBlam {

   @Override
   public String getName() {
      return "Report Attribute Types Usage";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Report Usage of AttributeTypes on Branch", IProgressMonitor.UNKNOWN);

      XResultData xResultData = new XResultData();
      BranchId branch = variableMap.getBranch("Branch");
      xResultData.log(getName() + " " + BranchManager.getBranchName(branch));
      xResultData.addRaw(AHTML.beginMultiColumnTable(100, 1));
      xResultData.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"AttributeType", "Occurances"}));
      for (AttributeType attributeType : AttributeTypeManager.getAllTypes()) {
         Collection<Artifact> arts = ArtifactQuery.getArtifactListFromAttributeType(attributeType, branch);
         xResultData.addRaw(AHTML.addRowMultiColumnTable(attributeType.getName(), String.valueOf(arts.size())));
      }
      xResultData.addRaw(AHTML.endMultiColumnTable());
      XResultDataUI.report(xResultData, getName() + " " + BranchManager.getBranchName(branch));
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Performs ArtifactQuery search on all attribyte types for a selected branch and reports usage by \"current\" artifacts.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Collections.singleton(CoreUserGroups.Everyone);
   }

}