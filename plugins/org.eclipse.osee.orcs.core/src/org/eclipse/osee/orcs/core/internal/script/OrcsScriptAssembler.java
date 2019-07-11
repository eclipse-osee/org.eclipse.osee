/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.script;

import org.eclipse.osee.orcs.core.ds.DynamicData;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript;
import org.eclipse.osee.orcs.search.BranchQueryBuilder;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.TxQueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsScriptAssembler {

   void onCompileStart(OrcsScript model);

   void onCompileEnd();

   void onError(Throwable ex);

   void onScriptVersion(String version);

   void onQueryStart();

   TxQueryBuilder<?> newTxQuery();

   TxQueryBuilder<?> getTxQuery();

   void resetTxQuery();

   BranchQueryBuilder<?> newBranchQuery();

   BranchQueryBuilder<?> getBranchQuery();

   void resetBranchQuery();

   QueryBuilder newArtifactQuery();

   QueryBuilder getArtifactQuery();

   void resetArtifactQuery();

   void onQueryEnd();

   void addCollect(DynamicData object, long limit);

   int getSelectSetIndex();

}