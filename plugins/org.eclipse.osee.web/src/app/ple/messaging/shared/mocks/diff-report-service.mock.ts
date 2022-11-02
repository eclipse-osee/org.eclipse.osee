import { BehaviorSubject } from 'rxjs';
import { connectionDiffsMock } from 'src/app/ple-services/http/difference-report-connections.mock';
import { elementDiffsMock } from 'src/app/ple-services/http/difference-report-elements.mock';
import { messageDiffsMock } from 'src/app/ple-services/http/difference-report-messages.mock';
import { nodeDiffsMock } from 'src/app/ple-services/http/difference-report-nodes.mock';
import { structureElementDiffsMock } from 'src/app/ple-services/http/difference-report-structures-elements.mock';
import { structureDiffsMock } from 'src/app/ple-services/http/difference-report-structures.mock';
import { submessageDiffsMock } from 'src/app/ple-services/http/difference-report-submessages.mock';
import { differenceReportMock } from 'src/app/ple-services/http/difference-report.mock';
import { DiffReportService } from '../services/ui/diff-report.service';

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
export const DiffReportServiceMock: Partial<DiffReportService> = {
	// get branchInfo() {
	//     return this._branchInfo;
	// },

	// get parentBranchInfo() {
	//     return this._parentBranchInfo;
	// },

	// get branchSummary() {
	//     return this._branchSummary;
	// },

	get diffReport() {
		return new BehaviorSubject(differenceReportMock);
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
