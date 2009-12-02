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

import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportResponseTranslator implements ITranslator<ChangeReportResponse> {
	public enum Entry {
		COUNT;
	}

	private final IDataTranslationService service;

	public ChangeReportResponseTranslator(IDataTranslationService service) {
		super();
		this.service = service;
	}

	@Override
	public ChangeReportResponse convert(PropertyStore propertyStore) throws OseeCoreException {
		ChangeReportResponse data = new ChangeReportResponse();
		int maxCount = propertyStore.getInt(Entry.COUNT.name());

		for (int i = 0; i < maxCount; i++) {
			ChangeItem changeItem = service.convert(propertyStore.getPropertyStore(String.valueOf(i)),
					CoreTranslatorId.CHANGE_ITEM);
			data.addItem(changeItem);
		}
		return data;
	}

	@Override
	public PropertyStore convert(ChangeReportResponse changeReportResponseData) throws OseeCoreException {
		PropertyStore store = new PropertyStore();
		store.put(Entry.COUNT.name(), changeReportResponseData.getChangeItems().size());

		int index = 0;
		for (ChangeItem changeItem : changeReportResponseData.getChangeItems()) {
			store.put(String.valueOf(index++), service.convert(changeItem, CoreTranslatorId.CHANGE_ITEM));
		}
		return store;
	}
}
