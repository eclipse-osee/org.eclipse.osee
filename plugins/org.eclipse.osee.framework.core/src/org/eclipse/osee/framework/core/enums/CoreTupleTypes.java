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

import static org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes.DefaultFamily;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.TupleFamilyId;
import org.eclipse.osee.framework.core.data.TupleTypeId;

/**
 * @author Ryan D. Brooks
 */
public final class CoreTupleTypes {

   public static final Tuple2Type<TupleFamilyId, TupleTypeId> TupleMetaType =
      TokenFactory.createTuple2Type(DefaultFamily, 1L);

   private CoreTupleTypes() {
      // Constants
   }
}