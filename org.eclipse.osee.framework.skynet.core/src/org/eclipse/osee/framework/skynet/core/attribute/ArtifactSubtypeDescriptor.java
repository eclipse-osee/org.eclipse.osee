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
package org.eclipse.osee.framework.skynet.core.attribute;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType.CONFLICTING;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType.INCOMING;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType.OUTGOING;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.CHANGE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.DELETE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.NEW;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.SQLException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.revision.ConflictionType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.InputStreamImageDescriptor;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;
import org.eclipse.swt.graphics.Image;

/**
 * Description of an Artifact subtype. The descriptor can be used to create new artifacts that are of the type of this
 * descriptor. <br/><br/> Descriptors can be acquired from the configuration manager.
 * 
 * @see org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager
 * @author Robert A. Fisher
 */
public class ArtifactSubtypeDescriptor implements Serializable {
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
   private static final ImageDescriptor lockedAccess =
         SkynetActivator.getInstance().getImageDescriptor("green_lock.gif");
   private static final ImageDescriptor lockedNoAccess =
         SkynetActivator.getInstance().getImageDescriptor("red_lock.gif");
   private static final ImageDescriptor nextImageDesc =
         SkynetActivator.getInstance().getImageDescriptor("yellowN_8_8.gif");
   private static final ImageDescriptor releasedImageDesc =
         SkynetActivator.getInstance().getImageDescriptor("orangeR_8_8.gif");
   private static final ImageDescriptor metricsFromTasks =
         SkynetActivator.getInstance().getImageDescriptor("yellowT_8_8.gif");
   private static final String LOCKED_ACCESS = "locked access";
   private static final String LOCKED_NO_ACCESS = "locked No access";
   private static final String SUBSCRIBED = "subscribed";
   private static final String FAVORITE = "favorite";
   private static final String NEXT = "next";
   private static final String RELEASED = "released";
   private static final String WARNING = "warning";
   private static final String ERROR = "error";
   private static final String METRICS_FROM_TASKS = "metricsFromTasks";
   private static final String BASE = "base";
   private final int artTypeId;
   private final String factoryKey;
   private final IArtifactFactory factory;
   private String name;
   private TransactionId transactionId;
   transient private ImageRegistry imageRegistry;
   transient private InputStreamImageDescriptor imageDescriptor;

   /**
    * @param artTypeId
    * @param factory
    * @param name
    */
   protected ArtifactSubtypeDescriptor(int artTypeId, String factoryKey, IArtifactFactory factory, String name, TransactionId transactionId, InputStreamImageDescriptor imageDescriptor) {
      this.artTypeId = artTypeId;
      this.factory = factory;
      this.name = name;
      this.factoryKey = factoryKey;
      this.transactionId = transactionId;
      this.imageDescriptor = imageDescriptor;
      this.imageRegistry = null;
   }

   //   /**
   //    * Constructor for deserialization
   //    */
   //   private ArtifactSubtypeDescriptor() {
   //      this.artTypeId = 0;
   //      this.factoryKey = null;
   //      this.factory = null;
   //      this.imageDescriptor = null;
   //      this.name = null;
   //      this.transactionId = null;
   //   }

   /**
    * Get a new instance of the type of artifact described by this descriptor. This is just a convenience method that
    * calls makeNewArtifact on the known factory with this descriptor for the descriptor parameter, and the supplied
    * branch.
    * 
    * @return Return artifact reference
    * @throws SQLException
    * @see IArtifactFactory#makeNewArtifact(ArtifactSubtypeDescriptor)
    */
   public Artifact makeNewArtifact() throws SQLException {
      return factory.makeNewArtifact(this);
   }

