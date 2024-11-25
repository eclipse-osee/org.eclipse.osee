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
	computed,
	inject,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { NodeDropdownComponent } from '@osee/messaging/nodes/dropdown';
import type { connection, nodeData } from '@osee/messaging/shared/types';
import { TransportTypeDropdownComponent } from '@osee/messaging/transports/dropdown';
import { applicabilitySentinel } from '@osee/applicability/types';
import { writableSlice } from '@osee/shared/utils';

@Component({
	selector: 'osee-create-connection-dialog',
	template: `<form #connectionForm="ngForm">
		<h1 mat-dialog-title>
			Create Connection{{ title() ? ' to Node: ' + title() : '' }}
		</h1>
		<mat-dialog-content>
			<div class="tw-flex tw-flex-col tw-gap-2">
				<mat-form-field
					id="connection-name-field"
					class="tw-w-full">
					<mat-label>Add a Name</mat-label>
					<input
						matInput
						name="name"
						type="text"
						[(ngModel)]="name"
						#input
						required
						data-cy="field-name" />
				</mat-form-field>
				<mat-form-field
					id="connection-description-field"
					class="tw-w-full">
					<mat-label>Add a Description</mat-label>
					<input
						matInput
						name="description"
						type="text"
						[(ngModel)]="description"
						#input
						data-cy="field-description" />
				</mat-form-field>
				<osee-transport-type-dropdown
					#transportTypeDropdown
					[(transportType)]="
						transportType
					"></osee-transport-type-dropdown>
				@if (
					connectionForm.form.controls[
						transportTypeDropdown.formId()
					] !== undefined &&
					connectionForm.form.controls[transportTypeDropdown.formId()]
						.valid
				) {
					@if (data()) {
						<osee-node-dropdown
							[(selectedNodes)]="selectedNodes"
							[transportType]="transportType()"
							[protectedNode]="
								protectedNode()
							"></osee-node-dropdown>
					} @else {
						<osee-node-dropdown
							[(selectedNodes)]="selectedNodes"
							[transportType]="
								transportType()
							"></osee-node-dropdown>
					}
				}
			</div>
		</mat-dialog-content>
		<div
			mat-dialog-actions
			align="end">
			<button
				mat-button
				(click)="onNoClick()"
				data-cy="cancel-btn">
				Cancel
			</button>
			<button
				mat-flat-button
				[mat-dialog-close]="connection()"
				class="primary-button"
				[disabled]="connectionForm.invalid || connectionForm.pending"
				data-cy="submit-btn">
				Ok
			</button>
		</div>
	</form>`,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatFormField,
		MatLabel,
		MatError,
		FormsModule,
		MatInput,
		MatButton,
		TransportTypeDropdownComponent,
		NodeDropdownComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateConnectionDialogComponent {
	private _dialogRef = inject(MatDialogRef<CreateConnectionDialogComponent>);
	protected data = signal(
		inject<nodeData | undefined>(MAT_DIALOG_DATA)
	).asReadonly();

	protected protectedNode = computed(() => {
		if (this.data()) {
			return this.data() as unknown as nodeData;
		}
		const node: nodeData = {
			id: '-1' as const,
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
		};
		return node;
	});

	protected title = signal(
		inject<nodeData | undefined>(MAT_DIALOG_DATA)?.name.value || ''
	).asReadonly();

	protected connection = signal<connection>({
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
		transportType: {
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: '',
			},
			byteAlignValidation: {
				id: '-1',
				typeId: '1682639796635579163',
				gammaId: '-1',
				value: false,
			},
			byteAlignValidationSize: {
				id: '-1',
				typeId: '6745328086388470469',
				gammaId: '-1',
				value: 0,
			},
			messageGeneration: {
				id: '-1',
				typeId: '6696101226215576386',
				gammaId: '-1',
				value: false,
			},
			messageGenerationType: {
				id: '-1',
				typeId: '7121809480940961886',
				gammaId: '-1',
				value: '',
			},
			messageGenerationPosition: {
				id: '-1',
				typeId: '7004358807289801815',
				gammaId: '-1',
				value: '',
			},
			minimumPublisherMultiplicity: {
				id: '-1',
				typeId: '7904304476851517',
				gammaId: '-1',
				value: 0,
			},
			maximumPublisherMultiplicity: {
				id: '-1',
				typeId: '8536169210675063038',
				gammaId: '-1',
				value: 0,
			},
			minimumSubscriberMultiplicity: {
				id: '-1',
				typeId: '6433031401579983113',
				gammaId: '-1',
				value: 0,
			},
			maximumSubscriberMultiplicity: {
				id: '-1',
				typeId: '7284240818299786725',
				gammaId: '-1',
				value: 0,
			},
			availableMessageHeaders: {
				id: '-1',
				typeId: '2811393503797133191',
				gammaId: '-1',
				value: [],
			},
			availableSubmessageHeaders: {
				id: '-1',
				typeId: '3432614776670156459',
				gammaId: '-1',
				value: [],
			},
			availableStructureHeaders: {
				id: '-1',
				typeId: '3020789555488549747',
				gammaId: '-1',
				value: [],
			},
			availableElementHeaders: {
				id: '-1',
				typeId: '3757258106573748121',
				gammaId: '-1',
				value: [],
			},
			interfaceLevelsToUse: {
				id: '-1',
				typeId: '1668394842614655222',
				gammaId: '-1',
				value: ['message', 'submessage', 'structure', 'element'],
			},
			dashedPresentation: {
				id: '-1',
				typeId: '3564212740439618526',
				gammaId: '-1',
				value: false,
			},
			spareAutoNumbering: {
				id: '-1',
				typeId: '6696101226215576390',
				gammaId: '-1',
				value: false,
			},
			id: '-1',
			gammaId: '-1',
			applicability: { id: '1', name: 'Base' },
			directConnection: false,
		},
		nodes:
			inject<nodeData | undefined>(MAT_DIALOG_DATA) !== undefined &&
			inject<nodeData | undefined>(MAT_DIALOG_DATA) !== null
				? [inject<nodeData>(MAT_DIALOG_DATA)]
				: [],
	});
	private nameAttr = writableSlice(this.connection, 'name');
	protected name = writableSlice(this.nameAttr, 'value');
	private descriptionAttr = writableSlice(this.connection, 'description');
	protected description = writableSlice(this.descriptionAttr, 'value');
	protected transportType = writableSlice(this.connection, 'transportType');
	protected selectedNodes = writableSlice(this.connection, 'nodes');

	protected onNoClick() {
		this._dialogRef.close();
	}
}
