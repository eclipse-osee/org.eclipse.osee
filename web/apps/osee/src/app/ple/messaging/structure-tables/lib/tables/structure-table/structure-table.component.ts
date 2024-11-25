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
	animate,
	state,
	style,
	transition,
	trigger,
} from '@angular/animations';
import { CdkVirtualForOf } from '@angular/cdk/scrolling';
import { AsyncPipe, NgClass, NgStyle } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	OnDestroy,
	computed,
	inject,
	input,
	signal,
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PersistedApplicabilityDropdownComponent } from '@osee/applicability/persisted-applicability-dropdown';
import { applicabilitySentinel } from '@osee/applicability/types';
import { PersistedStringAttributeInputComponent } from '@osee/attributes/persisted-string-attribute-input';
import { LayoutNotifierService } from '@osee/layout/notification';
import {
	defaultEditElementProfile,
	defaultEditStructureProfile,
	defaultViewElementProfile,
	defaultViewStructureProfile,
} from '@osee/messaging/shared/constants';
import { HeaderService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import type {
	DisplayableElementProps,
	displayableStructureFields,
	structure,
	structureWithChanges,
} from '@osee/messaging/shared/types';
import { PersistedStructureCategoryDropdownComponent } from '@osee/messaging/structure-category/persisted-structure-category-dropdown';
import { writableSlice } from '@osee/shared/utils';
import { combineLatest, from, iif, of } from 'rxjs';
import {
	distinct,
	filter,
	map,
	mergeMap,
	reduce,
	share,
	shareReplay,
	switchMap,
	tap,
} from 'rxjs/operators';
import { StructureTableLongTextFieldComponent } from '../../fields/structure-table-long-text-field/structure-table-long-text-field.component';
import { StructureTableNoEditFieldComponent } from '../../fields/structure-table-no-edit-field/structure-table-no-edit-field.component';
import { StructureMenuComponent } from '../../menus/structure-menu/structure-menu.component';
import { StructureImpactsValidatorDirective } from '../../structure-impacts-validator.directive';
import { SubElementTableComponent } from '../sub-element-table/sub-element-table.component';

@Component({
	selector: 'osee-structure-table',
	templateUrl: './structure-table.component.html',
	styles: [],
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		NgClass,
		NgStyle,
		AsyncPipe,
		RouterLink,
		FormsModule,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatTooltip,
		MatCell,
		MatCellDef,
		MatIconButton,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		CdkVirtualForOf,
		PersistedApplicabilityDropdownComponent,
		PersistedStringAttributeInputComponent,
		PersistedStructureCategoryDropdownComponent,
		SubElementTableComponent,
		StructureTableLongTextFieldComponent,
		StructureMenuComponent,
		StructureTableNoEditFieldComponent,
		StructureImpactsValidatorDirective,
	],
	animations: [
		trigger('detailExpand', [
			state('collapsed', style({ maxHeight: '0vh' })),
			state('expanded', style({ maxHeight: '60vh' })),
			transition(
				'expanded <=> collapsed',
				animate('225ms cubic-bezier(0.42, 0.0, 0.58, 1)')
			),
		]),
		trigger('expandButton', [
			state('closed', style({ transform: 'rotate(0)' })),
			state('open', style({ transform: 'rotate(-180deg)' })),
			transition(
				'open <=> closed',
				animate('225ms cubic-bezier(0.42, 0.0, 0.58, 1)')
			),
		]),
	],
})
export class StructureTableComponent implements OnDestroy {
	dialog = inject(MatDialog);
	private route = inject(ActivatedRoute);
	private structureService = inject(STRUCTURE_SERVICE_TOKEN);
	private layoutNotifier = inject(LayoutNotifierService);
	private headerService = inject(HeaderService);

	protected expandedElement = this.structureService.expandedRows;
	previousLink = input('../../../../');
	protected structureFilter = this.structureService.structureFilter;

