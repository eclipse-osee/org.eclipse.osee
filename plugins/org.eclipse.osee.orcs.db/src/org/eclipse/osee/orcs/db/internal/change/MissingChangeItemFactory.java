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
package org.eclipse.osee.orcs.db.internal.change;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;

/**
 * @author John Misinco
 */
public interface MissingChangeItemFactory {

   Collection<ChangeItem> createMissingChanges(HasCancellation cancellation, OrcsSession session, List<ChangeItem> changes, TransactionToken sourceTx, TransactionToken destTx, ApplicabilityQuery applicQuery) ;

}
