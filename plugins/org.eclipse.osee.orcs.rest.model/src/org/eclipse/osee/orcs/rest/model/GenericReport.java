/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.orcs.rest.model;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author David W. Miller
 */
public interface GenericReport {

   public GenericReport level(String levelName, QueryBuilder addedQuery);

   public GenericReport level(String levelName, String typeName);

   public GenericReport column(String columName);

   public GenericReport column(String columnName, String typeName);

   public GenericReport column(String columnName, AttributeTypeToken type);

   public GenericReport column(AttributeTypeToken type);

   public GenericReport type(String columnName);

   public GenericReport filter(AttributeTypeToken type, String regex);

   public QueryBuilder query();

   public OrcsApi getOrcsApi();
}
