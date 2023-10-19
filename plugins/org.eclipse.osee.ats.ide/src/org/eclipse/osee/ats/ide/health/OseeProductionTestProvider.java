/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.ide.health;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

public interface OseeProductionTestProvider {

   public Collection<StandAloneRestData> getStandAloneRestDatas();

   public void testAtsQuickSearchQueries(XResultData rd);

   public void testPublishing_1(XResultData rd);

   public void testPublishing_2(XResultData rd);

}
