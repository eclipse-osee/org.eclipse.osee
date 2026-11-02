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
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;

/**
 * Counts artifact of type or inherited type
 *
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class CountArtifactsOfTypeBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Count Artifacts of Type";
   static final String ALL_TYPES = "All Types";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public CountArtifactsOfTypeBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andBranchSelWidget();
      wb.andCheckBoxWidget(ALL_TYPES);
      wb.andLabelWidget("OR");
      wb.andArtifactTypeWidget();
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      // Remove when demo operation test is implemented in Blam
      if (DemoUtilCore.isDemoDbBlamTest()) {
         return;
      }

      boolean allTypes = variableMap.getBoolean(ALL_TYPES);
      if (allTypes) {
         XResultData rd = new XResultData(false);
         rd.log(
            String.format("Aritfact Count for Type for Branch [%s]\n", variableMap.getBranch(XWidgetLabels.BRANCH)));
         rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
         rd.addRaw(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Type", "Count")));
         for (ArtifactTypeToken artType : ServiceUtil.getTokenService().getArtifactTypes().stream().filter(
            a -> a.isValid()).collect(Collectors.toList())) {
            int count = ArtifactQuery.getArtifactCountFromTypeWithInheritence(artType,
               variableMap.getBranch(XWidgetLabels.BRANCH), DeletionFlag.EXCLUDE_DELETED);
            rd.addRaw(AHTML.addRowMultiColumnTable(artType.toString(), String.valueOf(count)));
         }
         rd.addRaw(AHTML.endMultiColumnTable());
         XResultDataUI.report(rd, "Artifact Type Count");
      } else {
         ArtifactTypeToken artType = variableMap.getArtifactType(XWidgetLabels.ARTIFACT_TYPE);
         int count = ArtifactQuery.getArtifactCountFromTypeWithInheritence(artType,
            variableMap.getBranch(XWidgetLabels.BRANCH), DeletionFlag.EXCLUDE_DELETED);
         String str = String.format("\nAritfact Count for Type [%s] = %d\n\n", artType, count);
         logf(str);
      }
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
      return "Count artifacts on specified branch by inherited type.";
   }

}
