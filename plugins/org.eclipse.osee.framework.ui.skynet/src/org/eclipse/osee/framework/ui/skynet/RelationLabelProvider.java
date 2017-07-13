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
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public class RelationLabelProvider implements ITableLabelProvider, ILabelProvider {
   private static final Image LOCK_IMAGE = ImageManager.getImage(FrameworkImage.LOCK_OVERLAY);

   private Artifact artifact;

   public RelationLabelProvider(Artifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public Image getColumnImage(Object element, int columnIndex) {
      Image toReturn = null;
      if (element instanceof RelationType && columnIndex == 0) {
         toReturn = ImageManager.getImage(FrameworkImage.RELATION);
      } else if (element instanceof WrapperForRelationLink && columnIndex == 0) {
         WrapperForRelationLink relationLinkWrapper = (WrapperForRelationLink) element;
         try {
            toReturn = ArtifactImageManager.getImage(relationLinkWrapper.getOther());
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } else if (element instanceof RelationTypeSide && columnIndex == 0) {
         toReturn = getImage((RelationTypeSide) element);
      }
      return toReturn;
   }

   private boolean isLocked(RelationTypeSide relationTypeSide) {
      boolean isLocked = true;
      AccessPolicy policyHandlerService;
      try {
         policyHandlerService = ServiceUtil.getAccessPolicy();
         PermissionStatus permissionStatus =
            policyHandlerService.canRelationBeModified(artifact, null, relationTypeSide, Level.FINE);
         isLocked = !permissionStatus.matched();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return isLocked;
   }

   private Image getImage(RelationTypeSide relationTypeSide) {
      boolean isLocked = isLocked(relationTypeSide);
      if (isLocked) {
         return LOCK_IMAGE;
      }

      RelationSide side = relationTypeSide.getSide();
      try {
         RelationType type = RelationTypeManager.getType(relationTypeSide);
         RelationTypeMultiplicity multiplicity = type.getMultiplicity();

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

   @Override
   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof RelationTypeSide && columnIndex == 0) {
         RelationTypeSide side = (RelationTypeSide) element;
         try {
            return RelationTypeManager.getType(side).getSideName(side.getSide());
         } catch (OseeCoreException ex) {
            return ex.toString();
         }
      }
      if (columnIndex == 1 && element instanceof WrapperForRelationLink) {
         WrapperForRelationLink wrapper = (WrapperForRelationLink) element;
         return wrapper.getOther().getIdString();
      }
      if (columnIndex == 3 && element instanceof WrapperForRelationLink) {
         WrapperForRelationLink wrapper = (WrapperForRelationLink) element;
         return wrapper.getRelation().getIdString();
      }
      if (columnIndex == 4 && element instanceof WrapperForRelationLink) {
         WrapperForRelationLink wrapper = (WrapperForRelationLink) element;
         return String.valueOf(wrapper.getRelation().getGammaId());
      }
      if (element instanceof RelationType) {
         if (columnIndex == 0) {
            return ((RelationType) element).getName();
         }
      } else if (element instanceof RelationLink) {
         RelationLink link = (RelationLink) element;
         if (columnIndex == 0) {
            try {
               return link.getOtherSideArtifact(artifact).getName();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         } else if (columnIndex == 2) {
            return link.getRationale();
         }
      } else if (element instanceof WrapperForRelationLink) {
         WrapperForRelationLink wrapper = (WrapperForRelationLink) element;
         if (columnIndex == 0) {
            return wrapper.getOther().getName();
         } else if (columnIndex == 2) {
            String rationale = "";
            try {
               RelationLink link = RelationManager.getRelationLink(wrapper.getArtifactA(), wrapper.getArtifactB(),
                  wrapper.getRelationType());
               rationale = link.getRationale();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
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