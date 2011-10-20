/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.view.web.components;

import org.eclipse.osee.display.view.web.CssConstants;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeLeftMarginContainer extends HorizontalLayout {
   private Label hSpacer_LeftMargin = new Label();

   public OseeLeftMarginContainer() {
      super();

      setSizeUndefined();

      hSpacer_LeftMargin.setWidth(CssConstants.OSEE_LEFTMARGINWIDTH, UNITS_PIXELS);

      addComponent(hSpacer_LeftMargin);
   }

}
