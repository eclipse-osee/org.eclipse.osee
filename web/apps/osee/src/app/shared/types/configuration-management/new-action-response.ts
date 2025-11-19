/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { branch, XResultData } from '..';

export type newActionResponse = {
	actResult: actionResult;
};

export type actionResult = {
	action: `${number}`;
	teamWfs: `${number}`[];
	workingBranchId: branch;
	results: XResultData;
	transaction: actionResultTransaction;
};

type actionResultTransaction = {
	id: `${number}`;
	branchId: `${number}`;
};
