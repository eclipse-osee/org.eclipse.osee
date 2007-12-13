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

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;

/**
 * Caches artifact subtype descriptors.
 * 
 * @see org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor
 * @author Robert A. Fisher
 */
public class ArtifactSubtypeDescriptorCache {
	private static final TransactionIdManager transactionIdManager = TransactionIdManager
			.getInstance();
	private final HashCollection<TransactionId, ArtifactSubtypeDescriptor> allDescriptors;
	private final DoubleKeyHashMap<String, TransactionId, ArtifactSubtypeDescriptor> nameToDescriptors;
	private final DoubleKeyHashMap<Integer, TransactionId, ArtifactSubtypeDescriptor> idToDescriptors;

	private static final String SELECT_ARTIFACT_TYPES = "SELECT * FROM osee_define_artifact_type aty1, osee_define_factory fac2, osee_define_txs txs3 WHERE aty1.factory_id = fac2.factory_id AND aty1.gamma_id = txs3.gamma_id AND txs3.transaction_id = (SELECT MAX(txs4.transaction_id) FROM osee_define_txs txs4, osee_define_tx_details txd5 WHERE txs4.gamma_id = aty1.gamma_id AND txs4.transaction_id <= ? AND txs4.transaction_id = txd5.transaction_id AND txd5.branch_id = ?) ORDER BY aty1.name";

	protected ArtifactSubtypeDescriptorCache() {
		this.allDescriptors = new HashCollection<TransactionId, ArtifactSubtypeDescriptor>(
				false, LinkedHashSet.class);
		this.nameToDescriptors = new DoubleKeyHashMap<String, TransactionId, ArtifactSubtypeDescriptor>();
		this.idToDescriptors = new DoubleKeyHashMap<Integer, TransactionId, ArtifactSubtypeDescriptor>();
	}

	private synchronized void checkPopulated(TransactionId transactionId)
			throws SQLException {
		if (!allDescriptors.containsKey(transactionId)) {
			populateCache(transactionId);
		}
	}

	private void populateCache(TransactionId transactionId) throws SQLException {
		Collection<ArtifactSubtypeDescriptor> descriptors = new LinkedList<ArtifactSubtypeDescriptor>();
		Query.acquireCollection(descriptors, new ArtifactSubtypeProcessor(
				transactionId), SELECT_ARTIFACT_TYPES, SQL3DataType.INTEGER,
				transactionId.getTransactionNumber(), SQL3DataType.INTEGER,
				transactionId.getBranch().getBranchId());

		for (ArtifactSubtypeDescriptor descriptor : descriptors) {
			cache(descriptor);
		}
	}

	/**
	 * @return Returns all of the descriptors.
	 * @throws SQLException
	 */
	public Collection<ArtifactSubtypeDescriptor> getAllDescriptors(Branch branch)
			throws SQLException {
		TransactionId transactionId = transactionIdManager
				.getEditableTransactionId(branch);

		return getAllDescriptors(transactionId);
	}

	/**
	 * @return Returns the descriptor with a particular name, null if it does
	 *         not exist.
	 * @throws SQLException
	 */
	public ArtifactSubtypeDescriptor getDescriptor(String name, Branch branch)
			throws SQLException {
		return getDescriptor(name, transactionIdManager
				.getEditableTransactionId(branch));
	}

	/**
	 * @return Returns the descriptor with a particular name, null if it does
	 *         not exist.
	 * @throws SQLException
	 */
	public ArtifactSubtypeDescriptor getDescriptor(int artTypeId, Branch branch)
			throws SQLException {
		TransactionId transactionId = transactionIdManager
				.getEditableTransactionId(branch);

		return getDescriptor(artTypeId, transactionId);
	}

	/**
	 * @return Returns all of the descriptors.
	 * @throws SQLException
	 */
	public Set<ArtifactSubtypeDescriptor> getAllDescriptors(
			TransactionId transactionId) throws SQLException {
		checkPopulated(transactionId);
		return (Set<ArtifactSubtypeDescriptor>) allDescriptors
				.getValues(transactionId);
	}

	/**
	 * @return Returns the descriptor with a particular name, null if it does
	 *         not exist.
	 * @throws SQLException
	 */
	public ArtifactSubtypeDescriptor getDescriptor(String name,
			TransactionId transactionId) throws SQLException {
		checkPopulated(transactionId);
		return nameToDescriptors.get(name, transactionId);
	}

	/**
	 * @return Returns the descriptor with a particular name, null if it does
	 *         not exist.
	 * @throws SQLException
	 */
	public ArtifactSubtypeDescriptor getDescriptor(int artTypeId,
			TransactionId transactionId) throws SQLException {
		checkPopulated(transactionId);

		ArtifactSubtypeDescriptor artifactSubtypeDescriptor = idToDescriptors
				.get(artTypeId, transactionId);

		if (artifactSubtypeDescriptor == null) {
			throw new IllegalArgumentException("Atrifact type: " + artTypeId
					+ " is not available for transaction: " + transactionId);
		}
		return artifactSubtypeDescriptor;
	}

	/**
	 * Cache a newly created descriptor.
	 * 
	 * @param descriptor
	 *            The descriptor to cache
	 * @throws IllegalArgumentException
	 *             if descriptor is null.
	 */
	public void cache(ArtifactSubtypeDescriptor descriptor) {
		if (descriptor == null) {
			throw new IllegalArgumentException(
					"The descriptor parameter can not be null");
		}

		if (nameToDescriptors.containsKey(descriptor.getName(), descriptor
				.getTransactionId())) {
			System.out.println("bad");
		}

		allDescriptors.put(descriptor.getTransactionId(), descriptor);
		nameToDescriptors.put(descriptor.getName(), descriptor
				.getTransactionId(), descriptor);
		idToDescriptors.put(descriptor.getArtTypeId(), descriptor
				.getTransactionId(), descriptor);
	}
}