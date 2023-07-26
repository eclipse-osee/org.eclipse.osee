/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component, Inject, Input, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatTableModule } from '@angular/material/table';
import { ActivatedRoute, RouterLink } from '@angular/router';
import {
	CdkDrag,
	CdkDragDrop,
	CdkDragHandle,
	CdkDropList,
} from '@angular/cdk/drag-drop';
import { LayoutNotifierService } from '@osee/layout/notification';
import { applic } from '@osee/shared/types/applicability';
import { difference } from '@osee/shared/types/change-report';
import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { SubElementTableFieldComponent } from '../../fields/sub-element-table-field/sub-element-table-field.component';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import type { element } from '@osee/messaging/shared/types';
import {
	CurrentStructureService,
	HeaderService,
} from '@osee/messaging/shared/services';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { SubElementArrayTableDropdownComponent } from '../../menus/sub-element-array-table-dropdown/sub-element-array-table-dropdown.component';

@Component({
	selector: 'osee-sub-element-array-table',
	templateUrl: './sub-element-array-table.component.html',
	styles: [
		':host {display: block;width: 100%;overflow-x: auto;max-height: 10%;}',
	],
	standalone: true,
	imports: [
		NgFor,
		NgIf,
		NgClass,
		AsyncPipe,
		RouterLink,
		CdkDrag,
		CdkDragHandle,
		CdkDropList,
		MatTableModule,
		MatIconModule,
		MatMenuModule,
		MatTooltipModule,
		MatFormFieldModule,
		FormsModule,
		SubElementTableFieldComponent,
		SubElementArrayTableDropdownComponent,
	],
})
export class SubElementArrayTableComponent implements OnInit {
	@Input() filter: string = '';
	@Input() element: element = {
		id: '-1',
		name: '',
		description: '',
		notes: '',
		interfaceElementIndexStart: 0,
		interfaceElementIndexEnd: 0,
		interfaceElementAlterable: false,
		interfaceElementArrayHeader: false,
		interfaceDefaultValue: '',
		enumLiteral: '',
		platformType: new PlatformTypeSentinel(),
		arrayElements: [],
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

	@ViewChild('generalMenuTrigger', { static: true })
	generalMenuTrigger!: MatMenuTrigger;
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
	}

	ngOnInit(): void {
		this.route.paramMap.subscribe((values) => {
			this._branchId = values.get('branchId') || '';
			this._branchType = values.get('branchType') || '';
		});
	}

	handleDragDrop(event: CdkDragDrop<unknown[]>) {
		if (event.currentIndex === event.previousIndex) {
			return;
		}
		// Rows not marked as draggable are not included in the index count,
		// so remove them from the list before checking index.
		const tableData = this.element.arrayElements.filter(
			(e) => e.id !== '-1'
		);
		const elementId = tableData[event.previousIndex].id;
		tableData.splice(event.previousIndex, 1);
		const newIndex = event.currentIndex - 1;
		const afterArtifactId = newIndex < 0 ? 'start' : tableData[newIndex].id;
		this.structureService
			.changeElementArrayRelationOrder(
				this.element.id,
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
			headerElement: this.element,
			field: field,
			header: header,
		};
		this.generalMenuTrigger.openMenu();
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
