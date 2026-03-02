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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class ExampleOutputBlam extends AbstractBlam {

   @Override
   public String getName() {
      return "Example Blam";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      for (int x = 0; x < 100; x++) {
         log("line number " + x + "\n");
      }
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andXCheckbox("body is html").andDefault(true).endWidget();
      wb.andXLabel("   ");
      wb.andXLabel("Widget to select a branch....");
      wb.andXHyperlinkBranchSelWidget().andBranchQuery().andBranchType(BranchType.WORKING);
      wb.andXLabel("- OR Widget to select a list of Branch Views passed in - ");
      wb.andXHyperlinkBranchViewSelWidget() //
         .andValues( //
            new BranchViewToken(DemoBranches.SAW_PL.getId(), "Config A", ArtifactId.valueOf(342432L)), //
            new BranchViewToken(DemoBranches.SAW_PL.getId(), "Config B", ArtifactId.valueOf(898689L)) //
         );
      wb.andXLabel("- OR Working Together - Select branch loads those views into View Selection - ");
      wb.andXHyperlinkBranchAndViewSelWidget().endWidget(); //
      return wb.getXWidgetDatas();
   }

   @Override
   public String getDescriptionUsage() {
      return "Send individual emails to everyone in the selected groups with an unsubscribe option";
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.UTILITY_EXAMPLES);
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.EXAMPLE);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EXAMPLE);
   }

}