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

import java.lang.reflect.Proxy;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.OrcsIntegrationRule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.junit.Assert;
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
      String expectedName = "Create A Folder";
      String expectedAnnotation = "Annotate It";

      Branch branch = orcsApi.getBranchCache().get(CoreBranches.COMMON);
      TransactionRecord currentTx = orcsApi.getTxsCache().getHeadTransaction(branch);

      OrcsTransaction tx = txFactory.createTransaction(branch, userArtifact, comment);

      ArtifactWriteable writeable = tx.createArtifact(CoreArtifactTypes.Folder, expectedName);

      writeable.setAttributesFromStrings(CoreAttributeTypes.Annotation, expectedAnnotation);
      Assert.assertEquals(expectedName, writeable.getName());
      Assert.assertEquals(expectedAnnotation,
         writeable.getAttributeValues(CoreAttributeTypes.Annotation).iterator().next());

      String id = writeable.getGuid();

      Assert.assertTrue(Proxy.isProxyClass(writeable.getClass()));

      TransactionRecord newTx = tx.commit();
      Assert.assertFalse(tx.isCommitInProgress());

      TransactionRecord newHeadTx = orcsApi.getTxsCache().getHeadTransaction(branch);

      checkTransaction(currentTx, newTx, branch, comment, userArtifact);
      Assert.assertEquals(newTx, newHeadTx);

      ArtifactReadable artifact =
         orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andGuidsOrHrids(id).getResults().getExactlyOne();
      Assert.assertEquals(expectedName, artifact.getName());
      Assert.assertEquals(expectedAnnotation,
         artifact.getAttributeValues(CoreAttributeTypes.Annotation).iterator().next());
      Assert.assertEquals(writeable, artifact);

      Assert.assertTrue(Proxy.isProxyClass(artifact.getClass()));
   }

   private ArtifactReadable getSystemUser() throws OseeCoreException {
      return orcsApi.getQueryFactory(context).fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

   private void checkTransaction(TransactionRecord previousTx, TransactionRecord newTx, Branch branch, String comment, ArtifactReadable user) throws OseeCoreException {
      Assert.assertEquals(previousTx.getId() + 1, newTx.getId());
      Assert.assertEquals(comment, newTx.getComment());
      Assert.assertEquals(branch, newTx.getBranch());
      Assert.assertEquals(TransactionDetailsType.NonBaselined, newTx.getTxType());
      Assert.assertEquals(user.getLocalId(), newTx.getAuthor());
      Assert.assertEquals(-1, newTx.getCommit());
      Assert.assertTrue(previousTx.getTimeStamp().before(newTx.getTimeStamp()));
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
