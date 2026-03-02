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
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * Select and persist with clear button
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XTargetedVersionPersistWidget extends XAbstractHyperlabelVersionSelPersistWidget {

   public static WidgetId ID = WidgetIdAts.XTargetedVersionPersistWidget;
   public static RelationTypeSide TARGETED_VERSION_RELATION = AtsRelationTypes.TeamWorkflowTargetedForVersion_Version;

   public XTargetedVersionPersistWidget() {
      this("Targeted Version");
   }

   public XTargetedVersionPersistWidget(String label) {
      super(ID, label, TARGETED_VERSION_RELATION);
   }

}
