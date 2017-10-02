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
package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.VersionData;

/**
 * @author Angel Avila
 */
public interface TupleObjectFactory extends VersionObjectFactory {

   TupleData createTuple2Data(VersionData version, BranchId branch, Tuple2Type<?, ?> tupleType, Long e1, Long e2) ;

   TupleData createTuple3Data(VersionData version, BranchId branch, Tuple3Type<?, ?, ?> tupleType, Long e1, Long e2, Long e3) ;

   TupleData createTuple4Data(VersionData version, BranchId branch, Tuple4Type<?, ?, ?, ?> tupleType, Long e1, Long e2, Long e3, Long e4) ;

}