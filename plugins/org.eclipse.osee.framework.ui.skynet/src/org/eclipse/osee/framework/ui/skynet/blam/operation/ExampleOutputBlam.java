/*********************************************************************
 * Copyright (c) 2014 Boeing
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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class ExampleOutputBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Example";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public ExampleOutputBlam() {
      super(BLAM_NAME, CoreImage.EXAMPLE);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andCheckBoxWidget("body is html").andDefault(true);
      wb.andLabelWidget("   ");
      wb.andLabelWidget("Widget to select a branch....");
      wb.andBranchSelWidget().andBranchQuery().andBranchType(BranchType.WORKING);
      wb.andLabelWidget("- OR Widget to select a list of Branch Views passed in - ");
      wb.andBranchViewSelWidget() //
         .andValues( //
            new BranchViewToken(DemoBranches.SAW_PL.getId(), "Config A", ArtifactId.valueOf(342432L)), //
            new BranchViewToken(DemoBranches.SAW_PL.getId(), "Config B", ArtifactId.valueOf(898689L)) //
         );
      wb.andLabelWidget("- OR Working Together - Select branch loads those views into View Selection - ");
      wb.andBranchAndViewSelWidget(); //
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      for (int x = 0; x < 100; x++) {
         log("line number " + x + "\n");
      }
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.UTILITY_EXAMPLES);
   }

   @Override
   public String getDescriptionUsage() {
      return "Send individual emails to everyone in the selected groups with an unsubscribe option";
   }

}
