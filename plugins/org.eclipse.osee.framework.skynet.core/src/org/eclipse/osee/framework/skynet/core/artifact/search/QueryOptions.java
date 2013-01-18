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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import org.eclipse.osee.framework.core.enums.CaseType;
import org.eclipse.osee.framework.core.enums.MatchTokenCountType;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.core.enums.TokenOrderType;

/**
 * @author John Misinco
 */
public final class QueryOptions {

   public static final QueryOption[] EXACT_MATCH_OPTIONS = {
      CaseType.MATCH_CASE,
      TokenOrderType.MATCH_ORDER,
      TokenDelimiterMatch.EXACT,
      MatchTokenCountType.MATCH_TOKEN_COUNT};

   public static final QueryOption[] CONTAINS_MATCH_OPTIONS = {
      CaseType.IGNORE_CASE,
      TokenOrderType.MATCH_ORDER,
      TokenDelimiterMatch.ANY,
      MatchTokenCountType.IGNORE_TOKEN_COUNT};
}
