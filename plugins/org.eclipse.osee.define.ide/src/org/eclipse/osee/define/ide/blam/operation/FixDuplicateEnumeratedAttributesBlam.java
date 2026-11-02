/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.define.ide.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.define.ide.blam.operation.FixAttributeOperation.Display;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.EmptyOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;

/**
 * @author Angel Avila
 */
@Component(service = AbstractBlam.class, immediate = true)
public class FixDuplicateEnumeratedAttributesBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Fix Duplicate Enumerated Attributes";
   static final String COMMIT_CHANGES_LABEL = "Commit Changes to Branch?";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public FixDuplicateEnumeratedAttributesBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andWidget(XWidgetLabels.BRANCH, WidgetId.XXBranchSelWidget).andValueSingleSelect();
      wb.andCheckBoxWidget(COMMIT_CHANGES_LABEL);
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {

      // Remove when demo operation test is implemented in Blam
      if (DemoUtilCore.isDemoDbBlamTest()) {
         return new EmptyOperation();
      }

      BranchId branch = variableMap.getBranch(XWidgetLabels.BRANCH);
      boolean commitChangesBool = variableMap.getBoolean(COMMIT_CHANGES_LABEL);

      Display display = new FixAttributesUIReport();
      return new FixAttributeOperation(logger, display, BranchManager.getBranchToken(branch), commitChangesBool);
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
      return "Remove duplicate enumerated attributes";
   }

}
