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

package org.eclipse.osee.framework.skynet.core.transactionChange;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class TransactionArtifactChange extends TransactionChange {
   private int artId;
   private int sourceGammaId;
   private int destGammaId;
   private List<TransactionAttributeChange> attributeChanges;
   private Artifact artifact;
   private ArtifactSubtypeDescriptor artifactSubtypeDescriptor;

   /**
    * @return the artifactSubtypeDescriptor
    */
   public ArtifactSubtypeDescriptor getArtifactSubtypeDescriptor() {
      return artifactSubtypeDescriptor;
   }

   /**
    * @param modType
    * @param changeType
    * @param toTransactionId
    * @param fromTransactionId
    * @param artId
    * @param sourceGammaId
    * @param destGammaId
    */
   public TransactionArtifactChange(ModificationType modType, ChangeType changeType, TransactionId toTransactionId, TransactionId fromTransactionId, int artId, int sourceGammaId, int destGammaId, ArtifactSubtypeDescriptor artifactSubtypeDescriptor) {
      super(modType, changeType, toTransactionId, fromTransactionId);
      this.artId = artId;
      this.sourceGammaId = sourceGammaId;
      this.destGammaId = destGammaId;
      this.artifactSubtypeDescriptor = artifactSubtypeDescriptor;
      this.attributeChanges = new LinkedList<TransactionAttributeChange>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transactionChange.TransactionChange#getImage()
    */
   @Override
   public Image getImage() {
      return artifactSubtypeDescriptor.getImage(getChangeType(), getModificationType());
   }

   /**
    * @return the artId
    */
   public int getArtId() {
      return artId;
   }

   /**
    * @return the sourceGammaId
    */
   public int getSourceGammaId() {
      return sourceGammaId;
   }

   /**
    * @return the destGammaId
    */
   public int getDestGammaId() {
      return destGammaId;
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null)
         throw new IllegalArgumentException("adapter can not be null");
      else
         try {
            if (adapter.isInstance(getArtifact())) {
               return getArtifact();
            }

            else if (adapter.isInstance(this)) {
               return this;
            }
         } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
      return null;
   }

   public void addAttributeChange(TransactionAttributeChange transactionAttributeChange) {
      attributeChanges.add(transactionAttributeChange);
   }

   /**
    * @return the attributeChanges
    */
   public List<TransactionAttributeChange> getAttributeChanges() {
      return attributeChanges;
   }

   /**
    * @return the artifact
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public Artifact getArtifact() throws IllegalArgumentException, SQLException {
      if (artifact == null) {
         artifact = ArtifactPersistenceManager.getInstance().getArtifactFromId(artId, getToTransactionId());
      }
      return artifact;
   }
}
