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
import { element } from './element';
import { structure } from './structure';
import { message } from './messages';
import { subMessage } from './sub-messages';
import { branchSummary, diffReportSummaryItem } from './DifferenceReport.d';
import { transportType } from './transportType';
import { headerDetail } from '@osee/shared/types';

export interface messageHeaderDetail extends headerDetail<message> {}
export interface subMessageHeaderDetail extends headerDetail<subMessage> {}
export interface structureHeaderDetail extends headerDetail<structure> {}

export interface elementHeaderDetail extends headerDetail<element> {}

export interface branchSummaryHeaderDetail
	extends headerDetail<branchSummary> {}

export interface diffReportSummaryHeaderDetail
	extends headerDetail<diffReportSummaryItem> {}

export interface transportTypeSummaryHeaderDetail
	extends headerDetail<transportType> {}
