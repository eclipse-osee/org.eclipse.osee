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
import {
	ChangeDetectionStrategy,
	Component,
	signal,
	inject,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatInput } from '@angular/material/input';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import type { nodeData } from '@osee/messaging/shared/types';
import { NewNodeFormComponent } from '../../forms/new-node-form/new-node-form.component';
import { applicabilitySentinel } from '@osee/applicability/types';

@Component({
	selector: 'osee-create-new-node-dialog',
	template: `<h1 mat-dialog-title>Create New Node</h1>
		<mat-dialog-content>
			<osee-new-node-form
				[(node)]="result"
				fieldPrefix="Add"></osee-new-node-form>
		</mat-dialog-content>
		<div mat-dialog-actions>
			<button
				mat-button
				(click)="onNoClick()"
				data-cy="cancel-btn">
				Cancel
			</button>
			<button
				mat-flat-button
				[mat-dialog-close]="result()"
				class="primary-button"
				[disabled]="result()!.name!.value.length === 0"
				data-cy="submit-btn">
				Ok
			</button>
		</div>`,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatInput,
		FormsModule,
		MatButton,
		MatSlideToggle,
		NewNodeFormComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateNewNodeDialogComponent {
	dialogRef =
		inject<MatDialogRef<CreateNewNodeDialogComponent>>(MatDialogRef);

	result = signal<nodeData>({
		id: '-1',
		gammaId: '-1',
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: '',
		},
		description: {
			id: '-1',
			typeId: '1152921504606847090',
			gammaId: '-1',
			value: '',
		},
		applicability: applicabilitySentinel,
		interfaceNodeNumber: {
			id: '-1',
			typeId: '5726596359647826657',
			gammaId: '-1',
			value: '',
		},
		interfaceNodeGroupId: {
			id: '-1',
			typeId: '5726596359647826658',
			gammaId: '-1',
			value: '',
		},
		interfaceNodeBackgroundColor: {
			id: '-1',
			typeId: '5221290120300474048',
			gammaId: '-1',
			value: '',
		},
		interfaceNodeAddress: {
			id: '-1',
			typeId: '5726596359647826656',
			gammaId: '-1',
			value: '',
		},
		interfaceNodeBuildCodeGen: {
			id: '-1',
			typeId: '5806420174793066197',
			gammaId: '-1',
			value: false,
		},
		interfaceNodeCodeGen: {
			id: '-1',
			typeId: '4980834335211418740',
			gammaId: '-1',
			value: false,
		},
		interfaceNodeCodeGenName: {
			id: '-1',
			typeId: '5390401355909179776',
			gammaId: '-1',
			value: '',
		},
		nameAbbrev: {
			id: '-1',
			typeId: '8355308043647703563',
			gammaId: '-1',
			value: '',
		},
		interfaceNodeToolUse: {
			id: '-1',
			typeId: '5863226088234748106',
			gammaId: '-1',
			value: false,
		},
		interfaceNodeType: {
			id: '-1',
			typeId: '6981431177168910500',
			gammaId: '-1',
			value: '',
		},
		notes: {
			id: '-1',
			typeId: '1152921504606847085',
			gammaId: '-1',
			value: '',
		},
	});

	onNoClick() {
		this.dialogRef.close();
	}
}
