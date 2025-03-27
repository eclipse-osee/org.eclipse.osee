/*******************************************************************************
 * Copyright (c) 2024 Boeing.
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
package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.osee.framework.jdk.core.util.WidgetHint;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelIntroducedInVersionSelection extends XHyperlabelVersionSelection {

   public static final String LABEL = "Introduced-In Version";

   public XHyperlabelIntroducedInVersionSelection() {
      super(LABEL);
      getWidgetHints().add(WidgetHint.SortAscending);
   }

}
