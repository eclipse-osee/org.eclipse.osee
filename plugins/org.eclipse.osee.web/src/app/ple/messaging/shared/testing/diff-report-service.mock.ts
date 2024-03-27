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
import { BehaviorSubject, of } from 'rxjs';
import { connectionDiffsMock } from './difference-report-connections.mock';
import { elementDiffsMock } from './difference-report-elements.mock';
import { messageDiffsMock } from './difference-report-messages.mock';
import { nodeDiffsMock } from './difference-report-nodes.mock';
import { structureElementDiffsMock } from './difference-report-structures-elements.mock';
import { structureDiffsMock } from './difference-report-structures.mock';
import { submessageDiffsMock } from './difference-report-submessages.mock';
import { DiffReportService } from '../services/ui/diff-report.service';
import { mimChangeSummaryMock } from './mim-change-summary.mock';

export const DiffReportServiceMock: Partial<DiffReportService> = {
	get diffReport() {
		return of(mimChangeSummaryMock);
	},

	get nodes() {
		return new BehaviorSubject(nodeDiffsMock);
	},

	get connections() {
		return new BehaviorSubject(connectionDiffsMock);
	},

	get messages() {
		return new BehaviorSubject(messageDiffsMock);
	},

	get submessages() {
		return new BehaviorSubject(submessageDiffsMock);
	},

	get structures() {
		return new BehaviorSubject(structureDiffsMock);
	},

	get structuresWithElements() {
		return new BehaviorSubject(structureElementDiffsMock);
	},

	get elements() {
		return new BehaviorSubject(elementDiffsMock);
	},
};
