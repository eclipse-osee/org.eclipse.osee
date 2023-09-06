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
import {
	ChangeDetectionStrategy,
	Component,
	Inject,
	Input,
	OnDestroy,
	OnInit,
	ViewChild,
} from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatTableModule } from '@angular/material/table';
import { combineLatest, from, iif, of, OperatorFunction } from 'rxjs';
import {
	distinct,
	filter,
	first,
	map,
	mergeMap,
	reduce,
	share,
	shareReplay,
	skipUntil,
	switchMap,
	take,
	takeUntil,
	pairwise,
	debounceTime,
	scan,
	startWith,
	tap,
} from 'rxjs/operators';
import { LayoutNotifierService } from '@osee/layout/notification';
import { applic } from '@osee/shared/types/applicability';
import { difference } from '@osee/shared/types/change-report';
import { AddElementDialogComponent } from '../../dialogs/add-element-dialog/add-element-dialog.component';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DataSource } from '@angular/cdk/collections';
import { AddStructureDialog } from '../../dialogs/add-structure-dialog/add-structure-dialog';
import { AddStructureDialogComponent } from '../../dialogs/add-structure-dialog/add-structure-dialog.component';
import { DeleteStructureDialogComponent } from '../../dialogs/delete-structure-dialog/delete-structure-dialog.component';
import { RemoveStructureDialogComponent } from '../../dialogs/remove-structure-dialog/remove-structure-dialog.component';
import { DefaultAddElementDialog } from '../../dialogs/add-element-dialog/add-element-dialog.default';
import {
	AsyncPipe,
	NgClass,
	NgFor,
	NgIf,
	NgStyle,
	NgSwitch,
	NgSwitchCase,
} from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { EditStructureFieldComponent } from '../../fields/edit-structure-field/edit-structure-field.component';
import { SubElementTableComponent } from '../sub-element-table/sub-element-table.component';
import { MatButtonModule } from '@angular/material/button';
import { StructureTableLongTextFieldComponent } from '../../fields/structure-table-long-text-field/structure-table-long-text-field.component';
import type {
	structure,
	structureWithChanges,
	element,
	EditViewFreeTextDialog,
	ElementDialog,
} from '@osee/messaging/shared/types';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';
import { TwoLayerAddButtonComponent } from '@osee/shared/components';
import { CdkVirtualForOf, ScrollingModule } from '@angular/cdk/scrolling';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { EditViewFreeTextFieldDialogComponent } from '@osee/messaging/shared/dialogs/free-text';
import {
	MessagingControlsComponent,
	ViewSelectorComponent,
} from '@osee/messaging/shared/main-content';
import {
	CurrentStructureService,
	HeaderService,
} from '@osee/messaging/shared/services';
import {
	defaultEditElementProfile,
	defaultViewElementProfile,
	defaultEditStructureProfile,
	defaultViewStructureProfile,
} from '@osee/messaging/shared/constants';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { StructureDataSource } from '@osee/messaging/shared/datasources';

