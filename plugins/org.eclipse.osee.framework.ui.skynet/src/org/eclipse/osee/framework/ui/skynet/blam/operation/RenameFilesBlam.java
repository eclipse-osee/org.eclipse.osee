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
import org.eclipse.osee.framework.core.operation.EmptyOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.ops.RenameFilesOperation;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;

/**
 * @author Ryan D. Brooks
 */
@Component(service = AbstractBlam.class, immediate = true)
public class RenameFilesBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Rename Files";
   static final String PARENT_FOLDER = "Parent Folder";
   static final String FULL_PATH_PATTERN = "Full Path Pattern";
   static final String REPLACEMENT = "Replacement";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public RenameFilesBlam() {
      super(BLAM_NAME, CoreImage.BLAM);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andTextWidget(PARENT_FOLDER);
      wb.andTextWidget(FULL_PATH_PATTERN);
      wb.andTextWidget(REPLACEMENT);
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      // Remove when demo operation test is implemented in Blam
      if (DemoUtilCore.isDemoDbBlamTest()) {
         return EmptyOperation.instance;
      }

      String parentFolder = variableMap.getString(PARENT_FOLDER);
      String pathPattern = variableMap.getString(FULL_PATH_PATTERN);
      String replacement = variableMap.getString(REPLACEMENT);
      return new RenameFilesOperation(logger, parentFolder, pathPattern, replacement);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE);
   }

   @Override
   public String getDescriptionUsage() {
      return "Batch renames files on disk matching a regex pattern. Specify the parent folder, a full path regex pattern to match, and the replacement string. Uses Java regex for pattern matching.";
   }

}
