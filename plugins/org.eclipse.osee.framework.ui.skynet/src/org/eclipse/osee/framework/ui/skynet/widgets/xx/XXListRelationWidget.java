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
package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Bhawana Mishra
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXListRelationWidget extends XXListWidget {

   public static final WidgetId ID = WidgetId.XXListRelationWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXListRelationWidget() {
      this("");
   }

   public XXListRelationWidget(String label) {
      this(ID, label);
   }

   public XXListRelationWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      // Relation viewer: read-only display of related artifact names by default (plain bold label, disabled list).
      // VAL_NO_SEL replaces the VAL_MULTI_SEL inherited from XXListWidget so it is not a selection widget. A work def
      // can still opt into editing by adding XOption.WRITE.
      set(XOption.READ, XOption.VAL_NO_SEL);
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   /**
    * Load step (per lifecycle contract): resolve the related artifacts for this widget's relation and use their names
    * as the selectable/displayed values. Loading (the ORCS relation read) belongs here, NOT in refresh() which is
    * paint-only.
    */
   @Override
   protected void initializeSelected() {
      if (getArtifact() != null && getRelationTypeSide() != null && getRelationTypeSide().isValid()) {
         Collection<String> names =
            Collections.transform(getArtifact().getRelatedArtifacts(getRelationTypeSide()), ArtifactToken::getName);
         setSelectable(new ArrayList<>(names));
      }
   }

}
