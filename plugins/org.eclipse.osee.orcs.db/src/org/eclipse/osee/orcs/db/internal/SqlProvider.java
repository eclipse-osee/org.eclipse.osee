/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Roberto E. Escobar
 */
public interface SqlProvider {

   public static String SQL_DATABASE_HINTS_SUPPORTED_KEY = "are.database.hints.supported.key";
   public static String SQL_RECURSIVE_WITH_KEY = "database.recursive.with.keyword.key";
   public static String SQL_REG_EXP_PATTERN_KEY = "database.regular.expression.pattern.key";

   String getSql(OseeSql key) throws OseeCoreException;

   String getSql(String key) throws OseeCoreException;

}
