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
import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ReportsService } from '@osee/messaging/shared/services';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { map } from 'rxjs';
import { NamedIdTableComponent } from './named-id-table/named-id-table.component';
import { toSignal } from '@angular/core/rxjs-interop';
@Component({
	selector: 'osee-impacted-connections-report',
	imports: [NamedIdTableComponent],
	template: `<h5 class="tw-p-4">Impacted Connections</h5>
		<osee-named-id-table
			[content]="impactedConnections()"></osee-named-id-table>`,
})
export class ImpactedConnectionsReportComponent {
	private route = inject(ActivatedRoute);
	private ui = inject(UiService);
	private reportsService = inject(ReportsService);
	private branchService = inject(CurrentBranchInfoService);

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);

	constructor() {
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
