/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.signby;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;

/**
 * This class should not be overridden for Work Definitions but instead use SignbyWidgetDefinition.
 *
 * @author Doanld G. Dunne
 */
public class XSignByAndDateWidget extends XAbstractSignByAndDateButton {

   public XSignByAndDateWidget() {
      super(AtsAttributeTypes.SignedOffBy, AtsAttributeTypes.SignedOffByDate);
   }

}
