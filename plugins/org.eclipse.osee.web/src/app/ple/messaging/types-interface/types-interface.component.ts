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
import { ActionDropDownComponent } from '../../../shared-components/components/action-state-button/action-drop-down/action-drop-down.component';
import { BranchPickerComponent } from '../../../shared-components/components/branch-picker/branch-picker/branch-picker.component';
import { UndoButtonBranchComponent } from '../../../shared-components/components/branch-undo-button/undo-button-branch/undo-button-branch.component';
import { CurrentTypesService } from './lib/services/current-types.service';
import { PlMessagingTypesUIService } from './lib/services/pl-messaging-types-ui.service';
import { TypeGridComponent } from './lib/type-grid/type-grid/type-grid.component';

@Component({
	selector: 'osee-messaging-types-interface',
	templateUrl: './types-interface.component.html',
	styleUrls: ['./types-interface.component.sass'],
	standalone: true,
	imports: [
		BranchPickerComponent,
		NgIf,
		AsyncPipe,
		UndoButtonBranchComponent,
		ActionDropDownComponent,
		TypeGridComponent,
	],
})
export class TypesInterfaceComponent implements OnInit {
	filterValue: string = '';
	inEditMode = this._typesService.inEditMode;
	constructor(
		private route: ActivatedRoute,
		private uiService: PlMessagingTypesUIService,
		private _typesService: CurrentTypesService
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
