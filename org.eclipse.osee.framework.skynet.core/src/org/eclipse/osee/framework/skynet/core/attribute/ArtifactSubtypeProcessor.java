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

import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.InputStreamImageDescriptor;
import org.eclipse.osee.framework.ui.plugin.util.db.RsetProcessor;

/**
 * Define how artifact descriptors are acquired from a ResultSet and validated before being placed in a Collection. This
 * processor expects to receive ResultSet's with access to columns art_type_id, name, factory_class.
 * 
 * @author Robert A. Fisher
 */
public class ArtifactSubtypeProcessor implements RsetProcessor<ArtifactSubtypeDescriptor> {
   private static final ConfigurationPersistenceManager configManager = ConfigurationPersistenceManager.getInstance();
   private TransactionId transactionId;

   public ArtifactSubtypeProcessor(Branch branch) {
      this(TransactionIdManager.getInstance().getEditableTransactionId(branch));
   }

   public ArtifactSubtypeProcessor(TransactionId transactionId) {
      this.transactionId = transactionId;
   }

   public ArtifactSubtypeDescriptor process(ResultSet set) throws SQLException {
      IArtifactFactory factory = null;
      ArtifactSubtypeDescriptor artifactDescriptor = null;

      factory = configManager.getFactoryFromName(set.getString("factory_class"));

      artifactDescriptor =
            new ArtifactSubtypeDescriptor(set.getInt("art_type_id"), set.getString("factory_key"), factory,
                  set.getString("name"), transactionId, new InputStreamImageDescriptor(set.getBinaryStream("image")));

      return artifactDescriptor;
   }

   public boolean validate(ArtifactSubtypeDescriptor item) {
      return item != null;
   }
}
