/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import type { applic } from '@osee/applicability/types';
import type { changeReportRow } from '@osee/shared/types/change-report';

export type MimChangeSummary = {
	nodes: Record<`${number}`, MimChangeSummaryItem>;
	connections: Record<`${number}`, MimChangeSummaryItem>;
	messages: Record<`${number}`, MimChangeSummaryItem>;
	subMessages: Record<`${number}`, MimChangeSummaryItem>;
	structures: Record<`${number}`, MimChangeSummaryItem>;
};

export type MimChangeSummaryItem = {
	name: string;
	artId: `${number}`;
	added: boolean;
	deleted: boolean;
	applicabilityChanged: boolean;
	addedDueToApplicChange: boolean;
	deletedDueToApplicChange: boolean;
	wasApplic: applic;
	isApplic: applic;
	artType: string;
	allTxIds: ({ id: string; branchId: string } | string)[];
	itemTxIds: ({ id: string; branchId: string } | string)[];
	attributeChanges: changeReportRow[];
	relationChanges: changeReportRow[];
	children: MimChangeSummaryItem[];
};

export type diffUrl = {
	label?: string;
	url?: string;
};

export type branchSummary = {
	pcrNo: string;
	description: string;
	compareBranch: string;
	reportDate?: string;
};

export type diffReportSummaryItem = {
	id: string;
	changeType: string;
	action: string;
	name: string;
	details: string[];
};
