/*********************************************************************
 * Copyright (c) 2021 Boeing
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
 * @author Donald G. Dunne
 */
public class XTargetedVersionWidget extends XHyperlabelVersionSelection {

   public static final String WIDGET_ID = XTargetedVersionWidget.class.getSimpleName();
   public static RelationTypeSide TARGETED_VERSION_RELATION = AtsRelationTypes.TeamWorkflowTargetedForVersion_Version;

   public XTargetedVersionWidget() {
      this("Targeted Version");
   }

   public XTargetedVersionWidget(String label) {
      super(label, TARGETED_VERSION_RELATION);
   }

}
