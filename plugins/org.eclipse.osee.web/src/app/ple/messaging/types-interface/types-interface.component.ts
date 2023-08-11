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
import { AsyncPipe, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import { PlMessagingTypesUIService } from './lib/services/pl-messaging-types-ui.service';
import { TypeGridComponent } from './lib/type-grid/type-grid/type-grid.component';

@Component({
	selector: 'osee-messaging-types-interface',
	templateUrl: './types-interface.component.html',
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block;}',
	],
	standalone: true,
	imports: [NgIf, AsyncPipe, TypeGridComponent, MessagingControlsComponent],
})
export class TypesInterfaceComponent implements OnInit {
	filterValue: string = '';
	constructor(
		private route: ActivatedRoute,
		private uiService: PlMessagingTypesUIService
	) {}

	ngOnInit(): void {
		this.route.paramMap.subscribe((values) => {
			this.filterValue = values.get('type')?.trim().toLowerCase() || '';
			this.uiService.BranchIdString = values.get('branchId') || '';
			this.uiService.branchType = values.get('branchType') || '';
		});
	}
}

export default TypesInterfaceComponent;
