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
package org.eclipse.osee.framework.skynet.core.test.importing;

import java.net.URL;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughRelation;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;

/**
 * @author Roberto E. Escobar
 */
public class RoughArtifactUtil {

   private RoughArtifactUtil() {
   }

   public static void loadDataFrom(URL expecetedData, RoughArtifactCollector collector) {
      // TODO: convert expected to actual Objects;
      //      collector.addRoughArtifact();
      //      collector.addRoughRelation();
   }

   public static void resetCollector(RoughArtifactCollector collector) {
      collector.reset();
      Assert.assertTrue(collector.getRoughArtifacts().isEmpty());
      Assert.assertTrue(collector.getRoughRelations().isEmpty());
   }

   public static void checkCollectors(RoughArtifactCollector expected, RoughArtifactCollector actual) {
      Assert.assertEquals(expected.getParentRoughArtifact(), actual.getParentRoughArtifact());

      List<RoughArtifact> expectedItems = expected.getRoughArtifacts();
      List<RoughArtifact> actualItems = actual.getRoughArtifacts();
      Assert.assertEquals(expectedItems.size(), actualItems.size());
      int size = expectedItems.size();
      for (int index = 0; index < size; index++) {
         checkRoughArtifact(expectedItems.get(index), actualItems.get(index));
      }

      List<RoughRelation> expectedRelItems = expected.getRoughRelations();
      List<RoughRelation> actualRelItems = actual.getRoughRelations();
      Assert.assertEquals(expectedRelItems.size(), actualRelItems.size());
      size = expectedRelItems.size();
      for (int index = 0; index < size; index++) {
         checkRoughRelation(expectedRelItems.get(index), actualRelItems.get(index));
      }
   }

   public static void checkRoughRelation(RoughRelation expected, RoughRelation actual) {
      Assert.assertTrue(GUID.isValid(actual.getAartifactGuid()));
      Assert.assertTrue(GUID.isValid(actual.getBartifactGuid()));
      Assert.assertEquals(expected.getRelationTypeName(), actual.getRelationTypeName());
      Assert.assertEquals(expected.getRationale(), actual.getRationale());
      Assert.assertEquals(expected.getAartifactGuid(), actual.getAartifactGuid());
      Assert.assertEquals(expected.getBartifactGuid(), actual.getBartifactGuid());
   }

   public static void checkRoughArtifact(RoughArtifact expected, RoughArtifact actual) {
      // Randomly generated - just check the format
      Assert.assertTrue(GUID.isValid(actual.getGuid()));
      Assert.assertTrue(HumanReadableId.isValid(actual.getHumandReadableId()));
      Assert.assertEquals(expected.getName(), actual.getName());
      Assert.assertEquals(expected.getPrimaryArtifactType(), actual.getPrimaryArtifactType());
      Assert.assertEquals(expected.getRoughArtifactKind(), actual.getRoughArtifactKind());
      Assert.assertEquals(expected.getRoughParent(), actual.getRoughParent());

      Assert.assertEquals(expected.getAttributes(), actual.getAttributes());
      Assert.assertEquals(expected.getURIAttributes(), actual.getURIAttributes());
   }

}
