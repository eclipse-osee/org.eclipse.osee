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
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
	ActionDropDownComponent,
	BranchPickerComponent,
	UndoButtonBranchComponent,
} from '@osee/shared/components';
import { UiService } from '@osee/shared/services';
import { iif, of, switchMap } from 'rxjs';
import { RouterLink, RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ViewSelectorComponent } from '../view-selector/view-selector.component';
import {
	MimRouteService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';

@Component({
	selector: 'osee-messaging-controls',
	standalone: true,
	imports: [
		CommonModule,
		RouterLink,
		RouterOutlet,
		MatButtonModule,
		MatIconModule,
		MatTooltipModule,
		ActionDropDownComponent,
		BranchPickerComponent,
		UndoButtonBranchComponent,
		ViewSelectorComponent,
	],
	templateUrl: './messaging-controls.component.html',
})
export class MessagingControlsComponent {
	@Input() branchControls: boolean = true;
	@Input() actionControls: boolean = false;
	@Input() diff: boolean = false;
	@Input() diffRouteLink: string | any | null | undefined = null;

	constructor(
		private preferencesService: PreferencesUIService,
		private ui: UiService,
		private mimRoutes: MimRouteService
	) {}

	inEditMode = this.preferencesService.inEditMode;
	branchId = this.ui.id;
	branchType = this.ui.type;

	inDiffMode = this.mimRoutes.isInDiff.pipe(
		switchMap((val) => iif(() => val, of('true'), of('false')))
	);
}
