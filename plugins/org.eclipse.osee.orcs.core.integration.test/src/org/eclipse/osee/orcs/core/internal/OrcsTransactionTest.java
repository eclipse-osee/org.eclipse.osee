/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.OrcsIntegrationRule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTransactionTest {

   @Rule
   public OrcsIntegrationRule osgi = new OrcsIntegrationRule(this);

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   @OsgiService
   private OrcsApi orcsApi;

   private final ApplicationContext context = null; // TODO use real application context
   private TransactionFactory txFactory;
   private ArtifactReadable userArtifact;

   @Before
   public void setUp() throws Exception {
      txFactory = orcsApi.getTransactionFactory(context);
      userArtifact = getSystemUser();
   }

   @Test
   public void testCreateArtifact() throws OseeCoreException {
      String comment = "Test Artifact Write";
      OrcsTransaction tx = txFactory.createTransaction(CoreBranches.COMMON, userArtifact, comment);

      String name = "Create A Folder";
      ArtifactWriteable writeable = tx.createArtifact(CoreArtifactTypes.Folder, "Create A Folder");
      Assert.assertEquals(name, writeable.getName());

      tx.commit();
   }

   private ArtifactReadable getSystemUser() throws OseeCoreException {
      return orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

   //   public static void main(String[] args) throws Exception {
   //      Tester x = new Tester();
   //
   //      ArtifactReadable artifact1 = null;
   //      ArtifactReadable artifact2 = null;
   //
   //      x.modifyOneArtifact(artifact1);
   //
   //      GraphReadable readableGraph = x.getApi().getGraph(null);
   //
   //      OrcsTransaction tx = x.getTransaction(); // branch and user and comment
   //
   //      GraphWriteable wGraph = tx.asWriteableGraph(readableGraph);
   //
   //      ArtifactWriteable wArt1 = tx.asWritable(artifact1);
   //      ArtifactWriteable wArt2 = tx.asWritable(artifact2);
   //
   //      for (ArtifactWriteable child : wGraph.getWriteableChildren(wArt1)) {
   //         child.setName("George");
   //      }
   //
   //      List<AttributeWriteable<String>> attributes = wArt1.getWriteableAttributes();
   //      for (AttributeWriteable<String> attribute : attributes) {
   //         attribute.setValue("Hello");
   //      }
   //
   //      wArt1.setName("Name");
   //      wArt1.setSoleAttributeValue(CoreAttributeTypes.Annotation, "hello");
   //
   //      wArt2.setName("Shawn");
   //
   //      tx.commit();
   //   }
}
