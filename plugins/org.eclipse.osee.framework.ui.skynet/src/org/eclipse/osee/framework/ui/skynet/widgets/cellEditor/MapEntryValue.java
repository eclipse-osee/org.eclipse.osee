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

package org.eclipse.osee.framework.ui.skynet.widgets.cellEditor;

import java.util.Map;
import java.util.Objects;
import org.eclipse.swt.widgets.Control;

/**
 * @author Loren K. Ashley
 */

public class MapEntryValue extends UniversalCellEditorValue {

   private Map.Entry<String, String> mapEntry;

   public MapEntryValue() {
      super();
   }

   @Override
   public Control prepareControl(UniversalCellEditor universalEditor) {
      var mapEntryWidget = universalEditor.getMapEntryControl();
      if (Objects.nonNull(this.mapEntry)) {
         mapEntryWidget.setMapEntry(this.mapEntry);
      }
      return mapEntryWidget;
   }

   public void setValue(Map.Entry<String, String> mapEntry) {
      this.mapEntry = mapEntry;
   }

}
