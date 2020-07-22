/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class ArtEdAttrLabelProvider extends XViewerLabelProvider {

   public ArtEdAttrLabelProvider(ArtEdAttrXViewer xViewer) {
      super(xViewer);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn dCol, int columnIndex) {
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn aCol, int columnIndex) {
      Attribute<?> attr = (Attribute<?>) element;
      if (aCol.equals(ArtEdAttrXViewerFactory.AttrTypeName)) {
         return attr.getAttributeType().getName();
      } else if (aCol.equals(ArtEdAttrXViewerFactory.Value)) {
         return attr.getDisplayableString();
      } else if (aCol.equals(ArtEdAttrXViewerFactory.Id)) {
         return attr.getIdString();
      } else if (aCol.equals(ArtEdAttrXViewerFactory.AttrTypeId)) {
         return attr.getAttributeType().getIdString();
      } else if (aCol.equals(ArtEdAttrXViewerFactory.GammaId)) {
         return attr.getGammaId().toString();
      }
      return "Unhandled Column";
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

}
