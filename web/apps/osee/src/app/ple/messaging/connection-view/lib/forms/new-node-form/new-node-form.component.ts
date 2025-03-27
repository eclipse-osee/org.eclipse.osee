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
import {
	ChangeDetectionStrategy,
	Component,
	input,
	model,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import { nodeData } from '@osee/messaging/shared/types';
import {
	provideOptionalControlContainerNgForm,
	writableSlice,
} from '@osee/shared/utils';

@Component({
	selector: 'osee-new-node-form',
	imports: [
		MatFormField,
		MatLabel,
		MatInput,
		FormsModule,
		MatSlideToggle,
		ApplicabilityDropdownComponent,
	],
	template: `<div class="tw-grid tw-grid-cols-3 tw-gap-4">
			<mat-form-field>
				<mat-label>{{ fieldPrefix() }} name</mat-label>
				<input
					matInput
					[(ngModel)]="name"
					required
					name="name"
					data-cy="field-name" />
			</mat-form-field>
			<mat-form-field>
				<mat-label>{{ fieldPrefix() }} name abbreviation</mat-label>
				<input
					matInput
					type="text"
					[(ngModel)]="nameAbbrev"
					name="nameAbbrev"
					#input />
			</mat-form-field>
			<mat-form-field>
				<mat-label>{{ fieldPrefix() }} code gen name</mat-label>
				<input
					matInput
					type="text"
					[(ngModel)]="interfaceNodeCodeGenName"
					name="codeGenName"
					#input />
			</mat-form-field>
			<mat-form-field>
				<mat-label>{{ fieldPrefix() }} node number</mat-label>
				<input
					matInput
					[(ngModel)]="interfaceNodeNumber"
					required
					name="nodeNumber"
					data-cy="field-node-number" />
			</mat-form-field>
			<mat-form-field>
				<mat-label>{{ fieldPrefix() }} node group ID</mat-label>
				<input
					matInput
					[(ngModel)]="interfaceNodeGroupId"
					name="groupId"
					data-cy="field-node-group-id" />
			</mat-form-field>
			<mat-form-field>
				<mat-label>{{ fieldPrefix() }} node type</mat-label>
				<input
					matInput
					type="text"
					[(ngModel)]="interfaceNodeType"
					name="nodeType"
					#input />
			</mat-form-field>
			<mat-form-field>
				<mat-label>{{ fieldPrefix() }} description</mat-label>
				<input
					matInput
					[(ngModel)]="description"
					name="description"
					data-cy="field-description" />
			</mat-form-field>
			<mat-form-field>
				<mat-label>{{ fieldPrefix() }} notes</mat-label>
				<input
					matInput
					type="text"
					[(ngModel)]="notes"
					name="notes"
					#input />
			</mat-form-field>
			<mat-form-field>
				<mat-label>{{ fieldPrefix() }} network address</mat-label>
				<input
					matInput
					[(ngModel)]="interfaceNodeAddress"
					name="nodeAddress"
					data-cy="field-address" />
			</mat-form-field>
			<mat-form-field>
				<mat-label>{{ fieldPrefix() }} color</mat-label>
				<input
					matInput
					type="color"
					[(ngModel)]="interfaceNodeBackgroundColor"
					name="backgroundColor"
					data-cy="field-color" />
			</mat-form-field>
			<osee-applicability-dropdown
				[(applicability)]="applicability"></osee-applicability-dropdown>
		</div>
		<div class="tw-flex tw-flex-col tw-gap-4">
			<mat-slide-toggle
				[(ngModel)]="interfaceNodeToolUse"
				name="toolUse"
				labelPosition="after">
				Used in code gen tool
			</mat-slide-toggle>
			<mat-slide-toggle
				[(ngModel)]="interfaceNodeCodeGen"
				name="codeGen"
				labelPosition="after">
				Code Gen
			</mat-slide-toggle>
			<mat-slide-toggle
				[(ngModel)]="interfaceNodeBuildCodeGen"
				name="buildCodeGen"
				labelPosition="after">
				Code Gen for Builds
			</mat-slide-toggle>
		</div>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class NewNodeFormComponent {
	node = model.required<nodeData>();

	private nameAttr = writableSlice(this.node, 'name');
	protected name = writableSlice(this.nameAttr, 'value');
	private nameAbbrevAttr = writableSlice(this.node, 'nameAbbrev');
	protected nameAbbrev = writableSlice(this.nameAbbrevAttr, 'value');
	private interfaceNodeCodeGenNameAttr = writableSlice(
		this.node,
		'interfaceNodeCodeGenName'
	);
	protected interfaceNodeCodeGenName = writableSlice(
		this.interfaceNodeCodeGenNameAttr,
		'value'
	);
	private interfaceNodeNumberAttr = writableSlice(
		this.node,
		'interfaceNodeNumber'
	);
	protected interfaceNodeNumber = writableSlice(
		this.interfaceNodeNumberAttr,
		'value'
	);
	private interfaceNodeGroupIdAttr = writableSlice(
		this.node,
		'interfaceNodeGroupId'
	);
	protected interfaceNodeGroupId = writableSlice(
		this.interfaceNodeGroupIdAttr,
		'value'
	);
	private interfaceNodeTypeAttr = writableSlice(
		this.node,
		'interfaceNodeType'
	);
	protected interfaceNodeType = writableSlice(
		this.interfaceNodeTypeAttr,
		'value'
	);
	private descriptionAttr = writableSlice(this.node, 'description');
	protected description = writableSlice(this.descriptionAttr, 'value');
	private notesAttr = writableSlice(this.node, 'notes');
	protected notes = writableSlice(this.notesAttr, 'value');
	private interfaceNodeAddressAttr = writableSlice(
		this.node,
		'interfaceNodeAddress'
	);
	protected interfaceNodeAddress = writableSlice(
		this.interfaceNodeAddressAttr,
		'value'
	);
	private interfaceNodeBackgroundColorAttr = writableSlice(
		this.node,
		'interfaceNodeBackgroundColor'
	);
	protected interfaceNodeBackgroundColor = writableSlice(
		this.interfaceNodeBackgroundColorAttr,
		'value'
	);
	applicability = writableSlice(this.node, 'applicability');
	private interfaceNodeToolUseAttr = writableSlice(
		this.node,
		'interfaceNodeToolUse'
	);
	protected interfaceNodeToolUse = writableSlice(
		this.interfaceNodeToolUseAttr,
		'value'
	);
	private interfaceNodeCodeGenAttr = writableSlice(
		this.node,
		'interfaceNodeCodeGen'
	);
	protected interfaceNodeCodeGen = writableSlice(
		this.interfaceNodeCodeGenAttr,
		'value'
	);
	private interfaceNodeBuildCodeGenAttr = writableSlice(
		this.node,
		'interfaceNodeBuildCodeGen'
	);
	protected interfaceNodeBuildCodeGen = writableSlice(
		this.interfaceNodeBuildCodeGenAttr,
		'value'
	);
	fieldPrefix = input.required<string>();
}
