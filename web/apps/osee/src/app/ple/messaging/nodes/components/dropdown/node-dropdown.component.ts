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
	effect,
	inject,
	input,
	signal,
} from '@angular/core';
import {
	outputFromObservable,
	takeUntilDestroyed,
	toObservable,
} from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteSelectedEvent,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import {
	MatChipGrid,
	MatChipInput,
	MatChipRemove,
	MatChipRow,
} from '@angular/material/chips';
import {
	MatError,
	MatFormField,
	MatHint,
	MatLabel,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { applicabilitySentinel } from '@osee/applicability/types';
import { CurrentNodeService } from '@osee/messaging/nodes/components/internal';
import { NodesCountDirective } from '@osee/messaging/shared/directives';
import { nodeData, transportType } from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { FormGroupDirective } from '@osee/shared/directives';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import {
	ReplaySubject,
	debounceTime,
	distinctUntilChanged,
	filter,
	map,
	of,
	switchMap,
	timeInterval,
	withLatestFrom,
} from 'rxjs';

let nextUniqueId = 0;
@Component({
	selector: 'osee-node-dropdown',
	imports: [
		MatFormField,
		MatLabel,
		MatError,
		FormsModule,
		MatChipGrid,
		MatChipRow,
		MatChipRemove,
		MatChipInput,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
		MatOptionLoadingComponent,
		MatIcon,
		AsyncPipe,
		NodesCountDirective,
		MatHint,
		FormGroupDirective,
	],
	template: `
		<div
			[ngModelGroup]="'node_dropdown_form_' + _componentId()"
			#nodeDropdownForm="ngModelGroup"
			(formGroupStatusChange)="this.__statusChanges.set($event)"
			oseeFormGroup>
			<mat-form-field
				class="tw-w-full [&>.mdc-text-field--filled]:tw-bg-inherit"
				#nodeDropdownFormField
				[id]="'node-dropdown-' + _componentId()"
				subscriptSizing="dynamic">
				<mat-label>{{ label() }}</mat-label>
				<mat-chip-grid
					#chipGrid
					[ngModel]="_innerNodes()"
					#nodeSelector="ngModel"
					[oseeNodesCount]="transportType()"
					[validationType]="validationType()"
					[name]="'node-dropdown-chip-' + _componentId()"
					[required]="required()"
					[disabled]="disabled()"
					minlength="1">
					@for (
						selectedNode of _innerNodes();
						track selectedNode.id
					) {
						<mat-chip-row (removed)="remove(selectedNode)">
							{{ selectedNode.name.value }}
							@if (protectedNode().id !== selectedNode.id) {
								<button matChipRemove>
									<mat-icon>cancel</mat-icon>
								</button>
							}
						</mat-chip-row>
					}
				</mat-chip-grid>
				<input
					matInput
					type="text"
					#input
					placeholder="Add a node"
					[name]="'node-dropdown-' + _componentId()"
					[required]="required()"
					[disabled]="disabled()"
					[(ngModel)]="filter$"
					(focusin)="autoCompleteOpened()"
					(focusout)="close()"
					[matChipInputFor]="chipGrid"
					[matAutocomplete]="autoNodes" />
				@if (!hintHidden()) {
					<mat-hint align="end">Select a Node</mat-hint>
				}
				<mat-autocomplete
					#autoNodes="matAutocomplete"
					(optionSelected)="add($event)"
					hideSingleSelectionIndicator>
					@if (availableNodes | async; as _availableNodes) {
						@if (availableNodesCount | async; as _count) {
							<osee-mat-option-loading
								[data]="_availableNodes"
								objectName="Node"
								[paginationSize]="paginationSize()"
								paginationMode="AUTO"
								[count]="_count">
								<ng-template let-option>
									<mat-option
										[value]="option"
										[disabled]="
											_innerNodes().length > maxNodes() ||
											(transportType().directConnection &&
												_innerNodes().length >= 2) ||
											selectedNodeIds().includes(
												option.id
											)
										">
										{{ option.name.value }}
									</mat-option>
								</ng-template>
							</osee-mat-option-loading>
						}
					}
				</mat-autocomplete>
				@if (
					nodeSelector.control.errors?.min ||
					nodeSelector.control.errors?.required
				) {
					<mat-error>
						Minimum nodes is
						{{ nodeSelector.control.errors?.min?.min || 1 }}
					</mat-error>
				}
				@if (nodeSelector.control.errors?.max) {
					<mat-error
						>Maximum nodes is
						{{ nodeSelector.control.errors?.max.max }}
					</mat-error>
				}
			</mat-form-field>
		</div>
	`,
	viewProviders: [provideOptionalControlContainerNgForm()],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NodeDropdownComponent {
	protected _componentId = signal(`${nextUniqueId++}`);
	selectedNodes = input.required<nodeData[]>();

	protected _innerNodes = signal<nodeData[]>([]);

	private _updateInnerNodesBasedOnInput = effect(() => {
		this._innerNodes.set(this.selectedNodes());
	});
	protected __statusChanges = signal<
		'VALID' | 'INVALID' | 'PENDING' | 'DISABLED'
	>('PENDING');
	private _statusChanges = toObservable(this.__statusChanges);

	private _selectedNodes = toObservable(this._innerNodes).pipe(
		debounceTime(0)
	);
	private validOutput = this._selectedNodes.pipe(
		withLatestFrom(this._statusChanges),
		filter(([_nodes, validity]) => validity === 'VALID'),
		timeInterval(),
		//TODO test to make sure nothing broke
		map(({ value }) => value[0]),
		takeUntilDestroyed()
	);
	selectedNodesChange = outputFromObservable(this.validOutput);

	selectedNodeIds = computed(() => this._innerNodes().map((x) => x.id));
	validSelectedNodeIds = computed(() =>
		this.selectedNodeIds().filter(
			(x) => x !== undefined && x !== null && x !== '-1'
		)
	);
	validSelectedNodeIdsLength = computed(
		() => this.validSelectedNodeIds().length
	);

	protectedNode = input<nodeData>({
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
	transportType = input.required<transportType>();
	connectionId = input<`${number}`>('-1');
	validationType = input<'connection' | 'publish' | 'subscribe'>(
		'connection'
	);
	required = input(false);
	disabled = input(false);

	hintHidden = input(false);
	label = input<string>('Select Nodes');
	validTransportType = computed(
		() =>
			this.transportType() !== undefined &&
			this.transportType() !== null &&
			this.transportType().id !== undefined &&
			this.transportType().id !== null &&
			this.transportType().id !== '-1'
	);

	protected filter$ = signal('');

	private filter$$ = toObservable(this.filter$);

	private _openAutoComplete = new ReplaySubject<void>();

	private _isOpen = signal(false);
	private nodeService = inject(CurrentNodeService);
	protected paginationSize = signal(10);
	availableNodes = this._openAutoComplete.pipe(
		debounceTime(10),
		distinctUntilChanged(),
		switchMap((_) =>
			this.filter$$.pipe(
				distinctUntilChanged(),
				debounceTime(250),
				switchMap((filter) =>
					of((pageNum: string | number) =>
						this.nodeService.getPaginatedNodesByName(
							filter,
							pageNum,
							this.paginationSize(),
							this.connectionId()
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
			this.filter$$.pipe(
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

	add(event: MatAutocompleteSelectedEvent) {
		this._innerNodes.update((nodes) => [...nodes, event.option.value]);
		this.filter$.set('');
	}

	remove(node: nodeData) {
		this._innerNodes.update((rows) =>
			rows.filter((value) => node !== value)
		);
	}

	minNodes = computed(() => {
		if (this.validTransportType()) {
			const minPub =
				this.transportType().minimumPublisherMultiplicity.value;
			const minSub =
				this.transportType().minimumSubscriberMultiplicity.value;
			const min = Math.min(minPub, minSub);
			return min;
		}
		return 0;
	});

	maxNodes = computed(() => {
		if (this.validTransportType()) {
			const maxPub =
				this.transportType().maximumPublisherMultiplicity.value;
			const maxSub =
				this.transportType().maximumSubscriberMultiplicity.value;
			const max = maxPub + maxSub;
			return max;
		}
		return 0;
	});
}
