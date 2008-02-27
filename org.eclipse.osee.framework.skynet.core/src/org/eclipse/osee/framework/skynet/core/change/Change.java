/*
 * Created on Feb 27, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.change;

import java.sql.SQLException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 *
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
	private int artTypeId;


	/**
	 * @param sourceGamma
	 * @param destGamma
	 * @param artId
	 * @param toTransactionId
	 * @param fromTransactionId
	 * @param transactionType
	 * @param changeType
	 */
	public Change(int artTypeId, String artName, int sourceGamma, int artId,
			TransactionId toTransactionId, TransactionId fromTransactionId,
			TransactionType transactionType, ChangeType changeType) {
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
	   
	   public String getArtifactName(){
		   return artName;
	}

	/**
	 * @return the sourceGamma
	 */
	public int getSourceGamma() {
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
	
	public Image getArtifactImage() throws IllegalArgumentException, SQLException{
		return ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(artTypeId).getImage(getChangeType(), TransactionType.convertTransactionTypeToModificationType (getTransactionType()));
	}
	
	public abstract Image getImage();
	public abstract String getSourceDisplayData();
}
