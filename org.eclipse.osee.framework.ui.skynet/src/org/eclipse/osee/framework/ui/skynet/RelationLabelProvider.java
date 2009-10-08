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

import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public class RelationLabelProvider implements ITableLabelProvider, ILabelProvider {
   private Artifact artifact;

   public RelationLabelProvider(Artifact artifact) {
      this.artifact = artifact;
   }

   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof RelationType && columnIndex == 0) {
         return ImageManager.getImage(FrameworkImage.RELATION);
      } else if (element instanceof WrapperForRelationLink && columnIndex == 0) {
         WrapperForRelationLink artifact = (WrapperForRelationLink) element;
         try {
            return ImageManager.getImage(artifact.getOther());
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof RelationTypeSideSorter && columnIndex == 0) {
         RelationTypeSideSorter side = (RelationTypeSideSorter) element;
         String sideDescription;
         try {
            sideDescription = " has [" + side.getRelationType().getMultiplicity().asLimitLabel(side.getSide()) + "] ";
         } catch (OseeCoreException ex) {
            sideDescription = ex.getLocalizedMessage();
         }
         return side.getSideName() + sideDescription + (side.isSideA() ? side.getRelationType().getSideBName() : side.getRelationType().getSideAName()) + " \"" + artifact.getName() + "\"";
      } else if (element instanceof RelationType) {
         if (columnIndex == 0) {
            return ((RelationType) element).getName();
         }
      } else if (element instanceof RelationLink) {
         RelationLink link = (RelationLink) element;
         if (columnIndex == 0) {
            try {
               return link.getArtifactOnOtherSide(artifact).getName();
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         } else if (columnIndex == 1) {
            return link.getRationale();
         }
      } else if (element instanceof WrapperForRelationLink) {
         WrapperForRelationLink wrapper = (WrapperForRelationLink) element;
         if (columnIndex == 0) {
            return wrapper.getOther().getName();
         } else if (columnIndex == 1) {
            String rationale = "";
            try {
               rationale =
                     RelationManager.getRelationRationale(wrapper.getArtifactA(), wrapper.getArtifactB(),
                           wrapper.getRelationType());
            } catch (OseeCoreException ex) {

            }
            return rationale;
         }
      }
      return "";
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void dispose() {
   }

   public boolean isLabelProperty(Object element, String property) {
      return true;
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public Image getImage(Object element) {
      return getColumnImage(element, 0);
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