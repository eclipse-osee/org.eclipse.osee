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
package org.eclipse.osee.framework.search.engine.test.mocks;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;

/**
 * @author Roberto E. Escobar
 */
public class TagCollector implements ITagCollector {

   private final Collection<Pair<String, Long>> actualTags;

   public TagCollector(Collection<Pair<String, Long>> actualTags) {
      this.actualTags = actualTags;
   }

   @Override
   public void addTag(String word, Long codedTag) {
      actualTags.add(new Pair<String, Long>(word, codedTag));
   }
}