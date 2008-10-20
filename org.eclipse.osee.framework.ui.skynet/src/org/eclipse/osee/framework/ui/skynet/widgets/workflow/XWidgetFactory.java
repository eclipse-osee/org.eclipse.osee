/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.SkynetSpellModifyDictionary;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactTypeListViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XAttributeTypeListViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBoxDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboBooleanDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XDateDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XFileSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloat;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloatDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelMemberSelDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XInteger;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabel;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XList;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersList;
import org.eclipse.osee.framework.ui.skynet.widgets.XMultiXWidgetTextDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XRelationTypeListViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextResourceDropDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.framework.Bundle;

/**
 * @author Jeff C. Phillips
 */
public class XWidgetFactory {

   private static final XWidgetFactory reference = new XWidgetFactory();

   private XWidgetFactory() {

   }

   public static XWidgetFactory getInstance() {
      return reference;
   }

   public XWidget createXWidget(DynamicXWidgetLayoutData xWidgetLayoutData) {
      return createXWidget(xWidgetLayoutData.getXWidgetName(), xWidgetLayoutData.getName(), xWidgetLayoutData);
   }

   public XWidget createXWidget(String xWidgetName, String name, DynamicXWidgetLayoutData xWidgetLayoutData) {
      XWidget xWidget = null;

      // Look for widget provider to create widget
      for (IXWidgetProvider widgetProvider : getXWidgetProviders()) {
         xWidget = widgetProvider.createXWidget(xWidgetName, name, xWidgetLayoutData);
         if (xWidget != null) {
            return xWidget;
         }
      }

      // Otherwise, use default widget creation
      if (xWidgetName.equals("XText")) {
         xWidget = new XText(name);
         if (xWidgetLayoutData.getDefaultValue() != null && !xWidgetLayoutData.getDefaultValue().equals("")) {
            ((XText) xWidget).set(xWidgetLayoutData.getDefaultValue());
         }
      } else if (xWidgetName.equals("XMultiXWidgetTextDam"))
         xWidget = new XMultiXWidgetTextDam(name);
      else if (xWidgetName.equals("XInteger"))
         xWidget = new XInteger(name);
      else if (xWidgetName.equals("XTextDam"))
         xWidget = new XTextDam(name);
      else if (xWidgetName.equals("XLabelDam"))
         xWidget = new XLabelDam(name);
      else if (xWidgetName.equals("XMembersList")) {
         try {
            xWidget = new XMembersList(name);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      } else if (xWidgetName.equals("XMembersCombo"))
         xWidget = new XMembersCombo(name);
      else if (xWidgetName.equals("XDate"))
         xWidget = new XDate(name);
      else if (xWidgetName.equals("XFileSelectionDialog"))
         xWidget = new XFileSelectionDialog(name);
      else if (xWidgetName.equals("XDateDam"))
         xWidget = new XDateDam(name);
      else if (xWidgetName.equals("XTextResourceDropDam"))
         xWidget = new XTextResourceDropDam(name);
      else if (xWidgetName.equals("XFloat"))
         xWidget = new XFloat(name);
      else if (xWidgetName.equals("XFloatDam"))
         xWidget = new XFloatDam(name);
      else if (xWidgetName.equals("XIntegerDam"))
         xWidget = new org.eclipse.osee.framework.ui.skynet.widgets.XIntegerDam(name);
      else if (xWidgetName.equals("XLabel"))
         xWidget = new XLabel(name);
      else if (xWidgetName.equals("XCheckBox")) {
         XCheckBox checkBox = new XCheckBox(name);
         checkBox.setLabelAfter(xWidgetLayoutData.getXOptionHandler().contains(XOption.LABEL_AFTER));
         if (xWidgetLayoutData.getDefaultValue() != null && !xWidgetLayoutData.getDefaultValue().equals("")) checkBox.set(xWidgetLayoutData.getDefaultValue().equals(
               "true"));
         xWidget = checkBox;
      } else if (xWidgetName.equals("XCheckBoxDam")) {
         XCheckBoxDam checkBox = new XCheckBoxDam(name);
         checkBox.setLabelAfter(xWidgetLayoutData.getXOptionHandler().contains(XOption.LABEL_AFTER));
         xWidget = checkBox;
      } else if (xWidgetName.startsWith("XComboDam")) {
         if (xWidgetLayoutData.getDynamicXWidgetLayout() != null) {
            String values[] =
                  xWidgetLayoutData.getDynamicXWidgetLayout().getOptionResolver().getWidgetOptions(xWidgetLayoutData);
            if (values.length > 0) {
               xWidget = new XComboDam(name);
               XComboDam combo = new XComboDam(name);
               combo.setDataStrings(values);
               xWidget = combo;
            } else
               throw new IllegalArgumentException(
                     "Invalid XComboDam.  " + "Must be \"XComboDam(option1,option2,option3)\"");
         }
      } else if (xWidgetName.startsWith("XComboBooleanDam")) {
         xWidget = new XComboBooleanDam(name);
         XComboBooleanDam combo = new XComboBooleanDam(name);
         combo.setDataStrings(BooleanAttribute.booleanChoices);
         xWidget = combo;
         if (xWidgetLayoutData.getDefaultValue() != null && !xWidgetLayoutData.getDefaultValue().equals("")) {
            String value = xWidgetLayoutData.getDefaultValue();
            if (value == null)
               combo.set("");
            else if (value.equals("true") || value.equals("yes"))
               combo.set("yes");
            else if (value.equals("false") || value.equals("no"))
               combo.set("no");
            else
               combo.set("");
         }
      } else if (xWidgetName.startsWith("XCombo")) {
         String values[] =
               xWidgetLayoutData.getDynamicXWidgetLayout().getOptionResolver().getWidgetOptions(xWidgetLayoutData);
         if (values.length > 0) {
            XCombo combo = new XCombo(name);
            combo.setDataStrings(values);
            xWidget = combo;
         } else
            throw new IllegalArgumentException("Invalid XCombo.  " + "Must be \"XCombo(option1,option2,option3)\"");
      } else if (xWidgetName.startsWith("XListDam")) {
         if (xWidgetLayoutData.getDynamicXWidgetLayout() != null) {
            String values[] =
                  xWidgetLayoutData.getDynamicXWidgetLayout().getOptionResolver().getWidgetOptions(xWidgetLayoutData);
            if (values.length > 0) {
               XListDam list = new XListDam(name);
               list.add(values);
               xWidget = list;
            } else
               throw new IllegalArgumentException("Invalid XList.  " + "Must be \"XList(option1,option2,option3)\"");
         }
      } else if (xWidgetName.equals("XHyperlabelMemberSelDam")) {
         xWidget = new XHyperlabelMemberSelDam(name);
      } else if (xWidgetName.startsWith("XListDropViewer")) {
         xWidget = new XListDropViewer(name);
      } else if (xWidgetName.equals("XArtifactTypeListViewer")) {
         xWidget =
               new XArtifactTypeListViewer(xWidgetLayoutData.getKeyedBranchName(), xWidgetLayoutData.getDefaultValue());
      } else if (xWidgetName.equals("XAttributeTypeListViewer")) {
         xWidget =
               new XAttributeTypeListViewer(xWidgetLayoutData.getKeyedBranchName(), xWidgetLayoutData.getDefaultValue());
      } else if (xWidgetName.equals("XRelationTypeListViewer")) {
         xWidget = new XRelationTypeListViewer();
      } else if (xWidgetName.startsWith("XList")) {
         String values[] =
               xWidgetLayoutData.getDynamicXWidgetLayout().getOptionResolver().getWidgetOptions(xWidgetLayoutData);
         if (values.length > 0) {
            XList list = new XList(name);
            list.add(values);
            xWidget = list;
            if (xWidgetLayoutData.getDefaultValue() != null && !xWidgetLayoutData.getDefaultValue().equals("")) list.setSelected(Arrays.asList(xWidgetLayoutData.getDefaultValue().split(
                  ",")));
         } else
            throw new IllegalArgumentException("Invalid XList.  " + "Must be \"XList(option1,option2,option3)\"");

      } else {
         throw new IllegalArgumentException("Unhandled XWidget \"" + xWidgetName + "\"");
      }
      if (xWidget instanceof XText) {
         ((XText) xWidget).addXTextSpellModifyDictionary(new SkynetSpellModifyDictionary());
      }
      return xWidget;
   }
   private static Set<IXWidgetProvider> widgetProviders;

   private static Set<IXWidgetProvider> getXWidgetProviders() {
      widgetProviders = new HashSet<IXWidgetProvider>();
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.ui.skynet.XWidgetProvider");
      if (point == null) {
         OSEELog.logSevere(SkynetGuiPlugin.class, "Can't access XWidgetProvider extension point", true);
         return null;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("XWidgetProvider")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     widgetProviders.add((IXWidgetProvider) obj);
                  } catch (Exception ex) {
                     OSEELog.logException(SkynetGuiPlugin.class, "Error loading XWidgetProvider extension", ex, true);
                  }
               }

            }
         }
      }
      return widgetProviders;
   }
}