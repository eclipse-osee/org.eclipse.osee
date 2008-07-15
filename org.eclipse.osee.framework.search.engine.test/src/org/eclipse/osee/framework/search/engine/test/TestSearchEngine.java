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
package org.eclipse.osee.framework.search.engine.test;

import junit.framework.TestCase;

/**
 * @author Roberto E. Escobar
 */
public class TestSearchEngine extends TestCase {

   public void testGettingSearchEngine() {
      assertNotNull(Activator.getInstance().getSearchEngine());
   }

   public void testGettingSearchTagger() {
      assertNotNull(Activator.getInstance().getSearchTagger());
   }

   public void test() {
      // create dummy attribute

      // tag it
      //      ISearchEngineTagger tagger = Activator.getInstance().getSearchTagger();
      //      tagger.tagAttribute(4);

      // check tagged

      // delete dummy attribute

   }
}