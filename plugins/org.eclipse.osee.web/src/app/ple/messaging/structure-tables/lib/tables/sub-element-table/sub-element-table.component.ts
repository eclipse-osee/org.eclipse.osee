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
	Component,
	EventEmitter,
	Inject,
	Input,
	OnChanges,
	OnInit,
	Output,
	SimpleChanges,
	ViewChild,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CdkDragDrop, CdkDropList } from '@angular/cdk/drag-drop';
import { combineLatest, iif, of, OperatorFunction } from 'rxjs';
import { filter, map, switchMap, take } from 'rxjs/operators';
import { LayoutNotifierService } from '@osee/layout/notification';
import { AddElementDialogComponent } from '../../dialogs/add-element-dialog/add-element-dialog.component';
import { applic } from '@osee/shared/types/applicability';
import { difference } from '@osee/shared/types/change-report';
import { UiService } from '@osee/shared/services';
import { DefaultAddElementDialog } from '../../dialogs/add-element-dialog/add-element-dialog.default';
import { RemoveElementDialogData } from '../../dialogs/remove-element-dialog/remove-element-dialog';
import { RemoveElementDialogComponent } from '../../dialogs/remove-element-dialog/remove-element-dialog.component';
import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { SubElementTableFieldComponent } from '../../fields/sub-element-table-field/sub-element-table-field.component';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { SubElementTableDropdownComponent } from '../../menus/sub-element-table-dropdown/sub-element-table-dropdown.component';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import type {
	structure,
	element,
	EditViewFreeTextDialog,
	ElementDialog,
} from '@osee/messaging/shared/types';
import { EditEnumSetDialogComponent } from '@osee/messaging/shared/dialogs';
import {
	CurrentStructureService,
	EnumerationUIService,
	HeaderService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import { EditViewFreeTextFieldDialogComponent } from '@osee/messaging/shared/dialogs/free-text';
import {
	createArtifact,
	modifyArtifact,
	modifyRelation,
	relation,
} from '@osee/shared/types';

@Component({
	selector: 'osee-messaging-message-element-interface-sub-element-table',
	templateUrl: './sub-element-table.component.html',
	styles: [
		':host {display: block;width: 100%;max-width: 100vw;overflow-x: auto;max-height: 10%;}',
	],
	standalone: true,
	imports: [
		NgFor,
		NgIf,
		NgClass,
		AsyncPipe,
		RouterLink,
		CdkDropList,
		MatTableModule,
		MatIconModule,
		MatMenuModule,
		MatTooltipModule,
		MatFormFieldModule,
		FormsModule,
		SubElementTableFieldComponent,
		SubElementTableDropdownComponent,
	],
})
export class SubElementTableComponent implements OnInit, OnChanges {
	@Input() data: any = {};
	@Input() dataSource: MatTableDataSource<any> =
		new MatTableDataSource<any>();
	@Input() filter: string = '';

	@Input() structure: structure = {
		id: '',
		name: '',
		nameAbbrev: '',
		description: '',
		interfaceMaxSimultaneity: '',
		interfaceMinSimultaneity: '',
		interfaceTaskFileType: 0,
		interfaceStructureCategory: '',
	};
	@Output() expandRow = new EventEmitter();
	@Input() subMessageHeaders: any;
	_branchId: string = '';
	_branchType: string = '';
	@Input() editMode: boolean = false;
	layout = this.layoutNotifier.layout;
	menuPosition = {
		x: '0',
		y: '0',
	};

	@ViewChild('generalMenuTrigger', { static: true })
	generalMenuTrigger!: MatMenuTrigger;
	constructor(
		private route: ActivatedRoute,
		private _ui: UiService,
		private router: Router,
		public dialog: MatDialog,
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structureService: CurrentStructureService,
		private layoutNotifier: LayoutNotifierService,
		private headerService: HeaderService,
		private enumSetService: EnumerationUIService,
		private warningDialogService: WarningDialogService
	) {
		this.subMessageHeaders = [
			'name',
			'beginWord',
			'endWord',
			'BeginByte',
			'EndByte',
			'interfaceElementAlterable',
			'description',
			'notes',
		];
		this.dataSource.data = this.data;
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (Array.isArray(this.data)) {
			this.dataSource.data = this.data;
		}
		if (this.filter !== '') {
			this.dataSource.filter = this.filter.replace('element: ', '');
			this.filter = this.filter.replace('element: ', '');
			if (this.dataSource.filteredData.length > 0) {
				this.expandRow.emit(this.structure);
			}
		}
	}

	ngOnInit(): void {
		if (Array.isArray(this.data)) {
			this.dataSource.data = this.data;
		}
		if (this.filter !== '') {
			this.dataSource.filter = this.filter.replace('element: ', '');
		}

		this.route.paramMap.subscribe((values) => {
			this._branchId = values.get('branchId') || '';
			this._branchType = values.get('branchType') || '';
		});
	}

	valueTracker(index: any, item: any) {
		return index;
	}

	handleDragDrop(event: CdkDragDrop<unknown[]>) {
		if (event.currentIndex === event.previousIndex) {
			return;
		}

		// Rows not marked as draggable are not included in the index count,
		// so remove them from the list before checking index.
		const tableData = this.dataSource.data.filter((e) => e.id !== '-1');
		const elementId = tableData[event.previousIndex].id;
		tableData.splice(event.previousIndex, 1);
		const newIndex = event.currentIndex - 1;
		const afterArtifactId = newIndex < 0 ? 'start' : tableData[newIndex].id;

		this.structureService
			.changeElementRelationOrder(
				this.structure.id,
				elementId,
				afterArtifactId
			)
			.subscribe();
	}

	openGeneralMenu(
		event: MouseEvent,
		element: element,
		header: string,
		field?: string | number | boolean | applic
	) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		this.generalMenuTrigger.menuData = {
			element: element,
			structure: this.structure,
			field: field,
			header: header,
		};
		this.generalMenuTrigger.openMenu();
	}

	removeElement(element: element, structure: structure) {
		const dialogData: RemoveElementDialogData = {
			elementId: element.id,
			structureId: structure.id,
			elementName: element.name,
		};
		this.dialog
			.open(RemoveElementDialogComponent, {
				data: dialogData,
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((dialogResult: string) =>
					iif(
						() => dialogResult === 'ok',
						this.structureService.removeElementFromStructure(
							element,
							structure
						),
						of()
					)
				)
			)
			.subscribe();
	}
	deleteElement(element: element) {
		//open dialog, yes/no if yes -> this.structures.deleteElement()
		const dialogData: RemoveElementDialogData = {
			elementId: element.id,
			structureId: '',
			elementName: element.name,
		};
		this.dialog
			.open(RemoveElementDialogComponent, {
				data: dialogData,
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((dialogResult: string) =>
					iif(
						() => dialogResult === 'ok',
						this.structureService.deleteElement(element),
						of()
					)
				)
			)
			.subscribe();
	}
	openAddElementDialog(structure: structure, afterElement?: string) {
		const dialogData = new DefaultAddElementDialog(
			structure?.id || '',
			structure?.name || ''
		);
		let dialogRef = this.dialog.open(AddElementDialogComponent, {
			data: dialogData,
		});
		let createElement = dialogRef.afterClosed().pipe(
			take(1),
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
					this.structureService
						.relateElement(
							structure.id,
							value.element.id !== undefined
								? value.element.id
								: '-1',
							afterElement || 'end'
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
							value.type.id as string,
							afterElement || 'end'
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
													afterElement ||
													''),
										});
									})
								)
							)
						)
				)
			)
		);
		createElement.subscribe();
	}
	openEnumDialog(id: string) {
		this.dialog
			.open(EditEnumSetDialogComponent, {
				data: {
					id: id,
					isOnEditablePage: this.editMode,
				},
			})
			.afterClosed()
			.pipe(
				filter((x) => x !== undefined) as OperatorFunction<
					| {
							createArtifacts: createArtifact[];
							modifyArtifacts: modifyArtifact[];
							deleteRelations: modifyRelation[];
					  }
					| undefined,
					{
						createArtifacts: createArtifact[];
						modifyArtifacts: modifyArtifact[];
						deleteRelations: modifyRelation[];
					}
				>,
				take(1),
				switchMap((tx) =>
					iif(
						() => this.editMode,
						this.warningDialogService
							.openEnumsDialogs(
								tx.modifyArtifacts
									.slice(0, -1)
									.map((v) => v.id),
								[
									...tx.createArtifacts
										.flatMap((v) => v.relations)
										.filter(
											(v): v is relation =>
												v !== undefined
										)
										.map((v) => v.sideA)
										.filter(
											(v): v is string | string[] =>
												v !== undefined
										)
										.flatMap((v) => v),
									...tx.deleteRelations
										.flatMap((v) => v.aArtId)
										.filter(
											(v): v is string => v !== undefined
										),
								]
							)
							.pipe(
								switchMap((_) =>
									this.enumSetService.changeEnumSet(tx)
								)
							),
						of()
					)
				)
			)
			.subscribe();
	}

	openDescriptionDialog(
		description: string,
		elementId: string,
		structureId: string
	) {
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
						this.structureService.partialUpdateElement(
							{
								id: elementId,
								description: (
									response as EditViewFreeTextDialog
								).return,
							},
							this.structure.id
						)
					)
				)
			)
			.subscribe();
	}

	openNotesDialog(notes: string, elementId: string, structureId: string) {
		this.dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: JSON.parse(JSON.stringify(notes)) as string,
					type: 'Notes',
					return: notes,
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
						//change notes
						this.structureService.partialUpdateElement(
							{
								id: elementId,
								notes: (response as EditViewFreeTextDialog)
									.return,
							},
							this.structure.id
						)
					)
				)
			)
			.subscribe();
	}

	getHeaderByName(value: string) {
		return this.headerService.getHeaderByName(value, 'element');
	}

	viewDiff(value: difference, header: string) {
		this.structureService.sideNav = {
			opened: true,
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
