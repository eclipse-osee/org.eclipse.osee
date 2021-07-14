/*********************************************************************
 * Copyright (c) 2019 Boeing
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

import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Jeremy A. Midvidy
 * @author Donald G. Dunne
 */
public class XFoundInVersionWidget extends XHyperlabelVersionSelection {

   public static final String WIDGET_ID = XFoundInVersionWidget.class.getSimpleName();
   public static RelationTypeSide FOUND_VERSION_RELATION = AtsRelationTypes.TeamWorkflowToFoundInVersion_Version;

   public XFoundInVersionWidget(String label) {
      super("Found In Version", FOUND_VERSION_RELATION);
   }

}
