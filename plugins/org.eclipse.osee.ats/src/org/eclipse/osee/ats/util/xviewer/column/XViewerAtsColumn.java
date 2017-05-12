/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.xviewer.column;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.util.ColorColumn;
import org.eclipse.osee.ats.column.CancelledDateColumnUI;
import org.eclipse.osee.ats.column.CompletedDateColumnUI;
import org.eclipse.osee.ats.column.CreatedDateColumnUI;
import org.eclipse.osee.ats.column.IPersistAltLeftClickProvider;
import org.eclipse.osee.ats.column.ReleaseDateColumn;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
   }

   protected boolean isPersistViewer() {
      return isPersistViewer((XViewer) getXViewer());
   }

   protected boolean isPersistViewer(XViewer xViewer) {
      return AtsAttributeColumnUtility.isPersistViewer(xViewer);
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

   protected boolean isPersistAltLeftClick() {
      XViewer xViewer = (XViewer) getXViewer();
      if (xViewer instanceof IPersistAltLeftClickProvider) {
         return ((IPersistAltLeftClickProvider) xViewer).isAltLeftClickPersist();
      }
      return false;
   }

   /**
    * Returns the backing data object for operations like sorting
    */
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      XViewerAtsColumn xViewerAtsColumn;
      if (xCol.getId().equals(AtsColumnId.CreatedDate.getId())) {
         xViewerAtsColumn = CreatedDateColumnUI.getInstance();
      } else if (xCol.getId().equals(AtsColumnId.ReleaseDate.getId())) {
         xViewerAtsColumn = ReleaseDateColumn.getInstance();
      } else if (xCol.getId().equals(AtsColumnId.CompletedDate.getId())) {
         xViewerAtsColumn = CompletedDateColumnUI.getInstance();
      } else if (xCol.getId().equals(AtsColumnId.CancelledDate.getId())) {
         xViewerAtsColumn = CancelledDateColumnUI.getInstance();
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
                     value = getStyledText(element, this, 0).getString();
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
         colorColumn = AtsClientService.get().getConfigurations().getColorColumns().getColumnById(getId());
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