	protected editableStructureHeaders: (
		| keyof structure
		| ' '
		| 'txRate'
		| 'publisher'
		| 'subscriber'
		| 'messageNumber'
		| 'messagePeriodicity'
	)[] = [
		'name',
		'nameAbbrev',
		'description',
		'interfaceMaxSimultaneity',
		'interfaceMinSimultaneity',
		'interfaceTaskFileType',
		'interfaceStructureCategory',
		'applicability',
	];
	protected headerIsEditable(
		header:
			| keyof structure
			| ' '
			| 'txRate'
			| 'publisher'
			| 'subscriber'
			| 'messageNumber'
			| 'messagePeriodicity'
	): header is
		| 'name'
		| 'nameAbbrev'
		| 'description'
		| 'interfaceMaxSimultaneity'
		| 'interfaceMinSimultaneity'
		| 'interfaceTaskFileType'
		| 'interfaceStructureCategory'
		| 'applicability' {
		return this.editableStructureHeaders.includes(header);
	}

	breadCrumb = input('');
	protected preferences = this.structureService.preferences;
	private _isEditing$ = this.preferences.pipe(
		map((x) => x.inEditMode),
		share(),
		shareReplay(1)
	);
	protected isEditing = toSignal(this._isEditing$, { initialValue: false });

	protected structures = toSignal(
		this.structureService.structures.pipe(
			tap((structs) => {
				if (this.structureFilter() !== '') {
					structs.forEach((s) => {
						if (
							s.elements &&
							s.elements.length > 0 &&
							!this.rowIsExpanded(s.id)
						) {
							this.rowChange(s, true);
						}
					});
				}
			})
		),
		{ initialValue: [] }
	);

	tableFieldsEditMode = computed(
		() =>
			this.isEditing() &&
			this.structures()
				.filter((s) =>
					this.structureService
						.expandedRows()
						.find((str) => str.id === s.id)
				)
				.map((s) => s.elements.length)
				.reduce((prev, curr) => prev + curr, 0) < 300
	);

	private _currentElementHeaders = combineLatest([
		this.headerService.AllElementHeaders,
		this.preferences,
	]).pipe(
		switchMap(([allHeaders, response]) =>
			of(response.columnPreferences).pipe(
				mergeMap((r) =>
					from(r).pipe(
						filter(
							(column) =>
								allHeaders.includes(
									column.name as keyof DisplayableElementProps
								) && column.enabled
						),
						distinct((r) => r.name),
						map(
							(header) =>
								header.name as keyof DisplayableElementProps
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as (keyof DisplayableElementProps)[]
						)
					)
				)
			)
		),
		mergeMap((headers) =>
			iif(
				() => headers.length !== 0,
				of(headers).pipe(
					map((array) => {
						array.push(
							array.splice(array.indexOf('applicability'), 1)[0]
						);
						return array;
					})
				),
				this._isEditing$.pipe(
					switchMap((editing) =>
						iif(
							() => editing,
							of(defaultEditElementProfile),
							of(defaultViewElementProfile)
						)
					)
				)
			)
		),
		switchMap((finalHeaders) =>
			of(['rowControls' as const, ...finalHeaders])
		),
		share(),
		shareReplay(1)
	);

	protected currentElementHeaders = combineLatest([
		this._currentElementHeaders,
		this.headerService.AllElementHeaders,
	]).pipe(
		map(([headers, allheaders]) =>
			headers.sort(
				(a, b) =>
					allheaders.indexOf(a as keyof DisplayableElementProps) -
					allheaders.indexOf(b as keyof DisplayableElementProps)
			)
		)
	);

