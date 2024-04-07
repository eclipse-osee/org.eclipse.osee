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
import { AsyncPipe, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MatCard,
	MatCardActions,
	MatCardContent,
	MatCardHeader,
	MatCardSubtitle,
	MatCardTitle,
	MatCardTitleGroup,
} from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
	EditEnumSetDialogComponent,
	EditTypeDialogComponent,
} from '@osee/messaging/shared/dialogs';
import { editPlatformTypeDialogDataMode } from '@osee/messaging/shared/enumerations';
import {
	EnumerationUIService,
	PreferencesUIService,
	TypesUIService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import type {
	PlatformType,
	editPlatformTypeDialogData,
} from '@osee/messaging/shared/types';
import {
	createArtifact,
	modifyArtifact,
	modifyRelation,
	relation,
} from '@osee/shared/types';
import { OperatorFunction, iif, of } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { PlatformTypeActionsComponent } from '../platform-type-actions/platform-type-actions.component';

@Component({
	selector: 'osee-messaging-types-platform-type-card',
	templateUrl: './platform-type-card.component.html',
	styles: [],
	standalone: true,
	imports: [
		NgIf,
		AsyncPipe,
		MatCard,
		MatCardHeader,
		MatCardTitleGroup,
		MatCardTitle,
		MatCardSubtitle,
		MatCardContent,
		MatCardActions,
		MatButton,
		MatIcon,
		PlatformTypeActionsComponent,
	],
})
export class PlatformTypeCardComponent {
	@Input() typeData!: PlatformType;
}
