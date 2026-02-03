/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.core.query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.IAtsSearchDataProvider;
import org.eclipse.osee.ats.api.query.IAtsSearchDataService;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchDataServiceImpl implements IAtsSearchDataService {

   private static final Pattern namespacePattern = Pattern.compile("\"namespace\"\\s*:\\s*\"(.*?)\"");
   private final AtsApi atsApi;
   private final JaxRsApi jaxRsApi;

   public AtsSearchDataServiceImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
      this.jaxRsApi = atsApi.jaxRsApi();
   }

   @Override
   public List<AtsSearchData> getSavedSearches(String namespace) {
      ArrayList<AtsSearchData> searches = new ArrayList<>();

      List<String> json = atsApi.getAttributeResolver().getAttributesToStringList(atsApi.userService().getUser(),
         CoreAttributeTypes.AtsActionSearch);

      for (String jsonValue : json) {
         if (jsonValue.contains("\"" + namespace + "\"")) {
            try {
               IAtsSearchDataProvider searchDataProvider = atsApi.getSearchDataProvider(namespace);
               if (searchDataProvider != null) {
                  AtsSearchData data = searchDataProvider.fromJson(namespace, jsonValue);
                  if (data != null) {
                     searches.add(data);
                  }
               }
            } catch (Exception ex) {
               // do nothing
            }
         }
      }
      return searches;
   }

   @Override
   public AtsSearchData createSearchData(String namespace, String searchName) {
      return atsApi.getSearchDataProvider(namespace).createSearchData(namespace, searchName);
   }

   @Override
   public TransactionId removeSearch(AtsSearchData data, ArtifactToken user) {
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Remove ATS Search", atsApi.getUserService().getCurrentUser());

      TransactionId transaction = TransactionId.SENTINEL;
      try {
         IAttribute<Object> attr = getAttrById(user, data.getId());
         if (attr != null) {
            changes.deleteAttribute(user, attr);
            transaction = changes.execute();
         }
         atsApi.getUserService().getCurrentUserNoCache();
      } catch (Exception ex) {
         throw new OseeCoreException("Unable to remove ATS Search", ex);
      }
      return transaction;
   }

   @Override
   public AtsSearchData getSearch(AtsUser atsUser, Long id) {
      try {
         ArtifactId userArt = atsApi.getStoreObject(atsUser);
         IAttribute<Object> attr = getAttrById(userArt, id);
         if (attr != null) {
            String json = (String) attr.getValue();
            AtsSearchData existing = fromJson(json);
            if (existing != null) {
               return existing;
            }
         }
         return null;
      } catch (Exception ex) {
         throw new OseeCoreException("Unable to get ATS Search", ex);
      }
   }

   private AtsSearchData fromJson(String jsonValue) {
      Matcher m = namespacePattern.matcher(jsonValue);
      if (m.find()) {
         String namespace = m.group(1);
         return atsApi.getSearchDataProvider(namespace).fromJson(namespace, jsonValue);
      }
      return null;
   }

   @Override
   public AtsSearchData getSearch(String jsonStr) {
      return fromJson(jsonStr);
   }

   @Override
   public TransactionId saveSearch(AtsSearchData data, ArtifactToken user) {
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Save ATS Search", atsApi.getUserService().getCurrentUser());

      TransactionId transaction = TransactionId.SENTINEL;
      try {
         IAttribute<Object> attr = getAttrById(user, data.getId());
         if (attr == null) {
            changes.addAttribute(user, CoreAttributeTypes.AtsActionSearch, jaxRsApi.toJson(data));
         } else {
            changes.setAttribute(user, attr, jaxRsApi.toJson(data));
         }
         if (!changes.isEmpty()) {
            transaction = changes.execute();
         }
         atsApi.getUserService().getCurrentUserNoCache();
      } catch (Exception ex) {
         throw new OseeCoreException("Unable to store ATS Search", ex);
      }
      return transaction;
   }

   private IAttribute<Object> getAttrById(ArtifactId artifact, Long attrId) {
      for (IAttribute<Object> attr : atsApi.getAttributeResolver().getAttributes(artifact,
         CoreAttributeTypes.AtsActionSearch)) {
         String jsonValue = (String) attr.getValue();
         try {
            AtsSearchData data = fromJson(jsonValue);
            if (data != null) {
               if (attrId.equals(data.getId())) {
                  return attr;
               }
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      return null;
   }

}
