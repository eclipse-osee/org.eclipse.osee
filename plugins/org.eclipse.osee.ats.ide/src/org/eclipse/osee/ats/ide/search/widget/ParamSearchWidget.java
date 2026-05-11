/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.search.widget;

import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

public interface ParamSearchWidget {

   public String getName();

   default public void widgetCreated(XWidget xWidget) {
      // do nothing
   }

   default public void widgetCreating(XWidget xWidget) {
      // do nothing
   }

}
