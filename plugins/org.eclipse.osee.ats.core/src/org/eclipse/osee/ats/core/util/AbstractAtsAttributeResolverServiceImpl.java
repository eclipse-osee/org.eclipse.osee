/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsAttributeResolverServiceImpl implements IAttributeResolver {

   protected AtsApi atsApi;

   public AbstractAtsAttributeResolverServiceImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public String getStaticIdValue(IAtsWorkItem workItem, String key, String defaultValue) {
      for (String value : getAttributesToStringList(workItem, CoreAttributeTypes.StaticId)) {
         if (value.startsWith(key + "=")) {
            return value.replaceFirst(key + "=", "");
         }
      }
      return defaultValue;
   }

   @Override
   public void setStaticIdValue(IAtsWorkItem workItem, String key, String value, IAtsChangeSet changes) {
      for (IAttribute<?> attr : getAttributes(workItem, CoreAttributeTypes.StaticId)) {
         if (((String) attr.getValue()).startsWith(key + "=")) {
            changes.setAttribute(workItem, attr, key + "=" + value);
            return;
         }
      }
      changes.addAttribute(workItem, CoreAttributeTypes.StaticId, key + "=" + value);
   }

   @Override
   public boolean hasNoAttribute(IAtsWorkItem workItem, AttributeTypeToken... attrTypes) {
      return !hasAttribute(workItem, attrTypes);
   }

   @Override
   public boolean hasAttribute(IAtsWorkItem workItem, AttributeTypeToken... attrTypes) {
      for (AttributeTypeToken attrType : attrTypes) {
         if (getAttributeCount(workItem, attrType) > 0) {
            return true;
         }
      }
      return false;
   }

}
