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
package org.eclipse.osee.ats.ide.workflow;

import java.util.Arrays;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.util.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;

/**
 * @author Donald G. Dunne
 */
public final class ATSXWidgetOptionResolver extends DefaultXWidgetOptionResolver {

   private static ATSXWidgetOptionResolver instance = new ATSXWidgetOptionResolver();
   public final static String OPTIONS_FROM_ATTRIBUTE_VALIDITY = "OPTIONS_FROM_ATTRIBUTE_VALIDITY";

   private ATSXWidgetOptionResolver() {
      // private constructor
   }

   @Override
   public String[] getWidgetOptions(XWidgetRendererItem xWidgetData) {

      if (xWidgetData.getXWidgetName().contains(
         OPTIONS_FROM_ATTRIBUTE_VALIDITY) || xWidgetData.getXWidgetName().contains("ACTIVE_USER_COMMUNITIES")) {
         Set<String> options = null;
         try {
            String storeName = xWidgetData.getStoreName();
            if (Strings.isValid(storeName)) {
               options = AttributeTypeManager.getEnumerationValues(storeName);
            } else {
               String displayName = xWidgetData.getName();
               if (Strings.isValid(displayName)) {
                  options = AttributeTypeManager.getEnumerationValues(displayName);
               }
            }
         } catch (Exception ex) {
            throw new OseeArgumentException(
               "Exception determining Attribute Type from storeName [%s] or Name [%s] and widget [%s]: %s",
               xWidgetData.getStoreName(), xWidgetData.getName(), xWidgetData, ex.getLocalizedMessage());
         }
         if (options == null) {
            throw new OseeArgumentException(
               "Attribute Type can not be determined from storeName [%s] or Name [%s] and is needed for OPTIONS_FROM_ATTRIBUTE_VALIDITY for widget [%s]",
               xWidgetData.getStoreName(), xWidgetData.getName(), xWidgetData);
         }
         String optStrs[] = options.toArray(new String[options.size()]);
         Arrays.sort(optStrs);
         return optStrs;
      }
      return super.getWidgetOptions(xWidgetData);
   }

   /**
    * @return the instance
    */
   public static ATSXWidgetOptionResolver getInstance() {
      return instance;
   }

}
