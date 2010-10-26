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
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.RelationTypeSide;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public class RelationLabelProvider implements ITableLabelProvider, ILabelProvider {
   private Artifact artifact;

   public RelationLabelProvider(Artifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof RelationType && columnIndex == 0) {
         return ImageManager.getImage(FrameworkImage.RELATION);
      } else if (element instanceof WrapperForRelationLink && columnIndex == 0) {
         WrapperForRelationLink artifact = (WrapperForRelationLink) element;
         try {
            return ArtifactImageManager.getImage(artifact.getOther());
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      } else if (element instanceof RelationTypeSide && columnIndex == 0) {
         try {
            RelationTypeSide relationTypeSide = (RelationTypeSide) element;
            RelationSide side = relationTypeSide.getSide();
            RelationTypeMultiplicity multiplicity = RelationTypeManager.getType(relationTypeSide).getMultiplicity();

            if (side == RelationSide.SIDE_A) {
               if (multiplicity.getSideALimit() == 1) {
                  return ImageManager.getImage(FrameworkImage.LEFT_ARROW_1);
               } else {
                  return ImageManager.getImage(FrameworkImage.LEFT_ARROW_N);
               }
            } else {
               if (multiplicity.getSideBLimit() == 1) {
                  return ImageManager.getImage(FrameworkImage.RIGHT_ARROW_1);
               } else {
                  return ImageManager.getImage(FrameworkImage.RIGHT_ARROW_N);
               }
            }
         } catch (OseeCoreException ex) {
            return null;
         }
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof RelationTypeSide && columnIndex == 0) {
         RelationTypeSide side = (RelationTypeSide) element;
         try {
            return RelationTypeManager.getType(side).getSideName(side.getSide());
         } catch (OseeCoreException ex) {
            return ex.toString();
         }
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
               RelationLink link =
                  RelationManager.getRelationLink(wrapper.getArtifactA(), wrapper.getArtifactB(),
                     wrapper.getRelationType());
               rationale = link.getRationale();
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
            return rationale;
         }
      }
      return "";
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return true;
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public Image getImage(Object element) {
      return getColumnImage(element, 0);
   }

   @Override
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