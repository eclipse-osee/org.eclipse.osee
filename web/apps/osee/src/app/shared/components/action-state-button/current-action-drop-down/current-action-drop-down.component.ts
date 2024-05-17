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
import { Component, inject, input } from '@angular/core';
import { ActionDropDownComponent } from '../action-drop-down/action-drop-down.component';
import { CreateActionButtonComponent } from '../../create-action-button/create-action-button.component';
import { toSignal } from '@angular/core/rxjs-interop';
import {
	CurrentActionService,
	CurrentBranchInfoService,
} from '@osee/shared/services';

@Component({
	selector: 'osee-current-action-drop-down',
	standalone: true,
	imports: [ActionDropDownComponent, CreateActionButtonComponent],
	templateUrl: './current-action-drop-down.component.html',
})
export class CurrentActionDropDownComponent {
	category = input('0');
	workType = input('');

	currentBranchService = inject(CurrentBranchInfoService);
	currentActionSercice = inject(CurrentActionService);

	currentBranch = toSignal(this.currentBranchService.currentBranch);
	teamWorkflow = toSignal(this.currentActionSercice.branchWorkFlow);
}
