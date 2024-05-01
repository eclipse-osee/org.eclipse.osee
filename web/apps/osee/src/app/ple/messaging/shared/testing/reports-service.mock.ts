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
import { of } from 'rxjs';
import { ReportsService } from '../services';
import { NodeTraceReportMock } from './node-trace-report-mock';

export const ReportsServiceMock: Partial<ReportsService> = {
	get currentPage() {
		return of(0);
	},
	get currentPageSize() {
		return of(200);
	},
	get missingPage() {
		return of(0);
	},
	get missingPageSize() {
		return of(200);
	},
	get nodeTraceReportRequirements() {
		return of(NodeTraceReportMock);
	},
	get nodeTraceReportInterfaceArtifacts() {
		return of(NodeTraceReportMock);
	},
	get nodeTraceReportNoMatchingArtifacts() {
		return of(NodeTraceReportMock);
	},
	get nodeTraceReportNoMatchingInterfaceArtifacts() {
		return of(NodeTraceReportMock);
	},
	get nodeTraceReportRequirementsCount() {
		return of(Math.min(NodeTraceReportMock.length, 200));
	},
	get nodeTraceReportNoMatchingArtifactsCount() {
		return of(Math.min(NodeTraceReportMock.length, 200));
	},
	get nodeTraceReportInterfaceArtifactsCount() {
		return of(Math.min(NodeTraceReportMock.length, 200));
	},
	get nodeTraceReportNoMatchingInterfaceArtifactsCount() {
		return of(Math.min(NodeTraceReportMock.length, 200));
	},
};
