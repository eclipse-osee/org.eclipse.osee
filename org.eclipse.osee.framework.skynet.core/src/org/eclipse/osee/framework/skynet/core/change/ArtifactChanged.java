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

package org.eclipse.osee.framework.skynet.core.change;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactChanged extends Change {

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactChanged.class);
   private ArtifactType artifactSubtypeDescriptor;
   private ArtifactChange artifactChange;

   /**
    * @param artTypeId
    * @param artName
    * @param sourceGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param modType
    * @param changeType
    */
   public ArtifactChanged(Branch branch, int artTypeId, int sourceGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType) {
      super(branch, artTypeId, sourceGamma, artId, toTransactionId, fromTransactionId, modType, changeType);
   }

   /**
    * @return the dynamicAttributeDescriptor
    */
   private ArtifactType getDynamicArtifactSubtypeDescriptor() throws SQLException {
      if (artifactSubtypeDescriptor == null) {
         artifactSubtypeDescriptor = ArtifactTypeManager.getType(artTypeId);
      }
      return artifactSubtypeDescriptor;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getImage()
    */
   @Override
   public Image getItemTypeImage() {
      Image image = null;

      try {
         image = getItemKindImage();
      } catch (IllegalArgumentException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return image;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getName()
    */
   @Override
   public String getName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
      return getArtifactName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getTypeName()
    */
   @Override
   public String getItemTypeName() throws SQLException {
      return getDynamicArtifactSubtypeDescriptor().getName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getValue()
    */
   @Override
   public String getIsValue() {
      return "";
   }

   private ArtifactChange getArtifactChange() throws SQLException, IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist {
      if (artifactChange == null) {
         artifactChange =
               new ArtifactChange(getChangeType(), getModificationType(), 
                      getArtifact(), null, null, getFromTransactionId(),
                     getFromTransactionId(), getToTransactionId(), getGamma());
      }
      return artifactChange;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      try {
         // this is a temporary fix until the old change report goes away.
         if (adapter.isInstance(getArtifactChange())) {
            return getArtifactChange();
         }
         if (adapter.isInstance(getArtifact())) {
            return getArtifact();
         }
      } catch (IllegalArgumentException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (ArtifactDoesNotExist ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (MultipleArtifactsExist ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getObjectImage()
    */
   @Override
   public Image getItemKindImage() throws IllegalArgumentException, SQLException {
      return ArtifactTypeManager.getType(artTypeId).getImage(getChangeType(), getModificationType());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getItemKind()
    */
   @Override
   public String getItemKind() {
      return "Artifact";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getWasValue()
    */
   @Override
   public String getWasValue() {
      return null;
   }

}
