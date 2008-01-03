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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType.CONFLICTING;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType.INCOMING;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType.OUTGOING;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.CHANGE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.DELETE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.NEW;
import java.io.InputStream;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Robert A. Fisher
 */
public class AttributeChange extends RevisionChange implements IAttributeChange {
   private static final long serialVersionUID = -8403712077455896863L;
   private static final SkynetActivator plugin = SkynetActivator.getInstance();
   private static final String BASE_IMAGE_STRING = "molecule";
   private static boolean imagesInitialized;

   private int attrId;
   private String name;
   private String isValue;
   private String wasValue;

   @SuppressWarnings("unused")
   transient private InputStream isContent;
   @SuppressWarnings("unused")
   transient private InputStream wasContent;

   /**
    * Constructor for serialization.
    */
   protected AttributeChange() {

   }

   /**
    * Constructor for making new and modified attribute changes.
    * 
    * @param modType
    * @param gammaId
    */
   public AttributeChange(ChangeType changeType, ModificationType modType, int attrId, long gammaId, String name, String isValue, InputStream isContent, String wasValue, InputStream wasContent) {
      super(changeType, modType, gammaId);
      this.attrId = attrId;
      this.name = name;
      this.isValue = isValue;
      this.isContent = isContent;
      this.wasValue = wasValue;
      this.wasContent = wasContent;
   }

   /**
    * Constructor for making deleted attribute changes.
    * 
    * @param gammaId
    */
   public AttributeChange(ChangeType changeType, int attrId, long gammaId, String name, String wasValue) {
      super(changeType, DELETE, gammaId);
      this.attrId = attrId;
      this.name = name;
      this.isValue = null;
      this.wasValue = wasValue;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getChange()
    */
   @Override
   public String getChange() {
      if (getModType() == DELETE)
         return "<deleted>";
      else
         return isValue;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getWasValue()
    */
   public String getWasValue() {
      return wasValue;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getName()
    */
   public String getName() {
      return name;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getImage()
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

         ImageDescriptor outNew = SkynetActivator.getInstance().getImageDescriptor("out_new.gif");
         ImageDescriptor outChange = SkynetActivator.getInstance().getImageDescriptor("out_change.gif");
         ImageDescriptor outDeleted = SkynetActivator.getInstance().getImageDescriptor("out_delete.gif");
         ImageDescriptor incNew = SkynetActivator.getInstance().getImageDescriptor("inc_new.gif");
         ImageDescriptor incChange = SkynetActivator.getInstance().getImageDescriptor("inc_change.gif");
         ImageDescriptor incDeleted = SkynetActivator.getInstance().getImageDescriptor("inc_delete.gif");
         ImageDescriptor conChange = SkynetActivator.getInstance().getImageDescriptor("con_change.gif");
         ImageDescriptor conDeleted = SkynetActivator.getInstance().getImageDescriptor("con_delete.gif");
         ImageDescriptor conNew = SkynetActivator.getInstance().getImageDescriptor("con_new.gif");

         Image baseImage = plugin.getImage(BASE_IMAGE_STRING + ".gif");

         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + DELETE, new OverlayImage(baseImage, outDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + CHANGE, new OverlayImage(baseImage, outChange));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + NEW, new OverlayImage(baseImage, outNew));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + DELETE, new OverlayImage(baseImage, incDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + CHANGE, new OverlayImage(baseImage, incChange));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + NEW, new OverlayImage(baseImage, incNew));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + DELETE, new OverlayImage(baseImage, conDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + CHANGE, new OverlayImage(baseImage, conChange));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + NEW, new OverlayImage(baseImage, conNew));
      }
   }

   /**
    * @return Returns the attrId.
    */
   public int getAttrId() {
      return attrId;
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
