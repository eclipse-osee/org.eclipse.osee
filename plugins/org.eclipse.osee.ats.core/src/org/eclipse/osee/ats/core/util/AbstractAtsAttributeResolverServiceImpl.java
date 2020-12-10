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
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsAttributeResolverServiceImpl implements IAttributeResolver {

   protected Log logger;
   protected AtsApi atsApi;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setServices(AtsApi atsApi) {
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

}
