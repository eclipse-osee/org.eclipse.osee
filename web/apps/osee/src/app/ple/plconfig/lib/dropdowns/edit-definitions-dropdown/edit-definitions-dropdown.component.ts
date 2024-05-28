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
import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';
import { CurrentActionService } from '@osee/configuration-management/services';
import { CompoundApplicabilityDropdownComponent } from '../compound-applicability-dropdown/compound-applicability-dropdown.component';
import { ConfigurationDropdownComponent } from '../configuration-dropdown/configuration-dropdown.component';
import { ConfigurationGroupDropdownComponent } from '../configuration-group-dropdown/configuration-group-dropdown.component';
import { FeatureDropdownComponent } from '../feature-dropdown/feature-dropdown.component';
import { ProductTypeDropDownComponent } from '../product-type-drop-down/product-type-drop-down.component';

@Component({
	selector: 'osee-edit-definitions-dropdown',
	templateUrl: './edit-definitions-dropdown.component.html',
	styles: [],
	standalone: true,
	imports: [
		MatButton,
		MatMenu,
		MatMenuTrigger,
		AsyncPipe,
		ConfigurationDropdownComponent,
		ConfigurationGroupDropdownComponent,
		FeatureDropdownComponent,
		CompoundApplicabilityDropdownComponent,
		ProductTypeDropDownComponent,
	],
})
export class EditDefinitionsDropdownComponent {
	isTeamLead = this.actionStateService.isTeamLead;

	constructor(
		private actionStateService: CurrentActionService //kinda hacky, but best place for it to live for now
	) {}
}
