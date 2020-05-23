/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;

/**
 * @author Angel Avila
 */
public interface TupleStore {

   <E1, E2> Long addTuple2(Tuple2Type<E1, E2> value, E1 e1, E2 e2);

   <E1, E2, E3> Long addTuple3(Tuple3Type<E1, E2, E3> value, E1 e1, E2 e2, E3 e3);

   <E1, E2, E3, E4> Long addTuple4(Tuple4Type<E1, E2, E3, E4> value, E1 e1, E2 e2, E3 e3, E4 e4);
}
