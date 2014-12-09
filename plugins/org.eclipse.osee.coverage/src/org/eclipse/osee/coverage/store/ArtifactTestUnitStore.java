/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;

/**
 * @author John R. Misinco
 */
public class ArtifactTestUnitStore implements ITestUnitStore {

   private static final String COVERAGE_TEST_UNIT_NAME_SEQ = "COVERAGE_TEST_UNIT_NAME_SEQ";

   private final Artifact coveragePackage;
   private Artifact readOnlyTestUnitNames;

   private static final IArtifactToken READ_ONLY_TEST_UNIT_NAMES = TokenFactory.createArtifactToken(
      "Bs+PvSVQf3R5EHSTcyQA", "ReadOnlyTestUnitNames", CoreArtifactTypes.GeneralData);
   public static final String READ_ONLY_GUID = READ_ONLY_TEST_UNIT_NAMES.getGuid();
   private final AtomicBoolean isRegisteredForEvents = new AtomicBoolean(false);

   public ArtifactTestUnitStore(Artifact coveragePackage, Artifact readOnlyTestUnitNames) {
      this.coveragePackage = coveragePackage;
      this.readOnlyTestUnitNames = readOnlyTestUnitNames;
   }

   @Override
   public void load(TestUnitCache cache) throws OseeCoreException {
      if (!isRegisteredForEvents.get() && coveragePackage != null) {
         IEventListener eventListener = createEventListener(coveragePackage, cache);
         OseeEventManager.addListener(eventListener);

         isRegisteredForEvents.compareAndSet(false, true);
      }
      String data = getAttributeData();
      parse(data, cache);
   }

   private IEventListener createEventListener(final Artifact artifact, final TestUnitCache cache) {
      IArtifactEventListener eventListener = new IArtifactEventListener() {

         @Override
         public List<? extends IEventFilter> getEventFilters() {
            return Arrays.asList(new ArtifactEventFilter(artifact));
         }

         @Override
         public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
            cache.reset();
         }

      };

      return eventListener;
   }

   private String getAttributeData() throws OseeCoreException {
      String data = Strings.EMPTY_STRING;
      if (coveragePackage != null) {
         data = coveragePackage.getSoleAttributeValueAsString(CoverageAttributeTypes.UnitTestTable, "");
      }
      if (!Strings.isValid(data)) {
         if (readOnlyTestUnitNames == null) {
            readOnlyTestUnitNames = ArtifactQuery.getArtifactFromToken(READ_ONLY_TEST_UNIT_NAMES, CoreBranches.COMMON);
         }
         data = readOnlyTestUnitNames.getSoleAttributeValueAsString(CoreAttributeTypes.GeneralStringData, "");
      }
      return data;
   }

   @Override
   public void store(TestUnitCache cache, SkynetTransaction transaction) throws OseeCoreException {

      Set<Entry<Integer, String>> entries = cache.getAllCachedTestUnitEntries();
      List<Entry<Integer, String>> entriesList = new ArrayList<Entry<Integer, String>>(entries);
      Collections.sort(entriesList, new Comparator<Entry<Integer, String>>() {

         @Override
         public int compare(Entry<Integer, String> o1, Entry<Integer, String> o2) {
            return o1.getKey().compareTo(o2.getKey());
         }
      });

      String storage = asStorage(entriesList);

      if (coveragePackage != null) {
         coveragePackage.setSoleAttributeFromString(CoverageAttributeTypes.UnitTestTable, storage);
         coveragePackage.persist(transaction);
      } else if (readOnlyTestUnitNames != null) {
         readOnlyTestUnitNames.setSoleAttributeFromString(CoreAttributeTypes.GeneralStringData, storage);
         readOnlyTestUnitNames.persist(transaction);
      }
   }

   protected String asStorage(List<Entry<Integer, String>> entries) {
      StringBuilder sb = new StringBuilder();
      boolean firstTime = true;
      for (Entry<Integer, String> entry : entries) {
         if (!firstTime) {
            sb.append("\n");
         }
         sb.append(entry.getKey());
         sb.append("|");
         sb.append(entry.getValue());
         firstTime = false;
      }
      return sb.toString();
   }

   protected void parse(String data, TestUnitCache cache) throws OseeCoreException {
      StringTokenizer entries = new StringTokenizer(data, "\n");
      while (entries.hasMoreElements()) {
         StringTokenizer idName = new StringTokenizer(entries.nextToken(), "|");
         if (idName.countTokens() == 2) {
            String id = idName.nextToken();
            String testUnitName = idName.nextToken().trim();
            if (Strings.isValid(id, testUnitName)) {
               int key = Integer.parseInt(id);
               cache.put(key, testUnitName);
            } else {
               throw new OseeArgumentException("Invalid Test Unit Name");
            }
         } else {
            throw new OseeArgumentException("Invalid Test Unit Name");
         }
      }
   }

   @Override
   public int getNextTestUnitId() throws OseeCoreException {
      return (int) ConnectionHandler.getNextSequence(COVERAGE_TEST_UNIT_NAME_SEQ);
   }
}
