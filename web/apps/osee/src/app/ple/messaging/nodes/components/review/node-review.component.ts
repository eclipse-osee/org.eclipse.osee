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
	computed,
	input,
} from '@angular/core';
import { MatLabel } from '@angular/material/form-field';
import { connection, nodeData } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-node-review',
	standalone: true,
	imports: [MatLabel],
	template: ` @if (node().id === '-1') {
			<div class="tw-flex tw-flex-col">
				A node will be created and added to:
				<span class="tw-font-bold">{{ connection().name.value }}</span>
				with the following properties:
				<mat-label>Name: {{ node().name.value }}</mat-label>
				<mat-label
					>Name Abbreviation: {{ node().nameAbbrev.value }}</mat-label
				>
				<mat-label
					>Code Gen Name:
					{{ node().interfaceNodeCodeGenName.value }}</mat-label
				>
				<mat-label
					>Node Number:
					{{ node().interfaceNodeNumber.value }}</mat-label
				>
				<mat-label
					>Node Group ID:
					{{ node().interfaceNodeGroupId.value }}</mat-label
				>
				<mat-label
					>Node Type: {{ node().interfaceNodeType.value }}</mat-label
				>
				<mat-label
					>Description: {{ node().description.value }}</mat-label
				>
				<mat-label>Notes: {{ node().notes.value }}</mat-label>
				<mat-label
					>Node Network Address:
					{{ node().interfaceNodeAddress.value }}</mat-label
				>
				<mat-label>Applicability: {{ applicability() }}</mat-label>
				<mat-label
					>Used in code gen tool:
					{{ node().interfaceNodeToolUse.value }}</mat-label
				>
				<mat-label
					>Code Generation:
					{{ node().interfaceNodeCodeGen.value }}</mat-label
				>
				<mat-label
					>Build Code Generation:
					{{ node().interfaceNodeBuildCodeGen.value }}</mat-label
				>
			</div>
		} @else {
			<div>
				The node
				<span class="tw-font-bold">{{ node().name.value }}</span> will
				be added to the connection
				<span class="tw-font-bold">{{ connection().name.value }}</span>
			</div>
		}`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NodeReviewComponent {
	connection = input.required<connection>();
	node = input.required<nodeData>();

	protected applicability = computed(
		() => this.node().applicability?.name || ''
	);
}
