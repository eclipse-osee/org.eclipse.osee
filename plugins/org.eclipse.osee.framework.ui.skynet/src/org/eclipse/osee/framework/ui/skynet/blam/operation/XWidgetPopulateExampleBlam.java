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
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;

/**
 * @author Ryan D. Brooks
 */
@Component(service = AbstractBlam.class, immediate = true)
public class XWidgetPopulateExampleBlam extends AbstractBlam {

   private static final String BLAM_NAME = "XWidget Populate Example";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XWidgetPopulateExampleBlam() {
      super(BLAM_NAME, CoreImage.EXAMPLE);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andStringsSelWidget("Select an Option",
         Arrays.asList("One", "Two", "Three")).andValueSingleSelect().andToolTip("Tooltip example");
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      //      AWorkbench.popup("Execute", "Blam is an example only.  Nothing done.");
      monitor.done();
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
      return "This blam is an example to show how an XWidget can populate it's values ( or perform other"
         //
         + " operations on the XWidget ) during it's creation.  This is used when the options or default" +
         //
         " selected value may come from another dynamic source such as a database query.";
   }

}
