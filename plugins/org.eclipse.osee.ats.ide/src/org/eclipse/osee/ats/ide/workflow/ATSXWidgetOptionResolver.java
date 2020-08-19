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

package org.eclipse.osee.ats.ide.workflow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
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
         Set<String> options = new HashSet<String>();
         OrcsTokenService tokenService = ServiceUtil.getTokenService();
         AttributeTypeGeneric<?> attributeType = AttributeTypeGeneric.SENTINEL;
         try {
            String storeName = xWidgetData.getStoreName();
            Long storeId = xWidgetData.getStoreId();
            if (storeId > 0) {
               attributeType = tokenService.getAttributeType(storeId);
            } else if (Strings.isValid(storeName)) {
               attributeType = tokenService.getAttributeType(storeName);
            } else if (Strings.isValid(xWidgetData.getName())) {
               attributeType = tokenService.getAttributeType(xWidgetData.getName());
            } else {
               throw new OseeArgumentException(
                  "Attribute Type can not be determined from storeName [%s] or Name [%s] and is needed for OPTIONS_FROM_ATTRIBUTE_VALIDITY for widget [%s]",
                  xWidgetData.getStoreName(), xWidgetData.getName(), xWidgetData);
            }
            if (attributeType.isEnumerated()) {
               options = attributeType.toEnum().getEnumStrValues();
            }
         } catch (Exception ex) {
            throw new OseeArgumentException(
               "Exception determining Attribute Type from storeName [%s] or Name [%s] and widget [%s]: %s",
               xWidgetData.getStoreName(), xWidgetData.getName(), xWidgetData, ex.getLocalizedMessage());
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