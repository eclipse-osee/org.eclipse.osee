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
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	input,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { editPlatformTypeDialogDataMode } from '@osee/messaging/shared/enumerations';
import {
	PreferencesUIService,
	PlatformTypeActionsService,
} from '@osee/messaging/shared/services';
import { PlatformType } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-platform-type-actions',
	imports: [MatButton, MatIcon],
	templateUrl: './platform-type-actions.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlatformTypeActionsComponent {
	typeData = input.required<PlatformType>();
	edit: editPlatformTypeDialogDataMode = editPlatformTypeDialogDataMode.edit;
	copy: editPlatformTypeDialogDataMode = editPlatformTypeDialogDataMode.copy;
	private preferenceService = inject(PreferencesUIService);
	inEditMode = toSignal(this.preferenceService.inEditMode, {
		initialValue: false,
	});
	private platformTypeActionsService = inject(PlatformTypeActionsService);

	openDialog(value: editPlatformTypeDialogDataMode) {
		this.platformTypeActionsService
			.openCopyEditDialog(value, this.typeData())
			.subscribe();
	}

	openEnumDialog(makeChanges: boolean) {
		this.platformTypeActionsService
			.openEnumDialog(this.typeData(), makeChanges)
			.subscribe();
	}
}
