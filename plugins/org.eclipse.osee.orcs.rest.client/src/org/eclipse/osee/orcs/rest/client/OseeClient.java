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
package org.eclipse.osee.orcs.rest.client;

import java.io.Writer;
import java.util.Collection;
import java.util.Properties;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;

/**
 * @author John Misinco
 */
public interface OseeClient {

   QueryBuilder createQueryBuilder(IOseeBranch branch);

   boolean isClientVersionSupportedByApplicationServer();

   boolean isApplicationServerAlive();

   void executeScript(String script, Properties properties, boolean debug, Writer writer);

   Collection<String> getIdeClientSupportedVersions();

   BranchEndpoint getBranchEndpoint();
   
   TransactionEndpoint getTransactionEndpoint();

}
