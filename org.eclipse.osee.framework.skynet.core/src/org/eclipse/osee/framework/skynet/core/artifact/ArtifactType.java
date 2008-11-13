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
package org.eclipse.osee.framework.skynet.core.artifact;

import static org.eclipse.osee.framework.core.enums.ModificationType.ARTIFACT_DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.CHANGE;
import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.MERGED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.CONFLICTING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.INCOMING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.OUTGOING;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.revision.ConflictionType;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.swt.graphics.Image;

/**
 * Description of an Artifact subtype. The descriptor can be used to create new artifacts that are of the type of this
 * descriptor. <br/><br/> Descriptors can be acquired from the configuration manager.
 * 
 * @see org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager
 * @author Robert A. Fisher
 */
public class ArtifactType implements Serializable, Comparable<ArtifactType> {
   private static final long serialVersionUID = 1L;
   private static final ImageDescriptor favorite = SkynetActivator.getInstance().getImageDescriptor("favorite.gif");
   private static final ImageDescriptor subscribed = SkynetActivator.getInstance().getImageDescriptor("subscribed.gif");
   private static final ImageDescriptor outNew = SkynetActivator.getInstance().getImageDescriptor("out_new.gif");
   private static final ImageDescriptor outChange = SkynetActivator.getInstance().getImageDescriptor("out_change.gif");
   private static final ImageDescriptor outDeleted = SkynetActivator.getInstance().getImageDescriptor("out_delete.gif");
   private static final ImageDescriptor incNew = SkynetActivator.getInstance().getImageDescriptor("inc_new.gif");
   private static final ImageDescriptor incChange = SkynetActivator.getInstance().getImageDescriptor("inc_change.gif");
   private static final ImageDescriptor incDeleted = SkynetActivator.getInstance().getImageDescriptor("inc_delete.gif");
   private static final ImageDescriptor conChange = SkynetActivator.getInstance().getImageDescriptor("con_change.gif");
   private static final ImageDescriptor conDeleted = SkynetActivator.getInstance().getImageDescriptor("con_delete.gif");
   private static final ImageDescriptor merge = SkynetActivator.getInstance().getImageDescriptor("branch_merge.gif");
   private static final ImageDescriptor conChangeSmall =
         SkynetActivator.getInstance().getImageDescriptor("con_change_2.gif");
   private static final ImageDescriptor conDeletedSmall =
         SkynetActivator.getInstance().getImageDescriptor("con_delete_2.gif");
   private static final ImageDescriptor lockedAccess =
         SkynetActivator.getInstance().getImageDescriptor("green_lock.gif");
   private static final ImageDescriptor lockedNoAccess =
         SkynetActivator.getInstance().getImageDescriptor("red_lock.gif");
   private static final ImageDescriptor nextImageDesc =
         SkynetActivator.getInstance().getImageDescriptor("yellowN_8_8.gif");
   private static final ImageDescriptor releasedImageDesc =
         SkynetActivator.getInstance().getImageDescriptor("orangeR_8_8.gif");
   private static final String LOCKED_ACCESS = "locked access";
   private static final String LOCKED_NO_ACCESS = "locked No access";
   private static final String SUBSCRIBED = "subscribed";
   private static final String FAVORITE = "favorite";
   private static final String NEXT = "next";
   private static final String RELEASED = "released";
   private static final String WARNING = "warning";
   private static final String ERROR = "error";
   private static final String BASE = "base";
   private final int artTypeId;
   private final String factoryKey;
   private final ArtifactFactory factory;
   private String name;
   private String namespace;
   transient private ImageRegistry imageRegistry;
   transient private ImageDescriptor imageDescriptor;

   ArtifactType(int artTypeId, String factoryKey, ArtifactFactory factory, String namespace, String name, ImageDescriptor imageDescriptor) {
      this.artTypeId = artTypeId;
      this.factory = factory;
      this.name = name;
      this.factoryKey = factoryKey == null ? "" : factoryKey;
      this.namespace = namespace == null ? "" : namespace;
      this.imageDescriptor = imageDescriptor;
      this.imageRegistry = null;
      ArtifactTypeManager.cache(this);
   }

   /**
    * Get a new instance of the type of artifact described by this descriptor. This is just a convenience method that
    * calls makeNewArtifact on the known factory with this descriptor for the descriptor parameter, and the supplied
    * branch.
    * 
    * @return Return artifact reference
    * @throws OseeCoreException
    * @see ArtifactFactory#makeNewArtifact(Branch, ArtifactType)
    * @use {@link ArtifactTypeManager}.addArtifact
    */
   public Artifact makeNewArtifact(Branch branch) throws OseeCoreException {
      return factory.makeNewArtifact(branch, this, null, null, null);
   }

