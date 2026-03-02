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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBoxWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class XWidgetEnableDisableBlamExampleBlam extends AbstractBlam {

   private XComboWidget combo;

   @Override
   public String getName() {
      return "XWidget Enable/Disable BLAM Example";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.done();
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andWidget("Check Me", WidgetId.XCheckBoxWidget);
      wb.andWidget("Enable/Disabled Combo", WidgetId.XComboWidget);
      return wb.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer swtXWidgetRenderer,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, swtXWidgetRenderer, modListener, isEditable);
      if (xWidget.getLabel().equals("Enable/Disabled Combo")) {
         combo = (XComboWidget) xWidget;
      } else if (xWidget.getLabel().equals("Check Me")) {
         final XCheckBoxWidget checkBox = (XCheckBoxWidget) xWidget;
         checkBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               combo.setEditable(!checkBox.isChecked());
            }

         });
      }
   }

   @Override
   public String getDescriptionUsage() {
      return "This blam is an example to show how an XWidget selection can enable/disable another widget.";
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
