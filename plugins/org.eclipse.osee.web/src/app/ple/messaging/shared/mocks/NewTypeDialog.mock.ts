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
import { newPlatformTypeDialogReturnData } from '../types/newTypeDialogDialogData';
import { NewTypeDialogComponent } from '../components/dialogs/new-type-dialog/new-type-dialog.component';
import { PlatformType } from '../types/platformType';

@Component({
	selector: 'osee-new-type-dialog',
	template: '<p>Dummy</p>',
})
// eslint-disable-next-line @angular-eslint/component-class-suffix
export class MockNewTypeDialog implements Partial<NewTypeDialogComponent> {
	@Output() dialogClosed =
		new EventEmitter<newPlatformTypeDialogReturnData>();
	@Input() preFillData?: PlatformType[];
	public closeDialog() {
		this.dialogClosed.emit({
			fields: [],
			createEnum: false,
			enumSetId: '-1',
			enumSetName: '',
			enumSetDescription: '',
			enumSetApplicability: { id: '1', name: 'Base' },
			enums: [],
		});
		return {
			fields: [],
			createEnum: false,
			enumSetId: '-1',
			enumSetName: '',
			enumSetDescription: '',
			enumSetApplicability: { id: '1', name: 'Base' },
			enums: [],
		};
	}
}
