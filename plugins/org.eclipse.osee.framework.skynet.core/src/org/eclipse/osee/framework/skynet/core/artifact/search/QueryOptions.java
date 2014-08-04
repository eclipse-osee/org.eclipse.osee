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

import org.eclipse.osee.framework.core.enums.QueryOption;

/**
 * @author John Misinco
 */
public final class QueryOptions {

   public static final QueryOption[] EXACT_MATCH_OPTIONS = {
      QueryOption.CASE__MATCH,
      QueryOption.TOKEN_MATCH_ORDER__MATCH,
      QueryOption.TOKEN_DELIMITER__EXACT,
      QueryOption.TOKEN_COUNT__MATCH};

   public static final QueryOption[] CONTAINS_MATCH_OPTIONS = {
      QueryOption.CASE__IGNORE,
      QueryOption.TOKEN_MATCH_ORDER__MATCH,
      QueryOption.TOKEN_DELIMITER__ANY,
      QueryOption.TOKEN_COUNT__IGNORE};
}
