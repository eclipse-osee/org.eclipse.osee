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
	CdkDrag,
	CdkDragDrop,
	CdkDragHandle,
	CdkDropList,
} from '@angular/cdk/drag-drop';
import { AsyncPipe, NgClass } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	Inject,
	Input,
	OnChanges,
	OnInit,
	SimpleChanges,
	computed,
	signal,
	viewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuTrigger,
} from '@angular/material/menu';
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
	MatTableDataSource,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { LayoutNotifierService } from '@osee/layout/notification';
import {
	CurrentStructureService,
	HeaderService,
} from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import type {
	element,
	elementWithChanges,
	structure,
} from '@osee/messaging/shared/types';
import { applic } from '@osee/shared/types/applicability';
import { difference } from '@osee/shared/types/change-report';
import { SubElementTableFieldComponent } from '../../fields/sub-element-table-field/sub-element-table-field.component';
import { SubElementTableDropdownComponent } from '../../menus/sub-element-table-dropdown/sub-element-table-dropdown.component';
import { SubElementArrayTableComponent } from '../sub-element-array-table/sub-element-array-table.component';

@Component({
	selector: 'osee-messaging-message-element-interface-sub-element-table',
	templateUrl: './sub-element-table.component.html',
	styles: [
		':host {display: block;width: 100%;max-width: 100vw;overflow-x: auto;max-height: 10%;}',
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	standalone: true,
	imports: [
		NgClass,
		AsyncPipe,
		RouterLink,
		CdkDrag,
		CdkDragHandle,
		CdkDropList,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatTooltip,
		MatCell,
		MatCellDef,
		MatIcon,
		MatIconButton,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatMenu,
		MatMenuContent,
		MatMenuTrigger,
		FormsModule,
		SubElementTableFieldComponent,
		SubElementTableDropdownComponent,
		SubElementArrayTableComponent,
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
	@Input() elementHeaders: any;
	@Input() editMode: boolean = false;

	_branchId: string = '';
	_branchType: string = '';
	layout = this.layoutNotifier.layout;
	menuPosition = {
		x: '0',
		y: '0',
	};

	generalMenuTrigger = viewChild.required('generalMenuTrigger', {
		read: MatMenuTrigger,
	});
	constructor(
		private route: ActivatedRoute,
		public dialog: MatDialog,
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structureService: CurrentStructureService,
		private layoutNotifier: LayoutNotifierService,
		private headerService: HeaderService
	) {
		this.elementHeaders = [
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

	expandedRows = signal<element[]>([]);

	ngOnChanges(changes: SimpleChanges): void {
		if (Array.isArray(this.data)) {
			this.dataSource.data = this.data;
		}
		if (this.filter !== '') {
			this.dataSource.data.forEach((e) => {
				if (e.arrayElements.length > 0) {
					this.rowChange(e, true);
				}
			});
		}
	}

	ngOnInit(): void {
		if (Array.isArray(this.data)) {
			this.dataSource.data = this.data;
		}
		this.route.paramMap.subscribe((values) => {
			this._branchId = values.get('branchId') || '';
			this._branchType = values.get('branchType') || '';
		});
	}

	rowIsExpanded(elementId: string) {
		return computed(() =>
			this.expandedRows()
				.map((e) => e.id)
				.includes(elementId)
		);
	}

	rowChange(element: element, expand: boolean) {
		if (expand && !this.rowIsExpanded(element.id)()) {
			this.expandedRows.update((rows) => [...rows, element]);
		} else if (!expand && this.rowIsExpanded(element.id)()) {
			this.expandedRows.update((rows) =>
				rows.filter((e) => e.id !== element.id)
			);
		}
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

	containsAutogenSpare(element: element) {
		return element.arrayElements.filter((e) => e.autogenerated).length > 0;
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
		this.generalMenuTrigger().menuData = {
			element: element,
			structure: this.structure,
			field: field,
			header: header,
		};
		this.generalMenuTrigger().openMenu();
	}

	getHeaderByName(value: string) {
		return this.headerService.getHeaderByName(value, 'element');
	}

	elementTracker(index: number, item: element | elementWithChanges) {
		return item.id !== '-1' ? item.id : index.toString();
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
