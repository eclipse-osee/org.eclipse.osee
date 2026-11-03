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
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;

/**
 * @author Ryan D. Brooks
 */
@Component(service = AbstractBlam.class, immediate = true)
public class DeleteUnneededUnspecifiedAttributesBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Delete Unneeded Unspecified Attributes";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public DeleteUnneededUnspecifiedAttributesBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andAttributeTypeWidget().andValueMultiSelect();
      wb.andBranchSelWidget();
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

      BranchId branch = variableMap.getBranch(XWidgetLabels.BRANCH);
      AttributeTypeToken attributeType = variableMap.getAttributeType(XWidgetLabels.ATTRIBUTE_TYPE);
      List<Artifact> artifacts =
         ArtifactQuery.getArtifactListFromAttribute(attributeType, AttributeId.UNSPECIFIED, branch);
      SkynetTransaction transaction =
         TransactionManager.createTransaction(branch, "BLAM: Delete unneeded unspecified attributes");

      for (Artifact artifact : artifacts) {
         Collection<Attribute<String>> attributes = artifact.getAttributes(attributeType);
         for (Attribute<String> attribute1 : attributes) {
            if (!attribute1.getValue().equals(AttributeId.UNSPECIFIED)) {
               for (Attribute<String> attribute : attributes) {
                  if (attribute.getValue().equals(AttributeId.UNSPECIFIED)) {
                     attribute.delete();
                  }
               }
               artifact.persist(transaction);
               break;
            }
         }
      }
      transaction.execute();
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
      return "Deletes UNSPECIFIED attribute values that are no longer needed. Scans the selected branch for artifacts with the given attribute type set to UNSPECIFIED and removes those attribute instances.";
   }

}
