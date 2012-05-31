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

import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.data.AttributeWriteable;
import org.eclipse.osee.orcs.data.GraphReadable;
import org.eclipse.osee.orcs.data.GraphWriteable;

public class Tester {

   OrcsTransaction getTransaction() {
      return null;
   }

   OrcsApi getApi() {
      return null;
   }

   public static void main(String[] args) throws Exception {
      Tester x = new Tester();

      ArtifactReadable artifact1 = null;
      ArtifactReadable artifact2 = null;

      x.modifyOneArtifact(artifact1);

      GraphReadable readableGraph = x.getApi().getGraph(null);

      OrcsTransaction tx = x.getTransaction(); // branch and user and comment

      GraphWriteable wGraph = tx.asWriteableGraph(readableGraph);

      ArtifactWriteable wArt1 = tx.asWritable(artifact1);
      ArtifactWriteable wArt2 = tx.asWritable(artifact2);

      for (ArtifactWriteable child : wGraph.getWriteableChildren(wArt1)) {
         child.setName("George");
      }

      List<AttributeWriteable<String>> attributes = wArt1.getWriteableAttributes();
      for (AttributeWriteable<String> attribute : attributes) {
         attribute.setValue("Hello");
      }

      wArt1.setName("Name");
      wArt1.setSoleAttribute(CoreAttributeTypes.Annotation, "hello");

      wArt2.setName("Shawn");

      tx.commit();
   }

   private void modifyOneArtifact(ArtifactReadable artifact1) throws OseeCoreException {
      OrcsTransaction tx = getTransaction(); // branch and user and comment
      tx.asWritable(artifact1).setName("new Name");
      tx.commit();
   }
}
