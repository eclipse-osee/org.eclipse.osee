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
package org.eclipse.osee.framework.ui.skynet;

import java.text.NumberFormat;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.IRelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLinkGroup;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public class RelationLabelProvider implements ITableLabelProvider, ILabelProvider {
   private static NumberFormat numberFormat = NumberFormat.getNumberInstance();
   private static Image RELATION_IMAGE = SkynetGuiPlugin.getInstance().getImage("relate.gif");
   private Artifact artifact;

   /**
    * 
    */
   public RelationLabelProvider(Artifact artifact) {
      super();
      this.artifact = artifact;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
    */
   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof IRelationType && columnIndex == 0) {
         return RELATION_IMAGE;
      } else if (element instanceof RelationLink && columnIndex == 0) {
         RelationLink link = (RelationLink) element;
         return (link.getArtifactA() == artifact) ? link.getArtifactB().getArtifactType().getImage() : link.getArtifactA().getArtifactType().getImage();
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
    */
   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof RelationLinkGroup) {
         if (columnIndex == 0) return ((RelationLinkGroup) element).getSideName();
      } else if (element instanceof IRelationType) {
         if (columnIndex == 0) return ((IRelationType) element).getTypeName();
      } else if (element instanceof RelationLink) {
         RelationLink link = (RelationLink) element;
         if (columnIndex == 0)
            return (link.getArtifactA() == artifact) ? link.getArtifactB().getDescriptiveName() : link.getArtifactA().getDescriptiveName();
         else if (columnIndex == 1)
            return link.getRationale();
         else if (columnIndex == 2) return (link.getArtifactA() == artifact) ? numberFormat.format(link.getAOrder()) : numberFormat.format(link.getBOrder());
      } else {
         throw new IllegalArgumentException("wrong type: " + element.getClass().getName());
      }
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void addListener(ILabelProviderListener listener) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
    */
   public void dispose() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
    *      java.lang.String)
    */
   public boolean isLabelProperty(Object element, String property) {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void removeListener(ILabelProviderListener listener) {
   }

   public Image getImage(Object element) {
      if (element instanceof IRelationType) {
         return RELATION_IMAGE;
      }
      return null;
   }

   public String getText(Object element) {
      return getColumnText(element, 0);
   }

   /**
    * @param artifact The artifact to set.
    */
   public void setArtifact(Artifact artifact) {
      this.artifact = artifact;
   }
}