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
import { LayoutNotifierService } from '../../../../../../layout/lib/notification/layout-notifier.service';
import { AddElementDialogComponent } from '../../dialogs/add-element-dialog/add-element-dialog.component';
import { applic } from '@osee/shared/types/applicability';
import { difference } from 'src/app/shared/types/change-report/change-report';
import { UiService } from '../../../../../../ple-services/ui/ui.service';
import { AddElementDialog } from '../../dialogs/add-element-dialog/add-element-dialog';
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
import {
	STRUCTURE_SERVICE_TOKEN,
	EditEnumSetDialogComponent,
	EditViewFreeTextFieldDialogComponent,
	CurrentStructureService,
	EnumerationUIService,
	HeaderService,
	PreferencesUIService,
} from '@osee/messaging/shared';
import type {
	structure,
	enumerationSet,
	element,
	EditViewFreeTextDialog,
} from '@osee/messaging/shared';

@Component({
	selector: 'osee-messaging-message-element-interface-sub-element-table',
	templateUrl: './sub-element-table.component.html',
	styleUrls: ['./sub-element-table.component.sass'],
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
		private preferencesService: PreferencesUIService
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
		const afterArtifactId = newIndex < 1 ? 'start' : tableData[newIndex].id;

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
			switchMap((value: AddElementDialog) =>
				iif(
					() =>
						value.element.id !== '-1' &&
						value.element.id.length > 0,
					this.structureService
						.relateElement(
							structure.id,
							value.element.id,
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
					enumerationSet | undefined,
					enumerationSet
				>,
				take(1),
				switchMap(({ enumerations, ...changes }) =>
					iif(
						() => this.editMode,
						this.enumSetService.changeEnumSet(
							changes,
							enumerations
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
