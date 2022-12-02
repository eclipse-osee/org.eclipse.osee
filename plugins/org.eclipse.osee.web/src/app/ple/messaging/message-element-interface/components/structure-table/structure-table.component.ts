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
	AfterViewChecked,
	Component,
	Input,
	OnInit,
	ViewChild,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuTrigger } from '@angular/material/menu';
import { MatTableDataSource } from '@angular/material/table';
import {
	combineLatest,
	from,
	iif,
	Observable,
	of,
	OperatorFunction,
} from 'rxjs';
import {
	delay,
	distinctUntilChanged,
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
	tap,
} from 'rxjs/operators';
import { LayoutNotifierService } from 'src/app/layoutNotification/layout-notifier.service';
import { applic } from 'src/app/types/applicability/applic';
import { difference } from 'src/app/types/change-report/change-report';
import { EditViewFreeTextFieldDialogComponent } from '../../../shared/components/dialogs/edit-view-free-text-field-dialog/edit-view-free-text-field-dialog.component';
import { HeaderService } from '../../../shared/services/ui/header.service';
import { EditViewFreeTextDialog } from '../../../shared/types/EditViewFreeTextDialog';
import { CurrentStructureService } from '../../services/current-structure.service';
import { AddElementDialog } from '../../types/AddElementDialog.d';
import { DefaultAddElementDialog } from '../../types/AddElementDialog';
import { AddStructureDialog } from '../../types/AddStructureDialog';
import { element } from '../../../shared/types/element';
import {
	structure,
	structureWithChanges,
} from '../../../shared/types/structure';
import { AddElementDialogComponent } from '../add-element-dialog/add-element-dialog.component';
import { AddStructureDialogComponent } from '../add-structure-dialog/add-structure-dialog.component';
import { DeleteStructureDialogComponent } from '../delete-structure-dialog/delete-structure-dialog.component';
import { RemoveStructureDialogComponent } from '../remove-structure-dialog/remove-structure-dialog.component';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpLoadingService } from '../../../../../services/http-loading.service';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import {
	defaultEditElementProfile,
	defaultEditStructureProfile,
	defaultViewElementProfile,
	defaultViewStructureProfile,
} from '../../../shared/constants/defaultProfiles';

@Component({
	selector: 'osee-structure-table',
	templateUrl: './structure-table.component.html',
	styleUrls: ['./structure-table.component.sass'],
	animations: [
		trigger('detailExpand', [
			state(
				'collapsed',
				style({ maxHeight: '0vh', overflowY: 'hidden' })
			),
			state('expanded', style({ maxHeight: '60vh', overflowY: 'auto' })),
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
export class StructureTableComponent implements OnInit {
	expandedElement = this.structureService.expandedRows;
	@Input() previousLink = '../../../../';
	@Input() structureId = '';
	@Input() messageData: MatTableDataSource<structure> =
		new MatTableDataSource<structure>();
	private _expandedElementData = combineLatest([
		this._loadingService.isLoading,
		this.structureService.expandedRows,
	]).pipe(
		map(([updated, rows]) =>
			this.messageData.data.filter((s) =>
				rows.map((r) => r.id).includes(s.id)
			)
		)
	);
	@Input() hasFilter: boolean = false;
	truncatedSections: string[] = [];
	editableStructureHeaders: (keyof structure)[] = [
		'name',
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
		share(),
		shareReplay(1)
	);

	currentElementHeaders = combineLatest([
		this._currentElementHeaders,
		this.headerService.AllElementHeaders,
	]).pipe(
		map(([headers, allheaders]) =>
			headers.sort(
				(a, b) => allheaders.indexOf(a) - allheaders.indexOf(b)
			)
		)
	);

	private _currentStructureHeaders = combineLatest([
		this.headerService.AllStructureHeaders,
		this.preferences,
	]).pipe(
		switchMap(([structureHeaders, response]) =>
			of(response.columnPreferences).pipe(
				mergeMap((r) =>
					from(r).pipe(
						filter(
							(column) =>
								structureHeaders.includes(
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
		switchMap((finalHeaders) =>
			of<(keyof structure & string)[]>([' ', ...finalHeaders])
		),
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

	private _moveView = combineLatest([
		this.route.fragment,
		this._expandedElementData,
		this.structureService.expandedRowsDecreasing,
		this._loadingService.isLoading,
	]).pipe(
		skipUntil(
			this._loadingService.isLoading.pipe(
				filter((load) => load === 'true'),
				take(1)
			)
		),
		switchMap(([fragment, rows, decreasing, loading]) =>
			iif(
				() =>
					decreasing === false &&
					fragment !== null &&
					fragment !== undefined &&
					fragment.includes('a') &&
					rows !== undefined &&
					rows.length > 0 &&
					rows[rows.length - 1] !== undefined &&
					(rows[rows.length - 1].elements?.length || 0) > 0 &&
					(rows[rows.length - 1].elements
						?.map((element) => element.id)
						.some((id) => id === fragment.split('a')[1]) ||
						false) &&
					loading === 'false',
				of(fragment).pipe(
					distinctUntilChanged(),
					map((f) => document.querySelector('#' + f)),
					filter(
						(query) => query !== null && query !== undefined
					) as OperatorFunction<Element | null, Element>,
					map((query) =>
						query.scrollIntoView({ behavior: 'smooth' })
					),
					delay(1000),
					map((final) => this.router.navigate([]))
				),
				of(null)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	constructor(
		public dialog: MatDialog,
		private route: ActivatedRoute,
		private _loadingService: HttpLoadingService,
		private router: Router,
		private structureService: CurrentStructureService,
		private layoutNotifier: LayoutNotifierService,
		private headerService: HeaderService,
		private _ui: UiService
	) {}

	ngOnInit(): void {
		this._moveView.subscribe();
	}
	valueTracker(index: any, item: any) {
		return index;
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
			switchMap((value: AddElementDialog) =>
				iif(
					() =>
						value.element.id !== '-1' &&
						value.element.id.length > 0,
					this.structureService
						.relateElement(structure.id, value.element.id)
						.pipe(
							switchMap((transaction) =>
								combineLatest([
									this._ui.isLoading,
									of(transaction),
								]).pipe(
									filter(
										([loading, transaction]) =>
											loading !== 'false'
									),
									take(1),
									map(([loading, transaction]) => {
										this.router.navigate([], {
											fragment: 'a' + value.element.id,
										});
									})
								)
							)
						),
					this.structureService
						.createNewElement(
							value.element,
							structure.id,
							value.type.id as string
						)
						.pipe(
							switchMap((transaction) =>
								combineLatest([
									this._ui.isLoading,
									of(transaction),
								]).pipe(
									filter(
										([loading, transaction]) =>
											loading !== 'false'
									),
									take(1),
									map(([loading, transaction]) => {
										this.router.navigate([], {
											fragment:
												'a' +
												(transaction.results.ids[0] ||
													''),
										});
									})
								)
							)
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
}
