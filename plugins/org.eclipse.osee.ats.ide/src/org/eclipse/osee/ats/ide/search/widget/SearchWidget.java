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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;
import org.eclipse.osee.framework.core.widget.WidgetId;

/**
 * @author Donald G. Dunne
 */
public class SearchWidget extends OseeEnum {

   private static final Long ENUM_ID = 992305828L;

   private final WidgetId widgetId;

   public SearchWidget(long id, String name, WidgetId widgetId) {
      super(ENUM_ID, id, name);
      this.widgetId = widgetId;
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return TitleSearchWidget.TitleWidget;
   }

   public WidgetId getWidgetId() {
      return widgetId;
   }

}
