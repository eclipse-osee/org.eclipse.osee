/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class XCheckBoxesExample extends XCheckBoxes {

   public static final Object WIDGET_ID = XCheckBoxesExample.class.getSimpleName();

   public XCheckBoxesExample() {
      super("Check Boxes Widget Example", 6);
   }

   @Override
   protected List<XCheckBoxData> getCheckBoxes() {
      List<XCheckBoxData> cbds = new ArrayList<>();
      cbds.add(new XCheckBoxData("Check Box 1", false));
      cbds.add(new XCheckBoxData("Check Box 2 default checked", true));
      cbds.add(new XCheckBoxData("Check Box 3", false));
      cbds.add(new XCheckBoxData("Check Box 4", false));
      cbds.add(new XCheckBoxData("Check Box 5", false));
      cbds.add(new XCheckBoxData("Check Box 6 with longer title", false));
      cbds.add(new XCheckBoxData("Check Box 7", false));
      return cbds;
   }

}
