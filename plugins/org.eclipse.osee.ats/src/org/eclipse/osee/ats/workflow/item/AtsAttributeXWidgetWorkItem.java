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
package org.eclipse.osee.ats.workflow.item;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeXWidgetWorkItem extends WorkWidgetDefinition {

   public AtsAttributeXWidgetWorkItem(String name, String id, String attributeTypeName, String xWidgetName, XOption... xOption) {
      super(name + " - " + id, id);
      DynamicXWidgetLayoutData data = new DynamicXWidgetLayoutData(null);
      data.setName(name);
      data.setId(id);
      data.setStorageName(attributeTypeName);
      data.setXWidgetName(xWidgetName);
      data.getXOptionHandler().add(xOption);
      set(data);
   }

   public AtsAttributeXWidgetWorkItem(ATSAttributes atsAttribute, String xWidgetName, XOption... xOption) {
      this(atsAttribute.getDisplayName(), atsAttribute.getStoreName(), atsAttribute.getStoreName(), xWidgetName,
            xOption);
   }

   public void setDefaultValue(String defaultValue) {
      DynamicXWidgetLayoutData data = get();
      data.setDefaultValue(defaultValue);
      set(data);
   }
}
