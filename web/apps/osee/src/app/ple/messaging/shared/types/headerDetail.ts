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
import { headerDetail } from '@osee/shared/types';
import type { branchSummary, diffReportSummaryItem } from './DifferenceReport';
import type { DisplayableElementProps } from './element';
import type { message } from './messages';
import type { displayableStructureFields } from './structure';
import type { subMessage } from './sub-messages';
import type { transportType } from './transportType';

export type messageHeaderDetail = {} & headerDetail<message>;
export type subMessageHeaderDetail = {} & headerDetail<subMessage>;
export type structureHeaderDetail = {} & headerDetail<
	displayableStructureFields & {
		txRate: unknown;
		publisher: unknown;
		subscriber: unknown;
		messageNumber: unknown;
		messagePeriodicity: unknown;
	}
>;

export type elementHeaderDetail = {} & headerDetail<DisplayableElementProps>;

export type branchSummaryHeaderDetail = {} & headerDetail<branchSummary>;

export type diffReportSummaryHeaderDetail =
	{} & headerDetail<diffReportSummaryItem>;

export type transportTypeSummaryHeaderDetail = {} & headerDetail<transportType>;
