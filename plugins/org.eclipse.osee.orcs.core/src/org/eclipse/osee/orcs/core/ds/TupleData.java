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

import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.data.TupleTypeToken;

public interface TupleData extends OrcsData<TupleTypeToken> {

   TupleTypeId getTupleType();

   Long getElement1();

   Long getElement2();

   Long getElement3();

   Long getElement4();

   void setElement1(Long e1);

   void setElement2(Long e2);

   void setElement3(Long e3);

   void setElement4(Long e4);

   void setTupleType(TupleTypeId tupleType);

   void setRationale(String rationale);

}