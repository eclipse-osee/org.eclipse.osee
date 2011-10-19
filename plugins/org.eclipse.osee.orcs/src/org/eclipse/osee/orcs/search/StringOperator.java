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
package org.eclipse.osee.orcs.search;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public enum StringOperator {
   EQUALS(), // Exact Match as in Strings.equals
   NOT_EQUALS(), // inverse of exact match - !Strings.equals
   CONTAINS,
   TOKENIZED_ANY_ORDER,
   TOKENIZED_MATCH_ORDER;

}
