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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Donald G. Dunne
 */
public class DefaultXWidgetOptionResolver implements IXWidgetOptionResolver {

   public String[] getWidgetOptions(DynamicXWidgetLayoutData xWidgetData) {
      Matcher m = Pattern.compile("\\((.*?)\\)$").matcher(xWidgetData.getXWidgetName());

      if (m.find()) {
         String data = m.group(1);
         return data.split(",");
      }
      return new String[] {};
   }

}
