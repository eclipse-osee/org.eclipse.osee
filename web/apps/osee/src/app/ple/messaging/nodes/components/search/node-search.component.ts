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
import { AsyncPipe } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	input,
	model,
	signal,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteSelectedEvent,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatTooltip } from '@angular/material/tooltip';
import { CurrentNodeService } from '@osee/messaging/nodes/components/internal';
import { nodeData } from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import {
	provideOptionalControlContainerNgForm,
	writableSlice,
} from '@osee/shared/utils';
import {
	HasValidIdDirective,
	HasValidNameDirective,
} from '@osee/shared/validators';
import {
	ReplaySubject,
	debounceTime,
	distinctUntilChanged,
	of,
	switchMap,
} from 'rxjs';
let nextUniqueId = 0;
@Component({
	selector: 'osee-node-search',
	standalone: true,
	imports: [
		FormsModule,
		AsyncPipe,
		MatFormField,
		MatLabel,
		MatInput,
		MatError,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
		MatOptionLoadingComponent,
		MatTooltip,
		HasValidIdDirective,
		HasValidNameDirective,
	],
	template: `<mat-form-field
		[id]="'node-search-' + _componentId()"
		class="tw-w-full">
		<mat-label>Select Node</mat-label>
		<input
			matInput
			type="text"
			class="tw-w-full"
			#input
			#nodeSelector="ngModel"
			placeholder="Select existing node"
			[name]="'node-search' + _componentId()"
			(ngModelChange)="filter.set($event)"
			[ngModel]="selectedNode()"
			oseeHasValidId
			oseeHasValidName
			(focusin)="autoCompleteOpened()"
			(focusout)="close()"
			[matAutocomplete]="autoNodes" />
		<mat-autocomplete
			#autoNodes="matAutocomplete"
			(optionSelected)="set($event)"
			hideSingleSelectionIndicator
			[displayWith]="displayFn">
			@if (availableNodes | async; as _availableNodes) {
				@if (availableNodesCount | async; as _count) {
					<osee-mat-option-loading
						[data]="_availableNodes"
						objectName="Nodes"
						[paginationSize]="paginationSize()"
						paginationMode="AUTO"
						[count]="_count">
						<ng-template let-option>
							<mat-option
								[value]="option"
								[matTooltip]="option.description.value"
								matTooltipClass="tw-whitespace-pre-line"
								[disabled]="
									protectedNodeIds().includes(option.id)
								"
								matTooltipShowDelay="250">
								{{ option.name.value }}
							</mat-option>
						</ng-template>
					</osee-mat-option-loading>
				}
			}
		</mat-autocomplete>
		@if (
			nodeSelector.control.errors?.invalidId ||
			nodeSelector.control.errors?.invalidName
		) {
			<mat-error> No Node Selected. </mat-error>
		}
	</mat-form-field> `,
	viewProviders: [provideOptionalControlContainerNgForm()],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NodeSearchComponent {
	protected _componentId = signal(`${nextUniqueId++}`);
	selectedNode = model.required<nodeData>();
	protectedNodes = input.required<nodeData[]>();
	protectedNodeIds = computed(() => this.protectedNodes().map((x) => x.id));
	protected filterAttr = writableSlice(this.selectedNode, 'name');
	protected filter = writableSlice(this.filterAttr, 'value');
	private filter$ = toObservable(this.filter);

	private _openAutoComplete = new ReplaySubject<void>();

	private _isOpen = signal(false);
	private nodeService = inject(CurrentNodeService);
	protected paginationSize = signal(10);
	availableNodes = this._openAutoComplete.pipe(
		debounceTime(10),
		distinctUntilChanged(),
		switchMap((_) =>
			this.filter$.pipe(
				distinctUntilChanged(),
				debounceTime(250),
				switchMap((filter) =>
					of((pageNum: string | number) =>
						this.nodeService.getPaginatedNodesByName(
							filter,
							pageNum,
							this.paginationSize(),
							'-1'
						)
					)
				)
			)
		)
	);

	availableNodesCount = this._openAutoComplete.pipe(
		debounceTime(10),
		distinctUntilChanged(),
		switchMap((_) =>
			this.filter$.pipe(
				distinctUntilChanged(),
				debounceTime(250),
				switchMap((filter) =>
					this.nodeService.getNodesByNameCount(filter)
				)
			)
		)
	);
	autoCompleteOpened() {
		this._openAutoComplete.next();
		this._isOpen.set(true);
	}
	close() {
		this._isOpen.set(false);
	}

	set(event: MatAutocompleteSelectedEvent) {
		this.selectedNode.set(event.option.value);
	}

	displayFn(value: nodeData) {
		if (value) {
			return value.name.value;
		}
		return '';
	}
}
