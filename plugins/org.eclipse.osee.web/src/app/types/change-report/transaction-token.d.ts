/*********************************************************************
 * Copyright (c) 2021 Boeing
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
/**
 * Wrapper containing transaction id and the branch id
 * @see {@link org.eclipse.osee.framework.core.data.TransactionToken.java}
 */
export class transactionToken {
	id: string;
	branchId: string;
	constructor(id?: string, branchId?: string);
}