	private _currentStructureHeaders = combineLatest([
		this.headerService.AllStructureHeaders,
		this.preferences,
	]).pipe(
		switchMap(([allHeaders, response]) =>
			of(response.columnPreferences).pipe(
				mergeMap((r) =>
					from(r).pipe(
						filter(
							(column) =>
								allHeaders.includes(
									column.name as keyof displayableStructureFields &
										' ' &
										'txRate' &
										'publisher' &
										'subscriber' &
										'messageNumber' &
										'messagePeriodicity'
								) && column.enabled
						),
						distinct((r) => r.name),
						map(
							(header) =>
								header.name as keyof displayableStructureFields &
									' ' &
									'txRate' &
									'publisher' &
									'subscriber' &
									'messageNumber' &
									'messagePeriodicity'
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as (keyof displayableStructureFields &
								' ' &
								'txRate' &
								'publisher' &
								'subscriber' &
								'messageNumber' &
								'messagePeriodicity')[]
						)
					)
				)
			)
		),
		mergeMap((headers) =>
			iif(
				() => headers.length !== 0,
				of(headers),
				this._isEditing$.pipe(
					switchMap((editing) =>
						iif(
							() => editing,
							of(defaultEditStructureProfile),
							of(defaultViewStructureProfile)
						)
					)
				)
			)
		),
		switchMap((finalHeaders) => of([' ' as const, ...finalHeaders])),
		share(),
		shareReplay(1)
	);

	protected currentStructureHeaders = combineLatest([
		this._currentStructureHeaders,
		this.headerService.AllStructureHeaders,
	]).pipe(
		map(([headers, allheaders]) =>
			headers.sort(
				(a, b) => allheaders.indexOf(a) - allheaders.indexOf(b)
			)
		)
	);

	protected message = toSignal(
		this.structureService.message.pipe(takeUntilDestroyed()),
		{
			initialValue: {
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
				subMessages: [],
				interfaceMessageRate: {
					id: '-1',
					typeId: '2455059983007225763',
					gammaId: '-1',
					value: '',
				},
				interfaceMessagePeriodicity: {
					id: '-1',
					typeId: '3899709087455064789',
					gammaId: '-1',
					value: '',
				},
				interfaceMessageWriteAccess: {
					id: '-1',
					typeId: '2455059983007225754',
					gammaId: '-1',
					value: false,
				},
				interfaceMessageType: {
					id: '-1',
					typeId: '2455059983007225770',
					gammaId: '-1',
					value: '',
				},
				interfaceMessageNumber: {
					id: '-1',
					typeId: '2455059983007225768',
					gammaId: '-1',
					value: '',
				},
				interfaceMessageExclude: {
					id: '-1',
					typeId: '2455059983007225811',
					gammaId: '-1',
					value: false,
				},
				interfaceMessageIoMode: {
					id: '-1',
					typeId: '2455059983007225813',
					gammaId: '-1',
					value: '',
				},
				interfaceMessageModeCode: {
					id: '-1',
					typeId: '2455059983007225810',
					gammaId: '-1',
					value: '',
				},
				interfaceMessageRateVer: {
					id: '-1',
					typeId: '2455059983007225805',
					gammaId: '-1',
					value: '',
				},
				interfaceMessagePriority: {
					id: '-1',
					typeId: '2455059983007225806',
					gammaId: '-1',
					value: '',
				},
				interfaceMessageProtocol: {
					id: '-1',
					typeId: '2455059983007225809',
					gammaId: '-1',
					value: '',
				},
				interfaceMessageRptWordCount: {
					id: '-1',
					typeId: '2455059983007225807',
					gammaId: '-1',
					value: '',
				},
				interfaceMessageRptCmdWord: {
					id: '-1',
					typeId: '2455059983007225808',
					gammaId: '-1',
					value: '',
				},
				interfaceMessageRunBeforeProc: {
					id: '-1',
					typeId: '2455059983007225812',
					gammaId: '-1',
					value: false,
				},
				interfaceMessageVer: {
					id: '-1',
					typeId: '2455059983007225804',
					gammaId: '-1',
					value: '',
				},
				applicability: applicabilitySentinel,
				publisherNodes: [],
				subscriberNodes: [],
			},
		}
	);
	protected layout = this.layoutNotifier.layout;
	protected menuMetaData = signal<{
		x: string;
		y: string;
		structure: structure;
		url: string;
		header:
			| keyof displayableStructureFields
			| ' '
			| 'txRate'
			| 'publisher'
			| 'subscriber'
			| 'messageNumber'
			| 'messagePeriodicity';
		isInDiff: boolean;
		open: boolean;
	}>({
		x: '0',
		y: '0',
		url: '',
		open: false,
		header: ' ',
		isInDiff: false,
		structure: {
			id: '-1',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: '',
			},
			nameAbbrev: {
				id: '-1',
				typeId: '8355308043647703563',
				gammaId: '-1',
				value: '',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			interfaceMaxSimultaneity: {
				id: '-1',
				typeId: '2455059983007225756',
				gammaId: '-1',
				value: '',
			},
			interfaceMinSimultaneity: {
				id: '-1',
				typeId: '2455059983007225755',
				gammaId: '-1',
				value: '',
			},
			interfaceTaskFileType: {
				id: '-1',
				typeId: '2455059983007225760',
				gammaId: '-1',
				value: 0,
			},
			interfaceStructureCategory: {
				id: '-1',
				typeId: '2455059983007225764',
				gammaId: '-1',
				value: '',
			},
			applicability: applicabilitySentinel,
			elements: [],
		},
	});
	protected menuPositionX = writableSlice(this.menuMetaData, 'x');
	protected menuPositionY = writableSlice(this.menuMetaData, 'y');
	protected url = writableSlice(this.menuMetaData, 'url');
	protected contextStructure = writableSlice(this.menuMetaData, 'structure');
	protected menuHeader = writableSlice(this.menuMetaData, 'header');
	protected menuDiff = writableSlice(this.menuMetaData, 'isInDiff');
	protected menuOpen = writableSlice(this.menuMetaData, 'open');
	protected sideNav = this.structureService.sideNavContent;
	protected sideNavOpened = this.sideNav.pipe(map((value) => value.opened));
	protected inDiffMode = this.structureService.isInDiff.pipe(
		switchMap((val) => iif(() => val, of('true'), of('false')))
	);

