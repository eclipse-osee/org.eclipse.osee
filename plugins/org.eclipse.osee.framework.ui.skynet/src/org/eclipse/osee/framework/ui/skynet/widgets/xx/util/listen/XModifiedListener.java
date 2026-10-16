/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen;

import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public interface XModifiedListener {

   default public void widgetModified(XWidget widget) {
      // do nothing
   }

   default public void handleRightClick(XWidget widget) {
      // do nothing
   }

}
