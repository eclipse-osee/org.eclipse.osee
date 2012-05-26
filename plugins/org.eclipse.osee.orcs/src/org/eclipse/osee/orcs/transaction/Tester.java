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
package org.eclipse.osee.orcs.transaction;

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.Graph;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.WritableArtifact;

public class Tester {

   OrcsTransaction getTransaction() {
      return null;
   }

   public static void main(String[] args) throws Exception {
      Tester x = new Tester();

      ReadableArtifact artifact1 = null;
      ReadableArtifact artifact2 = null;
      Graph graph = x.getGrpah();

      OrcsTransaction tx = x.getTransaction(); // branch and user and comment

      WritableArtifact wArt1 = tx.asWritable(artifact1);
      WritableArtifact wArt2 = tx.asWritable(artifact2);

      for (WritableArtifact child : wArt1.getChildren()) {
         child.setName("George");
      }

      wArt1.setName("Name");
      wArt1.setSoleAttribute(CoreAttributeTypes.Annotation, "hello");

      wArt2.setName("Shawn");

      tx.commit();

      //      tx.relate(relationType, aArt, bArt);

      //      tx.createCommit().call();

   }

   private Graph getGrpah() {
      return null;
   }

   private void modifyOneArtifact(ReadableArtifact artifact1) throws OseeCoreException {
      OrcsTransaction tx = getTransaction(); // branch and user and comment
      tx.asWritable(artifact1).setName("new Name");
      tx.commit();

   }
}
