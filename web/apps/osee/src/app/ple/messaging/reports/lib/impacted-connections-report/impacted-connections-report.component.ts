/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { AsyncPipe, JsonPipe } from '@angular/common';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ReportsService } from '@osee/messaging/shared/services';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { map } from 'rxjs';
import { NamedIdTableComponent } from './named-id-table/named-id-table.component';
import { toSignal } from '@angular/core/rxjs-interop';
@Component({
	selector: 'osee-impacted-connections-report',
	standalone: true,
	imports: [JsonPipe, AsyncPipe, NamedIdTableComponent],
	template: `<div class="mat-h3">Impacted Connections:</div>
		<osee-named-id-table
			[content]="impactedConnections()"></osee-named-id-table>`,
})
export class ImpactedConnectionsReportComponent {
	constructor(
		private route: ActivatedRoute,
		private ui: UiService,
		private reportsService: ReportsService,
		private branchService: CurrentBranchInfoService
	) {
		this.route.paramMap.subscribe((params) => {
			this.ui.idValue = params.get('branchId') || '';
			this.ui.typeValue =
				(params.get('branchType') as 'working' | 'baseline' | '') || '';
		});
	}

	branchName = this.branchService.currentBranch.pipe(map((br) => br.name));
	_impactedConnections$ = this.reportsService.impactedConnectionsArtifacts;
	impactedConnections = toSignal(this._impactedConnections$, {
		initialValue: [],
	});
}

export default ImpactedConnectionsReportComponent;