	ngOnDestroy(): void {
		this.structureFilter.set('');
	}

	protected valueTracker(index: number, _item: unknown) {
		return index;
	}

	protected structureTracker(
		index: number,
		item: structure | structureWithChanges
	) {
		return item.id !== '-1'
			? item.id +
					item.elements
						.map(
							(x) =>
								x.id +
								x.arrayElements.map((y) => y.id).join('::')
						)
						.join(':')
			: index.toString();
	}

	protected rowIsExpanded(value: `${number}`) {
		return this.structureService
			.expandedRows()
			.map((s) => s.id)
			.includes(value);
	}

	protected expandRow(value: structure | structureWithChanges) {
		this.structureService.addExpandedRow = value;
	}
	protected hideRow(value: structure | structureWithChanges) {
		this.structureService.removeExpandedRow = value;
	}

	protected rowChange(
		value: structure | structureWithChanges,
		type: boolean
	) {
		if (type) {
			this.expandRow(value);
		} else {
			this.hideRow(value);
		}
	}

	protected openMenu(
		event: MouseEvent,
		id: string,
		_name: string,
		_description: string,
		structure: structure,
		header:
			| keyof displayableStructureFields
			| ' '
			| 'txRate'
			| 'publisher'
			| 'subscriber'
			| 'messageNumber'
			| 'messagePeriodicity',
		diff: string
	) {
		event.preventDefault();
		this.menuPositionX.set(event.clientX + 'px');
		this.menuPositionY.set(event.clientY + 'px');
		this.url.set(
			this.route.snapshot.pathFromRoot
				.map((r) => r.url)
				.join()
				.replace(/(diff)/g, '')
				.replace(/,/g, '/')
				.replace(/\/\//g, '') +
				'/' +
				id +
				(diff === 'true' ? '/diff' : '')
		);
		this.contextStructure.set(structure);
		this.menuHeader.set(header);
		this.menuDiff.set(diff === 'true');
		this.menuOpen.set(true);
	}
	protected getHeaderByName(
		value:
			| keyof structure
			| ' '
			| 'txRate'
			| 'publisher'
			| 'subscriber'
			| 'messageNumber'
			| 'messagePeriodicity'
	) {
		return this.headerService.getHeaderByName(value, 'structure');
	}
}
