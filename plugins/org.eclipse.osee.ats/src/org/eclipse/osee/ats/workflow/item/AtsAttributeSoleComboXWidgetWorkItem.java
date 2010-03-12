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
public class AtsAttributeSoleComboXWidgetWorkItem extends WorkWidgetDefinition {

   public AtsAttributeSoleComboXWidgetWorkItem(ATSAttributes atsAttribute, String commaOptions, XOption... xOption) {
      super(atsAttribute.getDisplayName() + " - " + atsAttribute.getStoreName(), atsAttribute.getStoreName());
      DynamicXWidgetLayoutData data = new DynamicXWidgetLayoutData(null);
      data.setName(getName());
      data.setStorageName(getId());
      data.setXWidgetName("XComboDam(" + commaOptions + ")");
      data.getXOptionHandler().add(xOption);
      set(data);
   }
}
