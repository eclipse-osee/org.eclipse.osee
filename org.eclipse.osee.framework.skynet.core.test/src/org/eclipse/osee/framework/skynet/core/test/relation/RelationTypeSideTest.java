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

package org.eclipse.osee.framework.skynet.core.test.relation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSide;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
public class RelationTypeSideTest {
   @Test
   public void testEquals() throws OseeCoreException {
      RelationTypeSide a = new RelationTypeSide(RelationTypeManager.getType(6), RelationSide.SIDE_A);
      RelationTypeSide b = new RelationTypeSide(RelationTypeManager.getType(7), RelationSide.SIDE_B);
      assertFalse(a.equals(b));
      assertTrue(a.equals(a));
   }
}