   /**
    * Get a new instance of the type of artifact described by this descriptor. This is just a convenience method that
    * calls makeNewArtifact on the known factory with this descriptor for the descriptor parameter, and the supplied
    * branch.
    * 
    * @return Return artifact reference
    * @throws SQLException
    * @see IArtifactFactory#makeNewArtifact(ArtifactSubtypeDescriptor, String, String)
    */
   public Artifact makeNewArtifact(String guid, String humandReadableId) throws SQLException {
      return factory.makeNewArtifact(this, guid, humandReadableId);
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
   public IArtifactFactory getFactory() {
      return factory;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * @return Returns the factoryKey.
    */
   public String getFactoryKey() {
      return factoryKey;
   }

   /**
    * @return Returns the transactionId.
    */
   public TransactionId getTransactionId() {
      return transactionId;
   }

   /**
    * Determine if this is the descriptor that produces the same type of artifact as an already existing artifact.
    * 
    * @param artifact The artifact to compare against.
    * @return <b>true</b> if and only if this descriptor will give you the same type of artifact.
    */
   public boolean canProduceArtifact(Artifact artifact) {
      return artifact.getArtTypeId() == artTypeId && artifact.getFactoryId() == factory.getFactoryId();
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
      if (changeType == CONFLICTING && modType == NEW) throw new IllegalArgumentException(
            "conflicting new artifacts are not supported");

      checkImageRegistry();
      return imageRegistry.get(BASE + changeType + modType);
   }

   public Image getImage(ConflictionType conType) {
      if (conType == null) throw new IllegalArgumentException("conType can not be null.");
      checkImageRegistry();
      return imageRegistry.get(BASE + conType);

   }

   public Image getImage(boolean isSubscribed, boolean isFavorite, ArtifactAnnotation.Type notifyType, boolean isMetricsFromTasks) {
      checkImageRegistry();
      String hashKey =
            BASE + (isSubscribed ? SUBSCRIBED : "") + (isFavorite ? FAVORITE : "") + ((notifyType == null || notifyType == ArtifactAnnotation.Type.None) ? "" : (notifyType == ArtifactAnnotation.Type.Error ? ERROR : WARNING) + (isMetricsFromTasks ? METRICS_FROM_TASKS : ""));
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
         if (isMetricsFromTasks) imageDesc = new OverlayImage(imageDesc.createImage(), metricsFromTasks, 0, 0);
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

   private void checkImageRegistry() {
      if (imageRegistry == null) {
         imageRegistry = new ImageRegistry();

         imageRegistry.put(BASE, imageDescriptor);
         imageRegistry.put(BASE + LOCKED_NO_ACCESS, new OverlayImage(imageRegistry.get(BASE), lockedNoAccess, 0, 7));
         imageRegistry.put(BASE + LOCKED_ACCESS, new OverlayImage(imageRegistry.get(BASE), lockedAccess, 0, 7));
         imageRegistry.put(BASE + INCOMING + DELETE, new OverlayImage(imageRegistry.get(BASE), incDeleted));
         imageRegistry.put(BASE + INCOMING + CHANGE, new OverlayImage(imageRegistry.get(BASE), incChange));
         imageRegistry.put(BASE + INCOMING + NEW, new OverlayImage(imageRegistry.get(BASE), incNew));
         imageRegistry.put(BASE + OUTGOING + DELETE, new OverlayImage(imageRegistry.get(BASE), outDeleted));
         imageRegistry.put(BASE + OUTGOING + CHANGE, new OverlayImage(imageRegistry.get(BASE), outChange));
         imageRegistry.put(BASE + OUTGOING + NEW, new OverlayImage(imageRegistry.get(BASE), outNew));
         imageRegistry.put(BASE + CONFLICTING + DELETE, new OverlayImage(imageRegistry.get(BASE), conDeleted));
         imageRegistry.put(BASE + CONFLICTING + CHANGE, new OverlayImage(imageRegistry.get(BASE), conChange));
         imageRegistry.put(BASE + WARNING, new OverlayImage(imageRegistry.get(BASE),
               ArtifactAnnotation.Type.Warning.getImageOverlayDescriptor(), 0, 8));
         imageRegistry.put(BASE + ERROR, new OverlayImage(imageRegistry.get(BASE),
               ArtifactAnnotation.Type.Error.getImageOverlayDescriptor(), 0, 8));
      }
   }

   /**
    * @return Returns the imageDescriptor.
    */
   public InputStreamImageDescriptor getImageDescriptor() {
      return imageDescriptor;
   }

   public String toString() {
      return name;
   }

   /**
    * @throws SQLException
    */
   public void delete() throws SQLException {
      // delete existing artifact of this type
      // for (Artifact artifact : artifactManager.getArtifactsFromSubtypeName(name,
      // transactionId.getBranch())) {
      // artifact.delete();
      // }
      // then delete the artifact type itself
      // OSEE_DEFINE_ARTIFACT_TYPE needs a MODIFICATION_ID column to support deleting artifact types
   }

   /**
    * Only store the identifying data since a manager controls this object.
    * 
    * @param stream
    * @throws IOException
    */
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.writeObject(name);
      stream.writeObject(transactionId);
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
      transactionId = (TransactionId) stream.readObject();
   }

   /**
    * @return The ArtifactSubtypeDescriptor associated with the name and transactionId from this dumb object
    * @throws ObjectStreamException
    */
   private Object readResolve() throws ObjectStreamException {
      try {
         return ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(name, transactionId);
      } catch (SQLException e) {
         throw new RuntimeException("Error while resolving descriptor", e);
      }
   }

   /**
    * @param imageDescriptor the imageDescriptor to set
    */
   public void setImageDescriptor(InputStreamImageDescriptor imageDescriptor) {
      this.imageDescriptor = imageDescriptor;
      // Clear out the image cache so it will be re-created
      if (imageRegistry != null) imageRegistry = null;
   }

}
