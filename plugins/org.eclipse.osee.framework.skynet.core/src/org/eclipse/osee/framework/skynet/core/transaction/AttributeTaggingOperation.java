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
package org.eclipse.osee.framework.skynet.core.transaction;

import com.google.common.collect.Lists;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.IndexResources;
import org.eclipse.osee.orcs.rest.model.IndexerEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTaggingOperation extends AbstractOperation {
   private final Iterable<Long> gammaIds;

   public AttributeTaggingOperation(Iterable<Long> gammaIds) {
      super(AttributeTaggingOperation.class.getSimpleName(), Activator.PLUGIN_ID);
      this.gammaIds = gammaIds;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      OseeClient client = ServiceUtil.getOseeClient();
      IndexerEndpoint endpoint = client.getIndexerEndpoint();

      IndexResources data = new IndexResources();
      data.setWaitForIndexerToComplete(true);
      data.setGammaIds(Lists.newArrayList(gammaIds));
      try {
         endpoint.indexResources(data);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }
}
