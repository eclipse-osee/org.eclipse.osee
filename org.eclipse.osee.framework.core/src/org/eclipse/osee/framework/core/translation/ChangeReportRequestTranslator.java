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
package org.eclipse.osee.framework.core.translation;

import org.eclipse.osee.framework.core.data.ChangeReportRequest;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportRequestTranslator implements ITranslator<ChangeReportRequest> {
	private enum Entry {
		SRC_TRANSACTION, DEST_TRANSACTION, IS_HISTORY
	}

	@Override
	public ChangeReportRequest convert(PropertyStore propertyStore) throws OseeCoreException {
		int srcTx = propertyStore.getInt(Entry.SRC_TRANSACTION.name());
		int destTx = propertyStore.getInt(Entry.DEST_TRANSACTION.name());

		boolean isHistory = propertyStore.getBoolean(Entry.IS_HISTORY.name());
		ChangeReportRequest data = new ChangeReportRequest(srcTx, destTx, isHistory);
		return data;
	}

	@Override
	public PropertyStore convert(ChangeReportRequest data) throws OseeCoreException {
		PropertyStore store = new PropertyStore();
		store.put(Entry.IS_HISTORY.name(), data.isHistorical());
		store.put(Entry.SRC_TRANSACTION.name(), data.getSourceTx());
		store.put(Entry.DEST_TRANSACTION.name(), data.getDestinationTx());
		return store;
	}
}