@Component({
	selector: 'osee-structure-table',
	templateUrl: './structure-table.component.html',
	styles: [],
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		NgIf,
		NgFor,
		NgClass,
		NgStyle,
		AsyncPipe,
		RouterLink,
		NgSwitch,
		NgSwitchCase,
		NgFor,
		MatIconModule,
		TwoLayerAddButtonComponent,
		MatFormFieldModule,
		FormsModule,
		MatInputModule,
		MatTableModule,
		MatDialogModule,
		MatTooltipModule,
		MatMenuModule,
		MatButtonModule,
		MatPaginatorModule,
		CdkVirtualForOf,
		ScrollingModule,
		EditStructureFieldComponent,
		AddStructureDialogComponent,
		MessagingControlsComponent,
		ViewSelectorComponent,
		SubElementTableComponent,
		HighlightFilteredTextDirective,
		StructureTableLongTextFieldComponent,
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
	expandedElement = this.structureService.expandedRows;
	@Input() previousLink = '../../../../';
	@Input() structureId = '';
	messageData: DataSource<structure | structureWithChanges> =
		new StructureDataSource(this.structureService);
	@Input() hasFilter: boolean = false;
	truncatedSections: string[] = [];
	editableStructureHeaders: (keyof structure)[] = [
		'name',
		'nameAbbrev',
		'description',
		'interfaceMaxSimultaneity',
		'interfaceMinSimultaneity',
		'interfaceTaskFileType',
		'interfaceStructureCategory',
		'applicability',
	];

	filter: string = '';
	searchTerms: string = '';
	@Input() breadCrumb: string = '';
	preferences = this.structureService.preferences;
	isEditing = this.preferences.pipe(
		map((x) => x.inEditMode),
		share(),
		shareReplay(1)
	);
	structures = this.structureService.structures.pipe(
		tap((structs) => {
			if (this.filter !== '') {
				structs.forEach((s) => {
					if (s.elements && s.elements.length > 0) {
						this.rowChange(s, true);
					}
				});
			}
		})
	);
	structuresCount = this.structureService.structuresCount;
	currentPage = this.structureService.currentPage;
	currentPageSize = this.structureService.currentPageSize;

	currentOffset = combineLatest([
		this.structureService.currentPage.pipe(startWith(0), pairwise()),
		this.structureService.currentPageSize,
	]).pipe(
		debounceTime(100),
		scan((acc, [[previousPageNum, currentPageNum], currentSize]) => {
			if (previousPageNum < currentPageNum) {
				return (acc += currentSize);
			} else {
				return acc;
			}
		}, 10)
	);

	minPageSize = combineLatest([
		this.currentOffset,
		this.structuresCount,
	]).pipe(
		debounceTime(100),
		switchMap(([offset, messages]) => of([offset, messages])),
		map(([offset, length]) => Math.max(offset + 1, length + 1))
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
									column.name as keyof element
								) && column.enabled
						),
						distinct((r) => r.name),
						map((header) => header.name as keyof element),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as (keyof element)[]
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
				this.isEditing.pipe(
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
		switchMap((finalHeaders) => of(['rowControls', ...finalHeaders])),
		share(),
		shareReplay(1)
	);

	currentElementHeaders = combineLatest([
		this._currentElementHeaders,
		this.headerService.AllElementHeaders,
	]).pipe(
		map(([headers, allheaders]) =>
			headers.sort(
				(a, b) =>
					allheaders.indexOf(a as keyof element) -
					allheaders.indexOf(b as keyof element)
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
									column.name as Extract<
										keyof structure,
										string
									>
								) && column.enabled
						),
						distinct((r) => r.name),
						map(
							(header) =>
								header.name as Extract<keyof structure, string>
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as Extract<keyof structure, string>[]
						)
					)
				)
			)
		),
		mergeMap((headers) =>
			iif(
				() => headers.length !== 0,
				of(headers),
				this.isEditing.pipe(
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
		switchMap((finalHeaders) => of([' ', ...finalHeaders])),
		share(),
		shareReplay(1)
	);

	currentStructureHeaders = combineLatest([
		this._currentStructureHeaders,
		this.headerService.AllStructureHeaders,
	]).pipe(
		map(([headers, allheaders]) =>
			headers.sort(
				(a, b) => allheaders.indexOf(a) - allheaders.indexOf(b)
			)
		)
	);

	_connectionsRoute = this.structureService.connectionsRoute;
	_messageData = this.structureService.message.pipe(
		takeUntil(this.structureService.done)
	);
	structureDialog = this.structureService.SubMessageId.pipe(
		take(1),
		switchMap((submessage) =>
			this.dialog
				.open(AddStructureDialogComponent, {
					minWidth: '80%',
					data: {
						id: submessage,
						name: this.breadCrumb,
						structure: {
							id: '-1',
							name: '',
							elements: [],
							description: '',
							interfaceMaxSimultaneity: '',
							interfaceMinSimultaneity: '',
							interfaceStructureCategory: '',
							interfaceTaskFileType: 0,
							applicability: {
								id: '1',
								name: 'Base',
							},
						},
					},
				})
				.afterClosed()
				.pipe(
					take(1),
					filter((val) => val !== undefined),
					switchMap((value: AddStructureDialog) =>
						iif(
							() =>
								value.structure.id !== '-1' &&
								value.structure.id.length > 0,
							this.structureService.relateStructure(
								value.structure.id
							),
							this.structureService.createStructure(
								value.structure
							)
						)
					)
				)
		),
		first()
	);
	layout = this.layoutNotifier.layout;
	menuPosition = {
		x: '0',
		y: '0',
	};
	@ViewChild(MatMenuTrigger, { static: true })
	matMenuTrigger!: MatMenuTrigger;
	sideNav = this.structureService.sideNavContent;
	sideNavOpened = this.sideNav.pipe(map((value) => value.opened));
	inDiffMode = this.structureService.isInDiff.pipe(
		switchMap((val) => iif(() => val, of('true'), of('false')))
	);

	textExpanded: boolean = false;
	toggleExpanded() {
		this.textExpanded = !this.textExpanded;
	}

	constructor(
		public dialog: MatDialog,
		private route: ActivatedRoute,
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structureService: CurrentStructureService,
		private layoutNotifier: LayoutNotifierService,
		private headerService: HeaderService
	) {}

	ngOnDestroy(): void {
		this.structureService.filter = '';
	}

	valueTracker(index: any, item: any) {
		return index;
	}

	structureTracker(index: number, item: structure | structureWithChanges) {
		return item.id !== '-1' ? item.id : index.toString();
	}
	openAddElementDialog(structure: structure | structureWithChanges) {
		const dialogData = new DefaultAddElementDialog(
			structure?.id || '',
			structure?.name || ''
		);
		let dialogRef = this.dialog.open(AddElementDialogComponent, {
			data: dialogData,
			minWidth: '80%',
		});
		let createElement = dialogRef.afterClosed().pipe(
			filter(
				(val) =>
					(val !== undefined || val !== null) &&
					val?.element !== undefined
			),
			switchMap((value: ElementDialog) =>
				iif(
					() =>
						value.element.id !== undefined &&
						value.element.id !== '-1' &&
						value.element.id.length > 0,
					this.structureService.relateElement(
						structure.id,
						value.element.id !== undefined ? value.element.id : '-1'
					),
					this.structureService.createNewElement(
						value.element,
						structure.id,
						value.type.id as string
					)
				)
			),
			take(1)
		);
		createElement.subscribe();
	}

	rowIsExpanded(value: string) {
		return this.structureService.expandedRows.pipe(
			map((rows) => rows.map((s) => s.id).includes(value))
		);
	}

	expandRow(value: structure | structureWithChanges) {
		this.structureService.addExpandedRow = value;
	}
	hideRow(value: structure | structureWithChanges) {
		this.structureService.removeExpandedRow = value;
	}

	rowChange(value: structure | structureWithChanges, type: boolean) {
		if (type) {
			this.expandRow(value);
		} else {
			this.hideRow(value);
		}
	}

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.searchTerms = filterValue;
		this.filter = filterValue.trim().toLowerCase();
		this.structureService.filter = (event.target as HTMLInputElement).value;
	}
	isTruncated(value: string) {
		if (this.truncatedSections.find((x) => x === value)) {
			return true;
		}
		return false;
	}

	openAddStructureDialog() {
		this.structureDialog.subscribe();
	}

	openMenu(
		event: MouseEvent,
		id: string,
		name: string,
		description: string,
		structure: structure,
		header: keyof structure,
		diff: string
	) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		this.matMenuTrigger.menuData = {
			id: id,
			name: name,
			description: description,
			structure: structure,
			header: header,
			diffMode: diff === 'true',
			url:
				this.route.snapshot.pathFromRoot
					.map((r) => r.url)
					.join()
					.replace(/(diff)/g, '')
					.replace(/,/g, '/')
					.replace(/\/\//g, '') +
				'/' +
				id +
				(diff === 'true' ? '/diff' : ''),
		};
		this.matMenuTrigger.openMenu();
	}

	removeStructureDialog(id: string, name: string) {
		this.structureService.SubMessageId.pipe(
			take(1),
			switchMap((subMessageId) =>
				this.dialog
					.open(RemoveStructureDialogComponent, {
						data: {
							subMessageId: subMessageId,
							structureId: id,
							structureName: name,
						},
					})
					.afterClosed()
					.pipe(
						take(1),
						switchMap((dialogResult: string) =>
							iif(
								() => dialogResult === 'ok',
								this.structureService.removeStructureFromSubmessage(
									id,
									subMessageId
								),
								of()
							)
						)
					)
			)
		).subscribe();
	}

	deleteStructureDialog(id: string, name: string) {
		this.dialog
			.open(DeleteStructureDialogComponent, {
				data: {
					structureId: id,
					structureName: name,
				},
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((dialogResult: string) =>
					iif(
						() => dialogResult === 'ok',
						this.structureService.deleteStructure(id),
						of()
					)
				)
			)
			.subscribe();
	}

	insertStructure(afterStructure?: string) {
		this.dialog
			.open(AddStructureDialogComponent, {
				data: {
					id: this.structureService.subMessageId,
					name: this.breadCrumb,
					structure: {
						id: '-1',
						name: '',
						elements: [],
						description: '',
						interfaceMaxSimultaneity: '',
						interfaceMinSimultaneity: '',
						interfaceStructureCategory: '',
						interfaceTaskFileType: 0,
					},
				},
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((val) => val !== undefined),
				switchMap((value: AddStructureDialog) =>
					iif(
						() =>
							value.structure.id !== '-1' &&
							value.structure.id.length > 0,
						this.structureService.relateStructure(
							value.structure.id,
							afterStructure
						),
						this.structureService.createStructure(
							value.structure,
							afterStructure
						)
					)
				)
			)
			.subscribe();
	}

	copyStructure(
		structure: structure | structureWithChanges,
		afterStructure?: string
	) {
		this.dialog
			.open(AddStructureDialogComponent, {
				data: {
					id: this.structureService.subMessageId,
					name: this.breadCrumb,
					structure: structuredClone(structure),
				},
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((val) => val !== undefined),
				switchMap((result) =>
					this.structureService.copyStructure(
						result.structure,
						afterStructure
					)
				)
			)
			.subscribe();
	}
	openDescriptionDialog(description: string, structureId: string) {
		this.dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: JSON.parse(JSON.stringify(description)) as string,
					type: 'Description',
					return: description,
				},
				minHeight: '60%',
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((response: EditViewFreeTextDialog | string) =>
					iif(
						() =>
							response === 'ok' ||
							response === 'cancel' ||
							response === undefined,
						//do nothing
						of(),
						//change description
						this.structureService.partialUpdateStructure({
							id: structureId,
							description: (response as EditViewFreeTextDialog)
								.return,
						})
					)
				)
			)
			.subscribe();
	}

	getHeaderByName(value: keyof structure) {
		return this.headerService.getHeaderByName(value, 'structure');
	}

	viewDiff(open: boolean, value: difference, header: string) {
		this.structureService.sideNav = {
			opened: open,
			field: header,
			currentValue: value.currentValue as string | number | applic,
			previousValue: value.previousValue as
				| string
				| number
				| applic
				| undefined,
			transaction: value.transactionToken,
		};
	}
	setPage(event: PageEvent) {
		this.structureService.pageSize = event.pageSize;
		this.structureService.page = event.pageIndex;
	}
}
