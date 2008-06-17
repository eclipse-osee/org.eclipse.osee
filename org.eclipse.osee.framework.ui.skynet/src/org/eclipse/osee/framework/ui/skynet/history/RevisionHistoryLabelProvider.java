/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.history;

import java.sql.Timestamp;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.revision.IAttributeChange;
import org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange;
import org.eclipse.osee.framework.skynet.core.revision.RevisionChange;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class RevisionHistoryLabelProvider implements ITableLabelProvider, ILabelProvider {

   public RevisionHistoryLabelProvider() {
      super();
   }

   public Image getColumnImage(Object element, int columnIndex) {

      if (element instanceof TransactionData && columnIndex == 0) {
         return SkynetGuiPlugin.getInstance().getImage("transaction.gif");

      } else if (element instanceof RevisionChange && columnIndex == 0) {
         return SkynetGuiPlugin.getInstance().getImage("transaction_detail.gif");
      } else if (element instanceof RelationLinkChange && columnIndex == 2) {
         ArtifactType descriptor = ((RelationLinkChange) element).getOtherArtifactDescriptor();
         if (descriptor == null)
            return null;
         else
            return descriptor.getImage();

      }
      return null;
   }

   /**
    * returns the text for a specific column
    */
   public String getColumnText(Object element, int columnIndex) {

      if (element instanceof TransactionData) {
         TransactionData data = (TransactionData) element;

         if (columnIndex == 0)
            return String.valueOf(data.getTransactionNumber());

         else if (columnIndex == 1)
            return String.valueOf((Timestamp) data.getTimeStamp());

         else if (columnIndex == 2)
            return String.valueOf(data.getName());

         else if (columnIndex == 3) return data.getComment();
      } else if (element instanceof IAttributeChange) {
         IAttributeChange change = (IAttributeChange) element;

         if (columnIndex == 0) {
            return String.valueOf(change.getGammaId());
         } else if (columnIndex == 1) {
            return change.getName();
         } else if (columnIndex == 2) {
            return "was:" + change.getWasValue();
         } else if (columnIndex == 3) {
            return "is:" + change.getChange();
         }
      } else if (element instanceof RelationLinkChange) {
         RelationLinkChange change = (RelationLinkChange) element;

         if (columnIndex == 0) {
            return String.valueOf(change.getGammaId());
         } else if (columnIndex == 1) {
            return change.getRelTypeName();
         } else if (columnIndex == 2) {
            return change.getOtherArtifactName();
         } else if (columnIndex == 3) {
            return change.getRationale();
         }
      }

      return "";
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void dispose() {
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public Image getImage(Object element) {
      return getColumnImage(element, 0);
   }

   /**
    * return the text for the first column as default
    */
   public String getText(Object element) {
      return getColumnText(element, 0);
   }
}
