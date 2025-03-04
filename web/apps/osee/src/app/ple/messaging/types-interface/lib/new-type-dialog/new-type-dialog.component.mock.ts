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
import { Component, EventEmitter, Input, Output } from '@angular/core';
import type {
	newPlatformTypeDialogReturnData,
	PlatformType,
} from '@osee/messaging/shared/types';
import { NewTypeDialogComponent } from './new-type-dialog.component';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';

@Component({
	selector: 'osee-new-type-dialog',
	template: '<p>Dummy</p>',
	standalone: false,
})
export class MockNewTypeDialogComponent
	implements Partial<NewTypeDialogComponent>
{
	@Output() dialogClosed =
		new EventEmitter<newPlatformTypeDialogReturnData>();
	@Input() preFillData?: PlatformType[];
	public closeDialog() {
		this.dialogClosed.emit({
			platformType: new PlatformTypeSentinel(),
			createEnum: false,
			enumSetId: '-1',
			enumSetName: '',
			enumSetDescription: '',
			enumSetApplicability: { id: '1', name: 'Base' },
			enums: [],
		});
		return {
			platformType: new PlatformTypeSentinel(),
			createEnum: false,
			enumSetId: '-1',
			enumSetName: '',
			enumSetDescription: '',
			enumSetApplicability: { id: '1', name: 'Base' },
			enums: [],
		};
	}
}
