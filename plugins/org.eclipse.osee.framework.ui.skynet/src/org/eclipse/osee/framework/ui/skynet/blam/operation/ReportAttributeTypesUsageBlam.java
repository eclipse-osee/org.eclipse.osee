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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class ReportAttributeTypesUsageBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Report Attribute Types Usage";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public ReportAttributeTypesUsageBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andWidget(XWidgetLabels.BRANCH, WidgetId.XXBranchSelWidget);
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Report Usage of AttributeType on Branch", IProgressMonitor.UNKNOWN);

      XResultData xResultData = new XResultData();
      BranchId branch = variableMap.getBranch(XWidgetLabels.BRANCH);
      xResultData.log(getName() + " " + BranchManager.getBranchName(branch));
      xResultData.addRaw(AHTML.beginMultiColumnTable(100, 1));
      xResultData.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"AttributeType", "Occurances"}));
      for (AttributeTypeToken attributeType : AttributeTypeManager.getAllTypes()) {
         Collection<Artifact> arts = ArtifactQuery.getArtifactListFromAttributeType(attributeType, branch);
         xResultData.addRaw(AHTML.addRowMultiColumnTable(attributeType.getName(), String.valueOf(arts.size())));
      }
      xResultData.addRaw(AHTML.endMultiColumnTable());
      XResultDataUI.report(xResultData, getName() + " " + BranchManager.getBranchName(branch));
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

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
      return "Performs ArtifactQuery search on all attribyte types for a selected branch and reports usage by \"current\" artifacts.";
   }

}
