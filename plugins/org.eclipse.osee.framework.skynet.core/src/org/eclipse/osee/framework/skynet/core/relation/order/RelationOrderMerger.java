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
package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.Collection;
import java.util.List;

public class RelationOrderMerger {
	public /*<T>*/ void computeMergedOrder(List<String> leftOrder, List<String> rightOrder, Collection<String> mergedSet) {
		// 1) Cross out anything not found in mergedSet
		// 2) Star not anything in both lists
		// 3) Perform cursor algorithm to either
		//    a) generate a merged order, or
		//    b) fail
	}
}
