/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.util.xviewer.column;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.util.ColorColumn;
import org.eclipse.osee.ats.ide.column.CompletedCancelledDateColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IAttributeColumn;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Eventually, when all ATS columns are converted to value columns, this class should implement IAltLeftClickProvider,
 * IXViewerValueColumn. Until then, just provide IXViewerValueColumn methods needed for subclasses to not have to
 * implement each.
 *
 * @author Donald G. Dunne
 */
public abstract class XViewerAtsColumn extends XViewerColumn {

   private Map<Object, String> elementToForegroundColor;
   private Map<Object, String> elementToBackgroundColor;
   private Map<String, Color> hexColorToColor;
   private Boolean hasColorColumn = null;
   private ColorColumn colorColumn;
   private Boolean actionRollup = null;
   private Boolean inheritParent = null;

   protected XViewerAtsColumn() {
      // do nothing
   }

   public XViewerAtsColumn(String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   public XViewerAtsColumn(XViewer xViewer, String xml) {
      super(xViewer, xml);
   }

   @Override
   protected void copy(XViewerColumn fromXCol, XViewerColumn toXCol) {
      super.copy(fromXCol, toXCol);
      if (fromXCol instanceof XViewerAtsColumn && toXCol instanceof XViewerAtsColumn) {
         ((XViewerAtsColumn) toXCol).setActionRollup(actionRollup);
         ((XViewerAtsColumn) toXCol).setInheritParent(inheritParent);
         ((XViewerAtsColumn) toXCol).setHasColorColumn(hasColorColumn, colorColumn);
      }
      if (fromXCol instanceof IAttributeColumn && toXCol instanceof IAttributeColumn) {
         ((IAttributeColumn) toXCol).setAttributeType(((IAttributeColumn) this).getAttributeType());
      }
   }

   public Image getColumnImage(Object element, XViewerColumn column, int columnIndex) {
      return null;
   }

   public Color getBackground(Object element, XViewerColumn xCol, int columnIndex) {
      if (isColorColumn()) {
         return getColor(element, true, columnIndex);
      }
      return null;
   }

   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      if (isColorColumn()) {
         return getColor(element, false, columnIndex);
      }
      return null;
   }

   public StyledString getStyledText(Object element, XViewerColumn viewerColumn, int columnIndex) {
      return null;
   }

   public Font getFont(Object element, XViewerColumn viewerColumn, int columnIndex) {
      return null;
   }

   /**
    * Returns the backing data object for operations like sorting
    */
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      XViewerAtsColumn xViewerAtsColumn;
      if (xCol instanceof IAttributeColumn) {
         IAttributeColumn attrCol = (IAttributeColumn) xCol;
         return AtsApiService.get().getColumnService().getColumnDate(attrCol.getAttributeType(),
            (IAtsWorkItem) element);
      }
      if (xCol.getId().equals(AtsColumnTokensDefault.CompletedCancelledDateColumn.getId())) {
         xViewerAtsColumn = CompletedCancelledDateColumnUI.getInstance();
      } else {
         return null;
      }
      return xViewerAtsColumn.getBackingData(element, xCol, columnIndex);
   }

   private Color getColor(Object element, boolean background, int columnIndex) {
      Color resultColor = null;
      if (isColorColumn()) {
         String hexColor = null;
         if (background) {
            hexColor = elementToBackgroundColor.get(element);
         } else {
            hexColor = elementToForegroundColor.get(element);
         }
         if (!Strings.isValid(hexColor)) {
            Color color = hexColorToColor.get(hexColor);
            if (color != null) {
               resultColor = color;
            } else {
               try {
                  String value = null;
                  if (this instanceof IXViewerPreComputedColumn) {
                     IXViewerPreComputedColumn ixViewerPreComputedColumn = (IXViewerPreComputedColumn) this;
                     value = ixViewerPreComputedColumn.getText(element, ixViewerPreComputedColumn.getKey(element), "");
                  } else if (this instanceof IXViewerValueColumn) {
                     IXViewerValueColumn valueColumn = (IXViewerValueColumn) this;
                     value = valueColumn.getColumnText(element, this, columnIndex);
                  } else {
                     if (getStyledText(element, this, 0) != null) {
                        value = getStyledText(element, this, 0).getString();
                     }
                  }
                  if (Strings.isValid(value)) {
                     if (background) {
                        hexColor = colorColumn.getBackgroundColorHex(value);
                     } else {
                        hexColor = colorColumn.getForgroundColorHex(value);
                     }
                  }
               } catch (Exception ex) {
                  // do nothing
               }
            }
         }
         if (Strings.isValid(hexColor)) {
            resultColor = hexColorToColor.get(hexColor);
            if (resultColor == null) {
               resultColor = Displays.getColor(Integer.valueOf(hexColor.substring(1, 3), 16),
                  Integer.valueOf(hexColor.substring(3, 5), 16), Integer.valueOf(hexColor.substring(5, 7), 16));
               hexColorToColor.put(hexColor, resultColor);
               if (background) {
                  elementToBackgroundColor.put(element, hexColor);
               } else {
                  elementToForegroundColor.put(element, hexColor);
               }
            }
         }
      }
      return resultColor;
   }

   public boolean isColorColumn() {
      if (hasColorColumn == null) {
         colorColumn =
            AtsApiService.get().getConfigService().getConfigurations().getColorColumns().getColumnById(getId());
         hasColorColumn = colorColumn != null;
         if (hasColorColumn) {
            elementToForegroundColor = new HashMap<>(100);
            elementToBackgroundColor = new HashMap<>(100);
            hexColorToColor = new HashMap<>(25);
         }
      }
      return hasColorColumn;
   }

   public Boolean isActionRollup() {
      return actionRollup;
   }

   public void setHasColorColumn(Boolean hasColorColumn, ColorColumn colorColumn) {
      this.hasColorColumn = hasColorColumn;
      this.colorColumn = colorColumn;
   }

   public void setActionRollup(Boolean actionRollup) {
      this.actionRollup = actionRollup;
   }

   public Boolean isInheritParent() {
      return inheritParent;
   }

   public void setInheritParent(Boolean inheritParent) {
      this.inheritParent = inheritParent;
   }

}
