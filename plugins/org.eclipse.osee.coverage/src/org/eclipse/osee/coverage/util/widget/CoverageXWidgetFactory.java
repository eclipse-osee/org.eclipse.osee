/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.util.widget;

import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetProvider;

/**
 * @author Donald G. Dunne
 */
public class CoverageXWidgetFactory implements IXWidgetProvider {

   @Override
   public XWidget createXWidget(String widgetName, String name, DynamicXWidgetLayoutData widgetLayoutData) {
      if (widgetName.equals("XHyperlabelCoverageMethodSelection")) {
         return new XHyperlabelCoverageMethodSelection();
      }
      return null;
   }

}
