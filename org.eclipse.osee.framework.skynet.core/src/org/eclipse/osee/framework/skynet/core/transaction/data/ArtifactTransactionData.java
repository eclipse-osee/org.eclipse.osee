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
package org.eclipse.osee.framework.skynet.core.transaction.data;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactTransactionData implements ITransactionData {
   private static final String INSERT_INTO_ARTIFACT_VERSION_TABLE =
         "INSERT INTO " + ARTIFACT_VERSION_TABLE + "(art_id, gamma_id, modification_id) VALUES (?,?,?)";
   private static final int PRIME_NUMBER = 3;

   private List<Object> dataItems;
   private int gammaId;
   private int transactionId;
   private ModificationType modificationType;
   private Artifact artifact;

   public ArtifactTransactionData(Artifact artifact, int gammaId, int transactionId, ModificationType modificationType) {
      super();
      this.gammaId = gammaId;
      this.transactionId = transactionId;
      this.modificationType = modificationType;
      this.artifact = artifact;
      this.dataItems = new LinkedList<Object>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.TransactionData#getTransactionChangeSql()
    */
   public String getTransactionChangeSql() {
      return INSERT_INTO_ARTIFACT_VERSION_TABLE;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.transaction.TransactionData#getTransactionChangeData()
    */
   public List<Object> getTransactionChangeData() {
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(artifact.getArtId());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(gammaId);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(modificationType.getValue());

      return dataItems;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof ArtifactTransactionData) {
         return ((ArtifactTransactionData) obj).artifact.getArtId() == artifact.getArtId();
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return artifact.getArtId() * PRIME_NUMBER;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#getGammaId()
    */
   public int getGammaId() {
      return gammaId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#getTransactionId()
    */
   public int getTransactionId() {
      return transactionId;
   }

   /**
    * @return Returns the modificationType.
    */
   public ModificationType getModificationType() {
      return modificationType;
   }

   /**
    * @param modificationType The modificationType to set.
    */
   public void setModificationType(ModificationType modificationType) {
      this.modificationType = modificationType;
   }

   /**
    * @return the artifact
    */
   public Artifact getArtifact() {
      return artifact;
   }
}
