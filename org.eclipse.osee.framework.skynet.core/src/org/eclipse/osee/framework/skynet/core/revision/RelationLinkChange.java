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
package org.eclipse.osee.framework.skynet.core.revision;

import static org.eclipse.osee.framework.skynet.core.change.ChangeType.CONFLICTING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.INCOMING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.OUTGOING;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.CHANGE;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.DELETED;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.NEW;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Robert A. Fisher
 */
public class RelationLinkChange extends RevisionChange implements IRelationLinkChange {
   private static final SkynetActivator plugin = SkynetActivator.getInstance();
   private static final String BASE_IMAGE_STRING = "relate";
   private static final long serialVersionUID = 1L;
   private static boolean imagesInitialized;
   private int relLinkId;
   private String rationale;
   private int order;
   private String relTypeName;
   private String otherArtifactName;
   private ArtifactType otherArtifactDescriptor;

   /**
    * Constructor for deserialization.
    */
   protected RelationLinkChange() {

   }

   /**
    * Constructs a new or modified link change.
    * 
    * @param modType
    * @param gammaId
    * @param rationale
    * @param order
    * @param relTypeName
    * @param otherArtifactName
    * @param otherArtifactDescriptor
    */
   public RelationLinkChange(ChangeType changeType, ModificationType modType, int relLinkId, long gammaId, String rationale, int order, String relTypeName, String otherArtifactName, ArtifactType otherArtifactDescriptor) {
      super(changeType, modType, gammaId);
      this.relLinkId = relLinkId;
      this.rationale = rationale;
      this.order = order;
      this.relTypeName = relTypeName;
      this.otherArtifactName = otherArtifactName;
      this.otherArtifactDescriptor = otherArtifactDescriptor;
   }

   /**
    * Constructs a deleted link change.
    * 
    * @param gammaId
    * @param relTypeName
    * @param otherArtifactName
    * @param otherArtifactDescriptor
    */
   public RelationLinkChange(ChangeType changeType, int relLinkId, long gammaId, String relTypeName, String otherArtifactName, ArtifactType otherArtifactDescriptor) {
      super(changeType, DELETED, gammaId);
      this.relLinkId = relLinkId;
      this.relTypeName = relTypeName;
      this.otherArtifactName = otherArtifactName;
      this.otherArtifactDescriptor = otherArtifactDescriptor;
   }

   /**
    * @return Returns the text for 'change' text.
    */
   @Override
   public String getChange() {
      return rationale;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getImage()
    */
   @Override
   public Image getImage() {
      return getImage(getChangeType(), getModType());
   }

   protected static Image getImage(ChangeType changeType, ModificationType modType) {
      checkImageRegistry();
      return plugin.getImage(BASE_IMAGE_STRING + changeType + modType);
   }

   private static void checkImageRegistry() {
      if (!imagesInitialized) {
         imagesInitialized = true;

         ImageDescriptor outNew = plugin.getImageDescriptor("out_new.gif");
         ImageDescriptor outChange = plugin.getImageDescriptor("out_change.gif");
         ImageDescriptor outDeleted = plugin.getImageDescriptor("out_delete.gif");
         ImageDescriptor incNew = plugin.getImageDescriptor("inc_new.gif");
         ImageDescriptor incChange = plugin.getImageDescriptor("inc_change.gif");
         ImageDescriptor incDeleted = plugin.getImageDescriptor("inc_delete.gif");
         ImageDescriptor conChange = plugin.getImageDescriptor("con_change.gif");
         ImageDescriptor conDeleted = plugin.getImageDescriptor("con_delete.gif");

         Image baseImage = plugin.getImage(BASE_IMAGE_STRING + ".gif");

         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + DELETED, new OverlayImage(baseImage, outDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + CHANGE, new OverlayImage(baseImage, outChange));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + NEW, new OverlayImage(baseImage, outNew));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + DELETED, new OverlayImage(baseImage, incDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + CHANGE, new OverlayImage(baseImage, incChange));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + NEW, new OverlayImage(baseImage, incNew));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + DELETED, new OverlayImage(baseImage, conDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + CHANGE, new OverlayImage(baseImage, conChange));
      }
   }

   /**
    * @return Returns the order.
    */
   public int getOrder() {
      return order;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getOtherArtifactDescriptor()
    */
   public ArtifactType getOtherArtifactDescriptor() {
      return otherArtifactDescriptor;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getOtherArtifactName()
    */
   public String getOtherArtifactName() {
      return otherArtifactName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getRationale()
    */
   public String getRationale() {
      return rationale;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getRelTypeName()
    */
   public String getRelTypeName() {
      return relTypeName;
   }

   /**
    * @return Returns the relLinkId.
    */
   public int getRelLinkId() {
      return relLinkId;
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }
}
