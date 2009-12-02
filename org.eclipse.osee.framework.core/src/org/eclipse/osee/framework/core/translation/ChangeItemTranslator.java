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

import org.eclipse.osee.framework.core.data.ArtifactChangeItem;
import org.eclipse.osee.framework.core.data.AttributeChangeItem;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.RelationChangeItem;
import org.eclipse.osee.framework.core.enums.ChangeItemType;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Jeff C. Phillips
 */
public class ChangeItemTranslator implements ITranslator<ChangeItem> {

	private enum Entry {
		BASE_ENTRY, FIRST_CHANGE, CURRENT_ENTRY, DESTINATION_ENTRY, NET_ENTRY, ART_ID, B_ART_ID, TYPE, ITEM_ID, REL_TYPE_ID, RATIONALE;
	}

	private final IDataTranslationService service;

	public ChangeItemTranslator(IDataTranslationService service) {
		super();
		this.service = service;
	}

	@Override
	public ChangeItem convert(PropertyStore store) throws OseeCoreException {
		PropertyStore currentEntryStore = store.getPropertyStore(Entry.CURRENT_ENTRY.name());
		ChangeVersion currentEntry = service.convert(currentEntryStore, CoreTranslatorId.CHANGE_VERSION);

		ChangeItem changeItem = createChangeItem(store, currentEntry);

		populateChangeVersion(store, changeItem.getCurrentVersion(), Entry.CURRENT_ENTRY);
		populateChangeVersion(store, changeItem.getBaselineVersion(), Entry.BASE_ENTRY);
		populateChangeVersion(store, changeItem.getDestinationVersion(), Entry.DESTINATION_ENTRY);
		populateChangeVersion(store, changeItem.getFirstNonCurrentChange(), Entry.FIRST_CHANGE);
		populateChangeVersion(store, changeItem.getNetChange(), Entry.NET_ENTRY);
		return changeItem;
	}

	private ChangeItem createChangeItem(PropertyStore propertyStore, ChangeVersion currentChangeVersion)
			throws OseeStateException {
		ChangeItem changeItem = null;

		int artId = Integer.parseInt(propertyStore.get(Entry.ART_ID.name()));
		int itemId = Integer.parseInt(propertyStore.get(Entry.ITEM_ID.name()));
		ChangeItemType type = ChangeItemType.getType(propertyStore.get(Entry.TYPE.name()));

		switch (type) {
		case ARTIFACT:
			changeItem = new ArtifactChangeItem(currentChangeVersion.getGammaId(), currentChangeVersion.getModType(),
					currentChangeVersion.getTransactionNumber(), artId);
			break;
		case ATTRIBUTE:
			changeItem = new AttributeChangeItem(currentChangeVersion.getGammaId(), currentChangeVersion.getModType(),
					currentChangeVersion.getTransactionNumber(), itemId, artId, currentChangeVersion.getValue());
			break;
		case RELATION:
			int bArtId = Integer.parseInt(propertyStore.get(Entry.B_ART_ID.name()));
			int relTypeId = Integer.parseInt(propertyStore.get(Entry.REL_TYPE_ID.name()));
			String rationale = propertyStore.get(Entry.RATIONALE.name());

			changeItem = new RelationChangeItem(currentChangeVersion.getGammaId(), currentChangeVersion.getModType(),
					currentChangeVersion.getTransactionNumber(), artId, bArtId, itemId, relTypeId, rationale);
			break;
		default:
			throw new OseeStateException("Invalid change item type");
		}
		return changeItem;
	}

	private void populateChangeVersion(PropertyStore store, ChangeVersion destVersion, Enum<?> key)
			throws OseeCoreException {
		PropertyStore innerStore = store.getPropertyStore(key.name());
		ChangeVersion srcVersion = service.convert(innerStore, CoreTranslatorId.CHANGE_VERSION);
		if (srcVersion != null && destVersion != null && srcVersion.isValid()) {
			destVersion.setGammaId(srcVersion.getGammaId());
			destVersion.setModType(srcVersion.getModType());
			destVersion.setTransactionNumber(srcVersion.getTransactionNumber());
			destVersion.setValue(srcVersion.getValue());
		}
	}

	@Override
	public PropertyStore convert(ChangeItem changeItem) throws OseeCoreException {
		PropertyStore store = new PropertyStore();

		store.put(Entry.ART_ID.name(), changeItem.getArtId());
		store.put(Entry.ITEM_ID.name(), changeItem.getItemId());

		if (changeItem instanceof ArtifactChangeItem) {
			store.put(Entry.TYPE.name(), ChangeItemType.ARTIFACT.name());
		} else if (changeItem instanceof AttributeChangeItem) {
			store.put(Entry.TYPE.name(), ChangeItemType.ATTRIBUTE.name());
		} else if (changeItem instanceof RelationChangeItem) {
			store.put(Entry.TYPE.name(), ChangeItemType.RELATION.name());

			RelationChangeItem relationChangeItem = (RelationChangeItem) changeItem;
			store.put(Entry.B_ART_ID.name(), relationChangeItem.getBArtId());
			store.put(Entry.REL_TYPE_ID.name(), relationChangeItem.getRelTypeId());
			store.put(Entry.RATIONALE.name(), relationChangeItem.getRationale());
		}

		storeChangeVersion(store, Entry.CURRENT_ENTRY, changeItem.getCurrentVersion());

		storeChangeVersion(store, Entry.BASE_ENTRY, changeItem.getBaselineVersion());
		storeChangeVersion(store, Entry.FIRST_CHANGE, changeItem.getFirstNonCurrentChange());
		storeChangeVersion(store, Entry.DESTINATION_ENTRY, changeItem.getDestinationVersion());
		storeChangeVersion(store, Entry.NET_ENTRY, changeItem.getNetChange());
		return store;
	}

	private void storeChangeVersion(PropertyStore store, Enum<?> entry, ChangeVersion changeVersion)
			throws OseeCoreException {
		store.put(entry.name(), service.convert(changeVersion, CoreTranslatorId.CHANGE_VERSION));
	}

}
