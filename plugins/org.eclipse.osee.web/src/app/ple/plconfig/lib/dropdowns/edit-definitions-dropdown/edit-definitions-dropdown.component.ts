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
import { Component } from '@angular/core';
import { ActionStateButtonService } from '@osee/shared/components';

@Component({
	selector: 'osee-edit-definitions-dropdown',
	templateUrl: './edit-definitions-dropdown.component.html',
	styleUrls: ['./edit-definitions-dropdown.component.sass'],
})
export class EditDefinitionsDropdownComponent {
	isTeamLead = this.actionStateService.isTeamLead;

	constructor(
		private actionStateService: ActionStateButtonService //kinda hacky, but best place for it to live for now
	) {}
}
