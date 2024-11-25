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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	signal,
	viewChild,
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
import {
	MatStep,
	MatStepper,
	MatStepperNext,
	MatStepperPrevious,
} from '@angular/material/stepper';
import { NodeReviewComponent } from '@osee/messaging/nodes/review';
import { NodeSearchComponent } from '@osee/messaging/nodes/search';
import { connection, nodeData } from '@osee/messaging/shared/types';
import { AddNodeDialog } from '../../dialogs/add-node-dialog/add-node-dialog';
import { NewNodeFormComponent } from '../../forms/new-node-form/new-node-form.component';
import { applicabilitySentinel } from '@osee/applicability/types';

@Component({
	selector: 'osee-add-node-dialog',
	imports: [
		AsyncPipe,
		FormsModule,
		MatDialogActions,
		MatDialogClose,
		MatDialogContent,
		MatDialogTitle,
		MatStepper,
		MatStep,
		MatStepperPrevious,
		MatStepperNext,
		MatButton,
		NewNodeFormComponent,
		NodeSearchComponent,
		NodeReviewComponent,
	],
	template: `<h1 mat-dialog-title>
			Add Node to {{ connection().name.value }}
		</h1>
		<mat-horizontal-stepper class="new-node-stepper">
			<mat-step
				label="Select Node options"
				#step1>
				<mat-dialog-content>
					<div class="tw-flex tw-items-center tw-justify-between">
						<button
							mat-flat-button
							class="primary-button tw-w-full tw-p-6"
							matStepperNext
							(click)="createNew()"
							data-cy="create-new-btn">
							Create new Node
						</button>

						<p class="tw-w-full tw-text-center">or</p>

						<form #nodeSelector="ngForm">
							<osee-node-search
								[(selectedNode)]="selectedNode"
								[protectedNodes]="
									preExistingNodes()
								"></osee-node-search>
						</form>
					</div>
				</mat-dialog-content>
				<mat-dialog-actions align="end">
					<button
						mat-raised-button
						class="primary-button"
						[disabled]="!nodeSelector.valid"
						(click)="moveToReview()">
						Proceed to Review
					</button>
				</mat-dialog-actions>
			</mat-step>
			<mat-step
				label="Define Node"
				#step2>
				<mat-dialog-content>
					<form #nodeForm="ngForm">
						<osee-new-node-form
							[(node)]="selectedNode"
							fieldPrefix="Add"></osee-new-node-form>
					</form>
				</mat-dialog-content>
				<mat-dialog-actions align="end">
					<button
						mat-stroked-button
						matStepperPrevious
						data-cy="back-2">
						Back
					</button>
					<button
						mat-flat-button
						class="primary-button"
						[disabled]="!nodeForm.valid"
						matStepperNext
						data-cy="stepper-next">
						Next
					</button>
				</mat-dialog-actions>
			</mat-step>
			<mat-step
				label="Review"
				#step3>
				<mat-dialog-content>
					<osee-node-review
						[connection]="connection()"
						[node]="selectedNode()"></osee-node-review>
				</mat-dialog-content>
				<mat-dialog-actions align="end">
					<button
						mat-stroked-button
						matStepperPrevious
						data-cy="cancel-btn">
						Back
					</button>
					<button
						mat-flat-button
						class="primary-button"
						[mat-dialog-close]="dialogData()"
						data-cy="submit-btn">
						Ok
					</button>
				</mat-dialog-actions>
			</mat-step>
		</mat-horizontal-stepper>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddNodeDialogComponent {
	private _stepper = viewChild.required(MatStepper);
	private _dialogRef = inject(MatDialogRef<AddNodeDialogComponent>);
	protected selectedNode = signal<nodeData>(
		inject<AddNodeDialog>(MAT_DIALOG_DATA).node
	);

	protected connection = signal<connection>(
		inject<AddNodeDialog>(MAT_DIALOG_DATA).connection
	).asReadonly();

	protected preExistingNodes = signal(
		inject<AddNodeDialog>(MAT_DIALOG_DATA).connection.nodes
	).asReadonly();

	protected dialogData = computed<AddNodeDialog>(() => {
		return {
			connection: this.connection(),
			node: this.selectedNode(),
		};
	});

	createNew() {
		this.selectedNode.set({
			id: '-1' as const,
			gammaId: '-1' as const,
			name: {
				id: '-1' as const,
				typeId: '1152921504606847088' as const,
				gammaId: '-1' as const,
				value: '',
			},
			description: {
				id: '-1' as const,
				typeId: '1152921504606847090' as const,
				gammaId: '-1' as const,
				value: '',
			},
			applicability: applicabilitySentinel,
			interfaceNodeNumber: {
				id: '-1' as const,
				typeId: '5726596359647826657' as const,
				gammaId: '-1' as const,
				value: '',
			},
			interfaceNodeGroupId: {
				id: '-1' as const,
				typeId: '5726596359647826658' as const,
				gammaId: '-1' as const,
				value: '',
			},
			interfaceNodeBackgroundColor: {
				id: '-1' as const,
				typeId: '5221290120300474048' as const,
				gammaId: '-1' as const,
				value: '',
			},
			interfaceNodeAddress: {
				id: '-1' as const,
				typeId: '5726596359647826656' as const,
				gammaId: '-1' as const,
				value: '',
			},
			interfaceNodeBuildCodeGen: {
				id: '-1' as const,
				typeId: '5806420174793066197' as const,
				gammaId: '-1' as const,
				value: false,
			},
			interfaceNodeCodeGen: {
				id: '-1' as const,
				typeId: '4980834335211418740' as const,
				gammaId: '-1' as const,
				value: false,
			},
			interfaceNodeCodeGenName: {
				id: '-1' as const,
				typeId: '5390401355909179776' as const,
				gammaId: '-1' as const,
				value: '',
			},
			nameAbbrev: {
				id: '-1' as const,
				typeId: '8355308043647703563' as const,
				gammaId: '-1' as const,
				value: '',
			},
			interfaceNodeToolUse: {
				id: '-1' as const,
				typeId: '5863226088234748106' as const,
				gammaId: '-1' as const,
				value: false,
			},
			interfaceNodeType: {
				id: '-1' as const,
				typeId: '6981431177168910500' as const,
				gammaId: '-1' as const,
				value: '',
			},
			notes: {
				id: '-1' as const,
				typeId: '1152921504606847085' as const,
				gammaId: '-1' as const,
				value: '',
			},
		});
	}

	moveToReview() {
		this.moveToStep(3);
	}

	moveToStep(index: number) {
		this._stepper().selectedIndex = index - 1;
	}

	onNoclick() {
		this._dialogRef.close();
	}
}
