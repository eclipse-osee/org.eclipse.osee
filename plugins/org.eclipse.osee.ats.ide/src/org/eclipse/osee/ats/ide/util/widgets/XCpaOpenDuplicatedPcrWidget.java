/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public class XCpaOpenDuplicatedPcrWidget extends XCpaOpenPcrWidget {

   public static final String WIDGET_ID = XCpaOpenDuplicatedPcrWidget.class.getSimpleName();

   public XCpaOpenDuplicatedPcrWidget() {
      super(AtsAttributeTypes.DuplicatedPcrId);
   }

}
