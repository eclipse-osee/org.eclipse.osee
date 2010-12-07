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

import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class SimpleXWidgetWorkItem extends WorkWidgetDefinition {

   public SimpleXWidgetWorkItem(String name, String id, String xWidgetName, XOption... xOption) {
      super(name + " - " + id, id);
      DynamicXWidgetLayoutData data = new DynamicXWidgetLayoutData(null);
      data.setName(name);
      data.setId(id);
      data.setXWidgetName(xWidgetName);
      data.getXOptionHandler().add(xOption);
      set(data);
   }

}
