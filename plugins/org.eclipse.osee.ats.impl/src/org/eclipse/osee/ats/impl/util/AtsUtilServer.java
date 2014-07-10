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
package org.eclipse.osee.ats.impl.util;

import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsUtilServer {

   public static QueryBuilder getQuery(OrcsApi orcsApi) {
      return orcsApi.getQueryFactory(null).fromBranch(AtsUtilCore.getAtsBranch());
   }

}
