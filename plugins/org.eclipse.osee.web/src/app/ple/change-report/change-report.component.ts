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
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UiService } from 'src/app/ple-services/ui/ui.service';
import { RouteStateService } from '../messaging/connection-view/services/route-state-service.service';

@Component({
	selector: 'osee-change-report',
	templateUrl: './change-report.component.html',
	styleUrls: ['./change-report.component.sass'],
})
export class ChangeReportComponent implements OnInit {
	constructor(
		private route: ActivatedRoute,
		private routerState: RouteStateService,
		private uiService: UiService
	) {}

	ngOnInit(): void {
		this.route.paramMap.subscribe((params) => {
			this.routerState.branchId = params.get('branchId') || '';
			this.routerState.branchType = params.get('branchType') || '';
		});
	}

	branchId = this.uiService.id;
}
