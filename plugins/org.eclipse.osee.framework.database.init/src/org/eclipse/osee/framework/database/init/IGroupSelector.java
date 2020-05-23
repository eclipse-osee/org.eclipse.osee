/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.database.init;

/**
 * @author Roberto E. Escobar
 */
public interface IGroupSelector {

   void addChoice(String listName, IDatabaseInitConfiguration configuration);

   void addChoice(IDbInitChoiceEnum choice, IDatabaseInitConfiguration configuration);

}