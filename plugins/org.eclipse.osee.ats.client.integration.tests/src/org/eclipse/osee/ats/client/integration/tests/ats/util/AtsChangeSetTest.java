/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.util;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsChangeSetTest {

   Artifact folderArt = null;

   @Before
   public void setup() {
      folderArt =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, AtsUtilCore.getAtsBranch(), "AtsChangeSetTest");
      folderArt.setSoleAttributeValue(CoreAttributeTypes.StaticId, "my static id");
      folderArt.persist(getClass().getSimpleName());
   }

   @After
   public void cleanup() {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      for (Artifact artifact : ArtifactQuery.getArtifactListFromName(getClass().getSimpleName(),
         AtsUtilCore.getAtsBranch(), EXCLUDE_DELETED, QueryOption.CONTAINS_MATCH_OPTIONS)) {
         artifact.deleteAndPersist(transaction);
      }
      transaction.execute();
   }

   @Test
   public void testSetAttributeById_ArtifactId() {
      Attribute<?> staticIdAttr = null;
      for (Attribute<?> attr : folderArt.getAttributes()) {
         if (attr.getAttributeType().getId() == CoreAttributeTypes.StaticId.getGuid()) {
            staticIdAttr = attr;
            break;
         }
      }

      IAtsChangeSet changes = AtsClientService.get().getStoreService().createAtsChangeSet(getClass().getSimpleName());
      changes.setAttribute(folderArt, staticIdAttr.getId(), "new id");
      changes.execute();

      folderArt.reloadAttributesAndRelations();
      Assert.assertEquals("new id", folderArt.getSoleAttributeValue(CoreAttributeTypes.StaticId, null));
   }

   @Test
   public void testSetSoleAttributeById() {
      IAtsChangeSet changes = AtsClientService.get().getStoreService().createAtsChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue(folderArt, CoreAttributeTypes.StaticId, "newest id");
      changes.execute();

      folderArt.reloadAttributesAndRelations();
      Assert.assertEquals("newest id", folderArt.getSoleAttributeValue(CoreAttributeTypes.StaticId, null));
   }

}
