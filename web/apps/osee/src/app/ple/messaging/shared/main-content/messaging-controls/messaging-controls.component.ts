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
import { Component, Input } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink, RouterOutlet } from '@angular/router';
import {
	MimRouteService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';
import {
	CurrentActionDropDownComponent,
	BranchPickerComponent,
	UndoButtonBranchComponent,
	ViewSelectorComponent,
} from '@osee/shared/components';
import { UiService } from '@osee/shared/services';
import { iif, of, switchMap } from 'rxjs';

@Component({
	selector: 'osee-messaging-controls',
	standalone: true,
	imports: [
		AsyncPipe,
		RouterLink,
		RouterOutlet,
		MatAnchor,
		MatIcon,
		MatTooltip,
		CurrentActionDropDownComponent,
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
