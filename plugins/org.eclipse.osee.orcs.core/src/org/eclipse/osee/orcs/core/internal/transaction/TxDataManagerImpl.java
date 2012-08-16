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
package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitable;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactVisitable;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactVisitor;
import org.eclipse.osee.orcs.core.internal.proxy.ArtifactProxyFactory;
import org.eclipse.osee.orcs.core.internal.proxy.ProxyWriteable;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;

/**
 * @author Roberto E. Escobar
 */
public class TxDataManagerImpl implements TxDataManager {

   public static interface TxDataHandlerFactory {

      ArtifactVisitor createOnDirtyHandler(List<ArtifactTransactionData> data);

      OrcsVisitor createOnSuccessHandler(Map<String, ArtifactWriteable> writeableArtifacts);
   }

   private final Map<String, ArtifactWriteable> writeableArtifacts = new ConcurrentHashMap<String, ArtifactWriteable>();

   private final ArtifactProxyFactory proxyFactory;
   private final TxDataHandlerFactory handler;

   public TxDataManagerImpl(ArtifactProxyFactory proxyFactory, TxDataHandlerFactory handler) {
      super();
      this.proxyFactory = proxyFactory;
      this.handler = handler;
   }

   @Override
   public int size() {
      return writeableArtifacts.size();
   }

   @Override
   public synchronized void addWrite(ArtifactWriteable writeable) throws OseeCoreException {
      ArtifactWriteable value = writeableArtifacts.put(writeable.getGuid(), writeable);
      Conditions.checkExpressionFailOnTrue(value != null && value != writeable,
         "Another instance of writeable detected - writeable tracking would be inconsistent");
   }

   @Override
   public synchronized ArtifactWriteable getOrAddWrite(ArtifactReadable readable) throws OseeCoreException {
      String id = readable.getGuid();
      ArtifactWriteable writeable = writeableArtifacts.get(id);
      if (writeable == null) {
         writeable = proxyFactory.asWriteable(readable);
         addWrite(writeable);
      }
      return writeable;
   }

   @Override
   public void onCommitStart() throws OseeCoreException {
      setWritingEnabled(false);
   }

   @Override
   public void onCommitRollback() throws OseeCoreException {
      setWritingEnabled(true);
   }

   private void setWritingEnabled(boolean value) throws OseeCoreException {
      for (ArtifactWriteable writeable : writeableArtifacts.values()) {
         ProxyWriteable<?> item = proxyFactory.asProxyWriteable(writeable);
         item.setWritesAllowed(value);
      }
   }

   @Override
   public void onCommitSuccess(TransactionResult result) throws OseeCoreException {
      OrcsVisitor visitor = handler.createOnSuccessHandler(writeableArtifacts);
      for (OrcsVisitable visitable : result.getData()) {
         visitable.accept(visitor);
      }
   }

   @Override
   public List<ArtifactTransactionData> getChanges() throws OseeCoreException {
      List<ArtifactTransactionData> data = new ArrayList<ArtifactTransactionData>();
      ArtifactVisitor visitor = handler.createOnDirtyHandler(data);
      for (ArtifactWriteable writeable : writeableArtifacts.values()) {
         if (writeable.isDirty()) {
            ArtifactVisitable visitable = proxyFactory.getProxiedObject(writeable);
            visitable.accept(visitor);
         }
      }
      return data;
   }

   @Override
   public void onCommitEnd() {
      /// Do Something?
   }

}
