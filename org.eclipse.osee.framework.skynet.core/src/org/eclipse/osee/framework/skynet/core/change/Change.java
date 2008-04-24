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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public abstract class Change implements IAdaptable {

   private int sourceGamma;
   private int artId;
   private TransactionId toTransactionId;
   private TransactionId fromTransactionId;
   private Artifact artifact;
   private TransactionType transactionType;
   private ChangeType changeType;
   private String artName;
   protected int artTypeId;

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param transactionType
    * @param changeType
    */
   public Change(int artTypeId, String artName, int sourceGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, TransactionType transactionType, ChangeType changeType) {
      super();
      this.sourceGamma = sourceGamma;
      this.artId = artId;
      this.toTransactionId = toTransactionId;
      this.fromTransactionId = fromTransactionId;
      this.transactionType = transactionType;
      this.changeType = changeType;
      this.artName = artName;
      this.artTypeId = artTypeId;
   }

   /**
    * @return the transactionType
    */
   public TransactionType getTransactionType() {
      return transactionType;
   }

   /**
    * @return the changeType
    */
   public ChangeType getChangeType() {
      return changeType;
   }

   /**
    * @return the artifact
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public Artifact getArtifact() throws IllegalArgumentException, SQLException {
      if (artifact == null) {
         artifact = ArtifactPersistenceManager.getInstance().getArtifactFromId(artId, toTransactionId);
      }
      return artifact;
   }

   public String getArtifactName() {
      return artName;
   }

   /**
    * @return the sourceGamma
    */
   public int getGamma() {
      return sourceGamma;
   }

   /**
    * @return the artId
    */
   public int getArtId() {
      return artId;
   }

   /**
    * @return the toTransactionId
    */
   public TransactionId getToTransactionId() {
      return toTransactionId;
   }

   /**
    * @return the fromTransactionId
    */
   public TransactionId getFromTransactionId() {
      return fromTransactionId;
   }

   /**
    * @param fromTransactionId the fromTransactionId to set
    */
   public void setFromTransactionId(TransactionId fromTransactionId) {
      this.fromTransactionId = fromTransactionId;
   }

   public abstract Image getItemKindImage() throws IllegalArgumentException, SQLException;

   public abstract Image getItemTypeImage();

   public abstract String getValue();

   public abstract String getItemTypeName() throws SQLException;

   public abstract String getName();

   public abstract String getItemKind();

   /**
    * @param artName the artName to set
    */
   public void setArtName(String artName) {
      this.artName = artName;
   }

   /**
    * @return the artTypeId
    */
   public int getArtTypeId() {
      return artTypeId;
   }
}
