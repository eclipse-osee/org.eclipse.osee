/*
 * Created on Feb 21, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.conflict;

import java.sql.SQLException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.swt.graphics.Image;


/**
 * @author Jeff C. Phillips 
 *
 */
public abstract class Conflict implements IAdaptable {

	private int sourceGamma;
	private int destGamma;
	private int artId;
	private TransactionId toTransactionId;
	private TransactionId fromTransactionId;
	private Artifact artifact;
	private TransactionType transactionType;
	private ChangeType changeType;
	private Branch mergeBranch;


	/**
	 * @param sourceGamma
	 * @param destGamma
	 * @param artId
	 * @param toTransactionId
	 * @param fromTransactionId
	 * @param transactionType
	 * @param changeType
	 */
	public Conflict(int sourceGamma, int destGamma, int artId,
			TransactionId toTransactionId, TransactionId fromTransactionId,
			TransactionType transactionType, ChangeType changeType, Branch mergeBranch) {
		super();
		this.sourceGamma = sourceGamma;
		this.destGamma = destGamma;
		this.artId = artId;
		this.toTransactionId = toTransactionId;
		this.fromTransactionId = fromTransactionId;
		this.transactionType = transactionType;
		this.changeType = changeType;
		this.mergeBranch = mergeBranch;
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
	         artifact = ArtifactPersistenceManager.getInstance().getArtifactFromId(artId, mergeBranch);
	      }
	      return artifact;
	   }

	/**
	 * @return the sourceGamma
	 */
	public int getSourceGamma() {
		return sourceGamma;
	}

	/**
	 * @param sourceGamma the sourceGamma to set
	 */
	public void setSourceGamma(int sourceGamma) {
		this.sourceGamma = sourceGamma;
	}

	/**
	 * @return the destGamma
	 */
	public int getDestGamma() {
		return destGamma;
	}

	/**
	 * @param destGamma the destGamma to set
	 */
	public void setDestGamma(int destGamma) {
		this.destGamma = destGamma;
	}

	/**
	 * @return the artId
	 */
	public int getArtId() {
		return artId;
	}

	/**
	 * @param artId the artId to set
	 */
	public void setArtId(int artId) {
		this.artId = artId;
	}

	/**
	 * @return the toTransactionId
	 */
	public TransactionId getToTransactionId() {
		return toTransactionId;
	}

	/**
	 * @param toTransactionId the toTransactionId to set
	 */
	public void setToTransactionId(TransactionId toTransactionId) {
		this.toTransactionId = toTransactionId;
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
		return getArtifact().getDescriptor().getImage(getChangeType(), TransactionType.convertTransactionTypeToModificationType (getTransactionType()));
	}
	
	public abstract Image getImage();
	public abstract String getSourceDisplayData();
	public abstract String getDestDisplayData();
}