   /**
    * Get a new instance of the type of artifact described by this descriptor. This is just a convenience method that
    * calls makeNewArtifact on the known factory with this descriptor for the descriptor parameter, and the supplied
    * branch.
    * 
    * @param branch branch on which artifact will be created
    * @return Return artifact reference
    * @throws OseeCoreException
    * @see ArtifactFactory#makeNewArtifact(Branch, ArtifactType, String, String, ArtifactProcessor)
    * @use {@link ArtifactTypeManager}.addArtifact
    */
   public Artifact makeNewArtifact(Branch branch, String guid, String humandReadableId) throws OseeCoreException {
      return factory.makeNewArtifact(branch, this, guid, humandReadableId, null);
   }

   /**
    * @return Returns the artTypeId.
    */
   public int getArtTypeId() {
      return artTypeId;
   }

   /**
    * @return Returns the factory.
    */
   public ArtifactFactory getFactory() {
      return factory;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * determines if this artifact type is equal to or a subclass of the artifact type referenced by artifactTypeName
    * 
    * @return
    */
   public boolean isTypeCompatible(String artifactTypeName) {
      return name.equals(artifactTypeName);
   }

   /**
    * @return Returns the factoryKey.
    */
   public String getFactoryKey() {
      return factoryKey;
   }

   /**
    * Determine if this is the descriptor that produces the same type of artifact as an already existing artifact.
    * 
    * @param artifact The artifact to compare against.
    * @return <b>true</b> if and only if this descriptor will give you the same type of artifact.
    */
   public boolean canProduceArtifact(Artifact artifact) {
      return artifact.getArtTypeId() == artTypeId && artifact.getFactory().getFactoryId() == factory.getFactoryId();
   }

   public Image getImage() {
      checkImageRegistry();
      return imageRegistry.get(BASE);
   }

   public Image getLockedImage(boolean access) {
      checkImageRegistry();
      return imageRegistry.get(BASE + (access ? LOCKED_ACCESS : LOCKED_NO_ACCESS));
   }

   public Image getImage(ChangeType changeType, ModificationType modType) {
      if (changeType == null) throw new IllegalArgumentException("changeType can not be null.");
      if (modType == null) throw new IllegalArgumentException("modType can not be null.");
      if (changeType == CONFLICTING && modType == NEW) {
         return imageRegistry.get(BASE + changeType + ModificationType.CHANGE);
      }

      checkImageRegistry();
      return imageRegistry.get(BASE + changeType + modType);
   }

   public Image getImage(ConflictionType conType) {
      if (conType == null) throw new IllegalArgumentException("conType can not be null.");
      checkImageRegistry();
      return imageRegistry.get(BASE + conType);

   }

   public Image getImage(boolean isSubscribed, boolean isFavorite, ArtifactAnnotation.Type notifyType) {
      checkImageRegistry();
      String hashKey =
            BASE + (isSubscribed ? SUBSCRIBED : "") + (isFavorite ? FAVORITE : "") + ((notifyType == null || notifyType == ArtifactAnnotation.Type.None) ? "" : (notifyType == ArtifactAnnotation.Type.Error ? ERROR : WARNING));
      Image image = imageRegistry.get(hashKey);
      if (image == null) {

         ImageDescriptor imageDesc = imageRegistry.getDescriptor(BASE);
         if (isSubscribed) imageDesc = new OverlayImage(imageDesc.createImage(), subscribed, 8, 6);
         if (isFavorite) imageDesc = new OverlayImage(imageDesc.createImage(), favorite, 7, 0);
         if (notifyType == ArtifactAnnotation.Type.Error)
            imageDesc =
                  new OverlayImage(imageDesc.createImage(), ArtifactAnnotation.Type.Error.getImageOverlayDescriptor(),
                        0, 8);
         else if (notifyType == ArtifactAnnotation.Type.Warning) imageDesc =
               new OverlayImage(imageDesc.createImage(), ArtifactAnnotation.Type.Warning.getImageOverlayDescriptor(),
                     0, 8);
         imageRegistry.put(hashKey, imageDesc);
         image = imageRegistry.get(hashKey);
      }
      return image;
   }

   public Image getImage(boolean next, boolean released) {
      checkImageRegistry();
      String hashKey = BASE + (next ? NEXT : "") + (released ? RELEASED : "");
      Image image = imageRegistry.get(hashKey);
      if (image == null) {
         ImageDescriptor imageDesc = imageRegistry.getDescriptor(BASE);
         if (next) imageDesc = new OverlayImage(imageDesc.createImage(), nextImageDesc, 8, 8);
         if (released) imageDesc = new OverlayImage(imageDesc.createImage(), releasedImageDesc, 8, 0);
         imageRegistry.put(hashKey, imageDesc);
         image = imageRegistry.get(hashKey);
      }
      return image;
   }

   public Image getAnnotationImage(ArtifactAnnotation.Type type) {
      checkImageRegistry();
      if (type == ArtifactAnnotation.Type.Error)
         return imageRegistry.get(BASE + ERROR);
      else if (type == ArtifactAnnotation.Type.Warning) return imageRegistry.get(BASE + WARNING);
      return getImage();
   }

   private synchronized void checkImageRegistry() {
      if (imageRegistry == null) {
         imageRegistry = new ImageRegistry();

         imageRegistry.put(BASE, imageDescriptor);
         imageRegistry.put(BASE + LOCKED_NO_ACCESS, new OverlayImage(imageRegistry.get(BASE), lockedNoAccess, 0, 7));
         imageRegistry.put(BASE + LOCKED_ACCESS, new OverlayImage(imageRegistry.get(BASE), lockedAccess, 0, 7));
         imageRegistry.put(BASE + INCOMING + DELETED, new OverlayImage(imageRegistry.get(BASE), incDeleted));
         imageRegistry.put(BASE + INCOMING + CHANGE, new OverlayImage(imageRegistry.get(BASE), incChange));
         imageRegistry.put(BASE + INCOMING + NEW, new OverlayImage(imageRegistry.get(BASE), incNew));
         imageRegistry.put(BASE + OUTGOING + DELETED, new OverlayImage(imageRegistry.get(BASE), outDeleted));
         imageRegistry.put(BASE + OUTGOING + ARTIFACT_DELETED, new OverlayImage(imageRegistry.get(BASE), outDeleted));
         imageRegistry.put(BASE + OUTGOING + CHANGE, new OverlayImage(imageRegistry.get(BASE), outChange));
         imageRegistry.put(BASE + OUTGOING + MERGED, new OverlayImage(imageRegistry.get(BASE), merge));
         imageRegistry.put(BASE + OUTGOING + NEW, new OverlayImage(imageRegistry.get(BASE), outNew));
         imageRegistry.put(BASE + CONFLICTING + DELETED, new OverlayImage(imageRegistry.get(BASE), conDeleted));
         imageRegistry.put(BASE + CONFLICTING + CHANGE, new OverlayImage(imageRegistry.get(BASE), conChange));
         imageRegistry.put(BASE + CONFLICTING + DELETED + "Small", new OverlayImage(imageRegistry.get(BASE),
               conDeletedSmall));
         imageRegistry.put(BASE + CONFLICTING + CHANGE + "Small", new OverlayImage(imageRegistry.get(BASE),
               conChangeSmall));
         imageRegistry.put(BASE + WARNING, new OverlayImage(imageRegistry.get(BASE),
               ArtifactAnnotation.Type.Warning.getImageOverlayDescriptor(), 0, 8));
         imageRegistry.put(BASE + ERROR, new OverlayImage(imageRegistry.get(BASE),
               ArtifactAnnotation.Type.Error.getImageOverlayDescriptor(), 0, 8));
      }
   }

   /**
    * @return Returns the imageDescriptor.
    */
   public ImageDescriptor getImageDescriptor() {
      return imageDescriptor;
   }

   public String toString() {
      return name;
   }

   /**
    * Only store the identifying data since a manager controls this object.
    * 
    * @param stream
    * @throws IOException
    */
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.writeObject(name);
   }

