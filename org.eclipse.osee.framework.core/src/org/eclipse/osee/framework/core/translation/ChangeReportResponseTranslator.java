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

import java.util.List;

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
		CHANGE_ITEM, COUNT;
	}

	private final IDataTranslationService service;

	public ChangeReportResponseTranslator(IDataTranslationService service) {
		super();
		this.service = service;
	}

	@Override
	public ChangeReportResponse convert(PropertyStore propertyStore) throws OseeCoreException {
		ChangeReportResponse data = new ChangeReportResponse();
		int numberOfItems = propertyStore.getInt(Entry.COUNT.name());

		for (int index = 0; index < numberOfItems; index++) {
			String key = TranslationUtil.createKey(Entry.CHANGE_ITEM, index);
			PropertyStore innerStore = propertyStore.getPropertyStore(key);
			ChangeItem changeItem = service.convert(innerStore, CoreTranslatorId.CHANGE_ITEM);
			data.addItem(changeItem);
		}
		return data;
	}

	@Override
	public PropertyStore convert(ChangeReportResponse changeReportResponseData) throws OseeCoreException {
		PropertyStore store = new PropertyStore();
		List<ChangeItem> items = changeReportResponseData.getChangeItems();

		for (int index = 0; index < items.size(); index++) {
			ChangeItem changeItem = items.get(index);
			PropertyStore innerStore = service.convert(changeItem, CoreTranslatorId.CHANGE_ITEM);
			store.put(TranslationUtil.createKey(Entry.CHANGE_ITEM, index), innerStore);
		}
		store.put(Entry.COUNT.name(), items.size());
		return store;
	}
}
