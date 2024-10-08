/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.mocks;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;

/**
 * @author Roberto E. Escobar
 */
public class MockTagCollector implements TagCollector {

   private final Collection<Pair<String, Long>> actualTags;

   public MockTagCollector(Collection<Pair<String, Long>> actualTags) {
      this.actualTags = actualTags;
   }

   @Override
   public void addTag(String word, Long codedTag) {
      actualTags.add(new Pair<>(word, codedTag));
   }
}