   /**
    * Initialize as a dumb object for portraying the object needed
    * 
    * @param stream
    * @throws IOException
    * @throws ClassNotFoundException
    */
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      name = (String) stream.readObject();
   }

   /**
    * @return The ArtifactSubtypeDescriptor associated with the name and transactionId from this dumb object
    * @throws ObjectStreamException
    */
   private Object readResolve() throws ObjectStreamException {
      try {
         return ArtifactTypeManager.getType(name);
      } catch (OseeCoreException e) {
         throw new RuntimeException("Error while resolving descriptor", e);
      }
   }

   /**
    * @param imageDescriptor the imageDescriptor to set
    */
   public void setImageDescriptor(ImageDescriptor imageDescriptor) {
      this.imageDescriptor = imageDescriptor;
      // Clear out the image cache so it will be re-created
      if (imageRegistry != null) imageRegistry = null;
   }

   /**
    * @return the namespace
    */
   public String getNamespace() {
      return namespace;
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(ArtifactType artifactType) {
      if (artifactType == null) {
         return -1;
      }
      return name.compareTo(artifactType.name);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + name.hashCode();
      result = prime * result + namespace.hashCode();
      return result;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      final ArtifactType other = (ArtifactType) obj;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (namespace == null) {
         if (other.namespace != null) return false;
      } else if (!namespace.equals(other.namespace)) return false;
      return true;
   }
}