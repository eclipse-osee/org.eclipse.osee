/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.OverlayImage.Location;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class MassLabelProvider extends XViewerLabelProvider {

   private final MassXViewer xViewer;

   public MassLabelProvider(MassXViewer xViewer) {
      super(xViewer);
      this.xViewer = xViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) throws XViewerException {
      Image image = null;

      if (element instanceof Artifact) {
         Artifact artifact = (Artifact) element;

         if (columnIndex == 0) {
            if (artifact.isDeleted()) {
               image = ArtifactImageManager.getImage(artifact, FrameworkImage.PURGE, Location.BOT_LEFT);
            } else if (artifact.isReadOnly()) {
               image = ArtifactImageManager.getImage(artifact, FrameworkImage.LOCK_OVERLAY, Location.BOT_LEFT);
            } else {
               image = ArtifactImageManager.getImage(artifact);
            }
         } else {
            if (col instanceof XViewerValueColumn) {
               XViewerValueColumn valueColumn = (XViewerValueColumn) col;
               return valueColumn.getColumnImage(element, col, columnIndex);
            }
         }
      }
      return image;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn col, int columnIndex) throws XViewerException {
      try {
         if (col == null) {
            return "";
         }
         if (col instanceof XViewerValueColumn) {
            return ((XViewerValueColumn) col).getColumnText(element, col, columnIndex);
         }
         if (element instanceof String) {
            if (columnIndex == 1) {
               return (String) element;
            } else {
               return "";
            }
         }
         Artifact artifact = (Artifact) element;
         if (artifact == null || artifact.isDeleted()) {
            return "";
         }
         // Handle case where columns haven't been loaded yet
         if (columnIndex > getTreeViewer().getTree().getColumns().length - 1) {
            return "";
         }

         AttributeTypeToken attributeType = null;
         if (Long.parseLong(col.getId()) > 0) {
            attributeType = AttributeTypeManager.getAttributeType(Long.parseLong(col.getId()));
         }
         if (attributeType == null && Strings.isValid(col.getName())) {
            attributeType = AttributeTypeManager.getType(col.getName());
         }
         if (!artifact.isAttributeTypeValid(attributeType)) {
            return "";
         }
         if (AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeType)) {
            try {
               return new DateAttribute().MMDDYYHHMM.format(artifact.getSoleAttributeValue(attributeType));
            } catch (OseeCoreException ex) {
               return "";
            }
         }

         return artifact.getAttributesToString(attributeType);
      } catch (OseeCoreException ex) {
         throw new XViewerException(ex);
      }
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

   public MassXViewer getTreeViewer() {
      return xViewer;
   }

   @Override
   public void dispose() {
      // do nothing
   }
}
