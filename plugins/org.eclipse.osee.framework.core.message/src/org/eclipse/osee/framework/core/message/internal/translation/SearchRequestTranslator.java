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
package org.eclipse.osee.framework.core.message.internal.translation;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.message.SearchOptions;
import org.eclipse.osee.framework.core.message.SearchRequest;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class SearchRequestTranslator implements ITranslator<SearchRequest> {

   private static enum Entry {
      BRANCH_GUID,
      BRANCH_NAME,
      RAW_SEARCH,
      OPTION_IS_CASE_SENSITIVE,
      OPTION_MATCH_WORD_ORDER,
      OPTION_IS_INCLUDE_DELETED,
      OPTION_FIND_ALL_LOCATIONS,
      OPTION_ATTRIBUTE_TYPE_FILTER_GUIDS,
      OPTION_ATTRIBUTE_TYPE_FILTER_NAMES
   }

   @Override
   public SearchRequest convert(PropertyStore store) {
      String guid = store.get(Entry.BRANCH_GUID.name());
      String name = store.get(Entry.BRANCH_NAME.name());
      IOseeBranch branch = new BranchToken(guid, name);

      String rawSearch = store.get(Entry.RAW_SEARCH.name());
      SearchOptions options = new SearchOptions();

      options.setCaseSensive(store.getBoolean(Entry.OPTION_IS_CASE_SENSITIVE.name()));
      options.setFindAllLocationsEnabled(store.getBoolean(Entry.OPTION_FIND_ALL_LOCATIONS.name()));
      options.setMatchWordOrder(store.getBoolean(Entry.OPTION_MATCH_WORD_ORDER.name()));

      boolean areDeletedAllowed = store.getBoolean(Entry.OPTION_IS_INCLUDE_DELETED.name());
      options.setDeletedIncluded(DeletionFlag.allowDeleted(areDeletedAllowed));

      String[] typeFilterGuids = store.getArray(Entry.OPTION_ATTRIBUTE_TYPE_FILTER_GUIDS.name());
      String[] typeFilterNames = store.getArray(Entry.OPTION_ATTRIBUTE_TYPE_FILTER_NAMES.name());
      if (typeFilterGuids.length > 0 && typeFilterNames.length > 0) {
         for (int index = 0; index < typeFilterGuids.length; index++) {
            guid = typeFilterGuids[index];
            name = index < typeFilterNames.length ? typeFilterNames[index] : Strings.emptyString();
            IAttributeType type = new AttributeTypeFilter(guid, name);
            options.addAttributeTypeFilter(type);
         }
      }
      return new SearchRequest(branch, rawSearch, options);
   }

   @Override
   public PropertyStore convert(SearchRequest object) {
      PropertyStore store = new PropertyStore();
      IOseeBranch branch = object.getBranch();

      store.put(Entry.BRANCH_GUID.name(), branch.getGuid());
      store.put(Entry.BRANCH_NAME.name(), branch.getName());

      store.put(Entry.RAW_SEARCH.name(), object.getRawSearch());
      SearchOptions options = object.getOptions();
      if (options != null) {
         store.put(Entry.OPTION_IS_CASE_SENSITIVE.name(), options.isCaseSensitive());
         store.put(Entry.OPTION_MATCH_WORD_ORDER.name(), options.isMatchWordOrder());
         store.put(Entry.OPTION_IS_INCLUDE_DELETED.name(), options.getDeletionFlag().areDeletedAllowed());
         store.put(Entry.OPTION_FIND_ALL_LOCATIONS.name(), options.isFindAllLocationsEnabled());

         if (options.isAttributeTypeFiltered()) {
            Collection<IAttributeType> types = options.getAttributeTypeFilter();
            String[] guids = new String[types.size()];
            String[] names = new String[types.size()];
            int index = 0;
            for (IAttributeType type : types) {
               guids[index] = type.getGuid();
               names[index] = type.getName();
               index++;
            }
            store.put(Entry.OPTION_ATTRIBUTE_TYPE_FILTER_GUIDS.name(), guids);
            store.put(Entry.OPTION_ATTRIBUTE_TYPE_FILTER_NAMES.name(), names);
         }
      }
      return store;
   }

   private static final class BranchToken extends NamedIdentity implements IOseeBranch {
      public BranchToken(String guid, String name) {
         super(guid, name);
      }
   }

   private static final class AttributeTypeFilter extends NamedIdentity implements IAttributeType {
      public AttributeTypeFilter(String guid, String name) {
         super(guid, name);
      }
   }
}
