/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.account.admin.internal;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class AccountPreferencesArtifact extends BaseId implements AccountPreferences {

   private final ArtifactReadable artifact;

   private Map<String, String> data = Collections.emptyMap();
   private final AtomicBoolean wasLoaded = new AtomicBoolean(false);

   public AccountPreferencesArtifact(String uuid, ArtifactReadable artifact) {
      super(artifact.getId());
      this.artifact = artifact;
   }

   @Override
   public String get(String key) {
      checkLoaded();
      return data.get(key);
   }

   @Override
   public boolean getBoolean(String key) {
      checkLoaded();
      String value = data.get(key);
      return Boolean.valueOf(value);
   }

   @Override
   public Set<String> getKeys() {
      checkLoaded();
      return data.keySet();
   }

   @Override
   public Map<String, String> asMap() {
      checkLoaded();
      return Collections.unmodifiableMap(data);
   }

   private void checkLoaded() {
      if (wasLoaded.compareAndSet(false, true)) {
         String settings = artifact.getSoleAttributeValue(CoreAttributeTypes.UserSettings, null);
         if (settings != null) {
            PropertyStore storage = new PropertyStore(artifact.getGuid());
            try {
               storage.load(new StringReader(settings));
            } catch (Exception ex) {
               throw OseeCoreException.wrap(ex);
            }
            Map<String, String> map = new HashMap<>();
            for (String key : storage.keySet()) {
               map.put(key, storage.get(key));
            }
            data = map;
         }
      }
   }
}