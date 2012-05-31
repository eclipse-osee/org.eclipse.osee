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
package org.eclipse.osee.display.presenter.mocks;

import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.RelationsReadable;

/**
 * @author Roberto E. Escobar
 */
public class MockRelationsReadable implements RelationsReadable {

   private final List<ArtifactReadable> data;

   public MockRelationsReadable(List<ArtifactReadable> data) {
      super();
      this.data = data;
   }

   @Override
   public ArtifactReadable getOneOrNull() {
      return data.isEmpty() ? null : iterator().next();
   }

   @Override
   public ArtifactReadable getExactlyOne() throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(data.isEmpty(), "artifact", "artifact not found");
      return iterator().next();
   }

   @Override
   public List<ArtifactReadable> getList() {
      return data;
   }

   @Override
   public Iterable<ArtifactReadable> getIterable(int fetchSize) {
      return getList();
   }

   @Override
   public Iterator<ArtifactReadable> iterator() {
      return data.iterator();
   }

}
