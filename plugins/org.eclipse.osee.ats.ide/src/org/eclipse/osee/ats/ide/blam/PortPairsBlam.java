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

package org.eclipse.osee.ats.ide.blam;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.operation.EmptyOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
@Component(service = AbstractBlam.class, immediate = true)
public class PortPairsBlam extends AbstractAtsBlam {

   private final static String BLAM_NAME = "Porting ID Pairs";
   private static final String USE_ATSID = "Use Ats IDs for Porting Workflows";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public PortPairsBlam() {
      super(BLAM_NAME, CoreImage.RIGHT_ARROW_1);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andTextWidget(BLAM_NAME).fillVert();
      wb.andCheckBoxWidget(USE_ATSID);
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

      String pairs = variableMap.getString(BLAM_NAME);
      boolean checked = variableMap.getBoolean(USE_ATSID);
      return new PortPairsOperation(logger, pairs, checked);
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(AtsUserGroups.AtsAdmin);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_ADMIN);
   }

   @Override
   public String getDescriptionUsage() {
      return "Port a given list of workflows represented by ATS IDs, formated like: <from>,<to>. Each pair should be separated by a return. The BLAM can be run multiple times, as the conflicts are resolved on the port branch merges.";
   }

}
