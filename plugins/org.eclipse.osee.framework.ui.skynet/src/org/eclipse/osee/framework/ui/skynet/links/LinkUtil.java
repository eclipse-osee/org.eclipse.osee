/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.links;

import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.account.rest.model.Link;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.event.EventType;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.TopicEvent;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.util.FrameworkEvents;

/**
 * @author Donald G. Dunne
 */
public class LinkUtil {

   private LinkUtil() {
      // Utility class
   }

   public static AccountWebPreferences getAccountsPreferencesData(boolean global) throws Exception {
      return getAccountsPreferencesData(getStoreArtifact(global));
   }

   public static AccountWebPreferences getAccountsPreferencesData(ArtifactId accountId) throws Exception {
      AccountClient client = ServiceUtil.getAccountClient();
      return client.getAccountWebPreferencesByUniqueField(accountId);
   }

   /**
    * Delete single link from user or global links and store
    */
   public static void deleteLink(ArtifactId accountId, Link deleteLink) throws Exception {
      Artifact golbalArtifact = getStoreArtifact(true);
      Conditions.checkNotNull(golbalArtifact, "Guest accountId: " + SystemUser.Anonymous.getIdString());
      deleteLink(deleteLink, true, golbalArtifact);

      Artifact userArt = ArtifactQuery.getArtifactFromId(accountId, CoreBranches.COMMON);
      Conditions.checkNotNull(userArt, "User Artifact accountId: " + accountId);
      deleteLink(deleteLink, false, userArt);
   }

   public static void deleteLink(Link deleteLink, boolean global) throws Exception {
      deleteLink(deleteLink, global, getStoreArtifact(global));
   }

   public static void deleteLink(Link deleteLink, boolean global, Artifact useArtifact) throws Exception {
      String webPrefStr = useArtifact.getSoleAttributeValue(CoreAttributeTypes.WebPreferences, "{}");
      AccountWebPreferences webPrefs = new AccountWebPreferences(webPrefStr, useArtifact.getName());
      Link remove = webPrefs.getLinks().remove(deleteLink.getId());
      if (remove != null) {
         saveWebPrefsToArtifactAndKickEvent(global, useArtifact, webPrefs);
      }
   }

   public static void addUpdateLink(Link link, boolean global) throws Exception {
      addUpdateLink(getStoreArtifact(global), link, global);
   }

   /**
    * Update existing link in shared/global web preferences and store
    */
   public static void addUpdateLink(Artifact useArtifact, Link link, boolean global) throws Exception {
      String webPrefStr = useArtifact.getSoleAttributeValue(CoreAttributeTypes.WebPreferences, null);
      AccountWebPreferences webPrefs = null;
      if (!Strings.isValid(webPrefStr)) {
         webPrefs = new AccountWebPreferences();
      } else {
         webPrefs = new AccountWebPreferences(webPrefStr, useArtifact.getName());
      }
      boolean found = false;
      for (Entry<String, Link> stored : webPrefs.getLinks().entrySet()) {
         if (stored.getKey().equals(link.getId())) {
            setLinkFromLink(link, stored.getValue());
            found = true;
            break;
         }
      }
      if (!found) {
         webPrefs.getLinks().put(link.getId(), link);
      }
      saveWebPrefsToArtifactAndKickEvent(global, useArtifact, webPrefs);
   }

   public static Artifact getStoreArtifact(boolean global) {
      if (global) {
         return ArtifactQuery.getArtifactFromToken(SystemUser.Anonymous);
      }
      return LinkUtil.getPersonalLinksArtifact();
   }

   public static void saveWebPreferences(AccountWebPreferences webPrefs, boolean global) throws Exception {
      saveWebPreferences(webPrefs, global, getStoreArtifact(global));
   }

   public static void saveWebPreferences(AccountWebPreferences webPrefs, boolean global, Artifact useArtifact) throws Exception {
      saveWebPrefsToArtifactAndKickEvent(global, useArtifact, webPrefs);
   }

   public static boolean setLinkFromLink(Link fromLink, Link toLink) {
      boolean changed = false;
      if (!toLink.getName().equals(fromLink.getName())) {
         toLink.setName(fromLink.getName());
         changed = true;
      }
      if (!Collections.isEqual(fromLink.getTags(), toLink.getTags())) {
         toLink.setTags(fromLink.getTags());
         changed = true;
      }
      if (!toLink.getTeam().equals(fromLink.getTeam())) {
         toLink.setTeam(fromLink.getTeam());
         changed = true;
      }
      if (!toLink.getUrl().equals(fromLink.getUrl())) {
         toLink.setUrl(fromLink.getUrl());
         changed = true;
      }
      return changed;
   }

   private static void saveWebPrefsToArtifactAndKickEvent(boolean global, Artifact useArtifact, AccountWebPreferences webPrefs) throws Exception {
      String json = JsonUtil.getMapper().writeValueAsString(webPrefs);
      useArtifact.setSoleAttributeValue(CoreAttributeTypes.WebPreferences, json);
      useArtifact.persist("Add web preferences links to " + useArtifact.toStringWithId());

      TopicEvent event =
         new TopicEvent(global ? FrameworkEvents.GLOBAL_WEB_PREFERENCES : FrameworkEvents.PERSONAL_WEB_PREFERENCES,
            "links", webPrefs.getLinks().toString(), global ? EventType.LocalAndRemote : EventType.LocalOnly);
      OseeEventManager.kickTopicEvent(LinkUtil.class, event);
   }

   public static Link getExistingLink(Link link, AccountWebPreferences webPrefs) {
      return getExistingLink(link, webPrefs.getLinks());
   }

   public static Link getExistingLink(Link link, Map<String, Link> links) {
      for (Link existingLink : links.values()) {
         if (existingLink.getId().equals(link.getId())) {
            return existingLink;
         }
      }
      return null;
   }

   public static void upateLinkFromDialog(EditLinkDialog dialog, Link link) throws Exception {
      link.setName(dialog.getEntry());
      link.setUrl(dialog.getUrl());
      link.getTags().clear();
      boolean global = dialog.isChecked();
      for (String tag : dialog.getTags().split(",")) {
         tag = tag.replaceAll("^ ", "");
         tag = tag.replaceAll(" $", "");
         if (Strings.isValid(tag) && !link.getTags().contains(tag)) {
            link.getTags().add(tag);
         }
      }
      if (link.getId() == null) {
         link.setId(GUID.create());
      }
      if (global) {
         link.setTeam("Guest");
      } else {
         User user = UserManager.getUser();
         link.setTeam(user.getName());
      }
      LinkUtil.addUpdateLink(getStoreArtifact(global), link, global);
   }

   public static Artifact getPersonalLinksArtifact() {
      return UserManager.getUser();
   }

}
