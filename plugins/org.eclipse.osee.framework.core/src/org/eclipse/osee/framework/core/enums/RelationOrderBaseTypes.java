/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *public static final CoreAttributeTypes   Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.RelationSorter;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationOrderBaseTypes {
   public static final RelationSorter USER_DEFINED = RelationSorter.USER_DEFINED;
   public static final RelationSorter LEXICOGRAPHICAL_ASC = RelationSorter.LEXICOGRAPHICAL_ASC;
   public static final RelationSorter LEXICOGRAPHICAL_DESC = RelationSorter.LEXICOGRAPHICAL_DESC;
   public static final RelationSorter UNORDERED = RelationSorter.UNORDERED;
   public static final RelationSorter PREEXISTING = RelationSorter.PREEXISTING;
}