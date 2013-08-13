/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction.handler;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.OrcsVisitorAdapter;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactVisitor;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManagerImpl.TxDataHandlerFactory;
import org.eclipse.osee.orcs.data.ArtifactWriteable;

/**
 * @author Roberto E. Escobar
 */
public class TxDataHandlerFactoryImpl implements TxDataHandlerFactory {

   private final DataFactory dataFactory;

   public TxDataHandlerFactoryImpl(DataFactory dataFactory) {
      super();
      this.dataFactory = dataFactory;
   }

   @Override
   public ArtifactVisitor createOnDirtyHandler(List<ArtifactTransactionData> data) {
      return new CollectAndCopyDirtyData(dataFactory, data);
   }

   @Override
   public OrcsVisitor createOnSuccessHandler(Map<String, ArtifactWriteable> writeableArtifacts) {
      // Do Nothing on success;
      return new OrcsVisitorAdapter();
   }
}
