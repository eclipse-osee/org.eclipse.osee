/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.data.TupleFamilyId;

/**
 * @author Ryan D. Brooks
 */
public final class CoreTupleFamilyTypes {

   public static final TupleFamilyId DefaultFamily = TokenFactory.createTupleFamilyType(1L);
   public static final TupleFamilyId ArtifatFamily = TokenFactory.createTupleFamilyType(2L);
   public static final TupleFamilyId AttribueFamily = TokenFactory.createTupleFamilyType(3L);
   public static final TupleFamilyId RelationFamily = TokenFactory.createTupleFamilyType(4L);

   private CoreTupleFamilyTypes() {
      // Constants
   }
}