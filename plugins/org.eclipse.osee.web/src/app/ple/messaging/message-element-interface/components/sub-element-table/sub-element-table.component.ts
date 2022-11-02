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
	Input,
	OnChanges,
	OnInit,
	Output,
	SimpleChanges,
	ViewChild,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuTrigger } from '@angular/material/menu';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, iif, of, OperatorFunction } from 'rxjs';
import { filter, map, switchMap, take } from 'rxjs/operators';
import { LayoutNotifierService } from 'src/app/layoutNotification/layout-notifier.service';
import { enumerationSet } from '../../../shared/types/enum';
import { EditEnumSetDialogComponent } from '../../../shared/components/dialogs/edit-enum-set-dialog/edit-enum-set-dialog.component';
import { CurrentStructureService } from '../../services/current-structure.service';
import { AddElementDialog } from '../../types/AddElementDialog.d';
import { DefaultAddElementDialog } from '../../types/AddElementDialog';
import { element } from '../../../shared/types/element.d';
import { RemoveElementDialogData } from '../../types/RemoveElementDialog';
import {
	structure,
	structureWithChanges,
} from '../../../shared/types/structure.d';
import { AddElementDialogComponent } from '../add-element-dialog/add-element-dialog.component';
import { RemoveElementDialogComponent } from '../remove-element-dialog/remove-element-dialog.component';
import { EditViewFreeTextFieldDialogComponent } from '../../../shared/components/dialogs/edit-view-free-text-field-dialog/edit-view-free-text-field-dialog.component';
import { EditViewFreeTextDialog } from '../../../shared/types/EditViewFreeTextDialog';
import { HeaderService } from '../../../shared/services/ui/header.service';
import { enumsetDialogData } from '../../../shared/types/EnumSetDialogData';
import { applic } from '../../../../../types/applicability/applic';
import { difference } from 'src/app/types/change-report/change-report';
import { EnumerationUIService } from '../../../shared/services/ui/enumeration-ui.service';
import { UiService } from '../../../../../ple-services/ui/ui.service';

@Component({
	selector: 'osee-messaging-message-element-interface-sub-element-table',
	templateUrl: './sub-element-table.component.html',
	styleUrls: ['./sub-element-table.component.sass'],
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
		private structureService: CurrentStructureService,
		private layoutNotifier: LayoutNotifierService,
		private headerService: HeaderService,
		private enumSetService: EnumerationUIService
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
