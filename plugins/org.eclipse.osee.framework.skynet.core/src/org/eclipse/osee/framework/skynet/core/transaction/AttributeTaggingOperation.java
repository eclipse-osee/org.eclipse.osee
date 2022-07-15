/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.transaction;

import com.google.common.collect.Lists;
import javax.ws.rs.core.Response;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
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
      Response res = endpoint.indexResources(data);
      res.close();
   }
}