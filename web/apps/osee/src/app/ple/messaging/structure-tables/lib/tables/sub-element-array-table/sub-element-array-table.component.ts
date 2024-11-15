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
	effect,
	inject,
	input,
	model,
	OnInit,
	signal,
	viewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
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
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { applic } from '@osee/applicability/types';
import { LayoutNotifierService } from '@osee/layout/notification';
import { HeaderService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import {
	DisplayableElementProps,
	element,
	elementSentinel,
} from '@osee/messaging/shared/types';
import { difference } from '@osee/shared/types/change-report';
import { SubElementTableFieldComponent } from '../../fields/sub-element-table-field/sub-element-table-field.component';
import { SubElementArrayTableDropdownComponent } from '../../menus/sub-element-array-table-dropdown/sub-element-array-table-dropdown.component';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { MatBadge } from '@angular/material/badge';

@Component({
	selector: 'osee-sub-element-array-table',
	templateUrl: './sub-element-array-table.component.html',
	styles: [
		':host {display: block;width: 100%;overflow-x: auto;max-height: 10%;}',
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
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatMenu,
		MatMenuContent,
		MatMenuTrigger,
		FormsModule,
		SubElementTableFieldComponent,
		SubElementArrayTableDropdownComponent,
		MatCheckbox,
		MatBadge,
	],
})
export class SubElementArrayTableComponent implements OnInit {
	private route = inject(ActivatedRoute);
	dialog = inject(MatDialog);
	private structureService = inject(STRUCTURE_SERVICE_TOKEN);
	private layoutNotifier = inject(LayoutNotifierService);
	private headerService = inject(HeaderService);

	element = input<element>(elementSentinel);
	elementHeaders = model<(keyof DisplayableElementProps | 'rowControls')[]>();
	editMode = input(false);
	tableFieldsEditMode = input(false);

	private _editEffect = effect(
		() => {
			this.editMode();
			this.selectedElements.set([]);
		},
		{ allowSignalWrites: true }
	);

	_branchId = '';
	_branchType = '';
	layout = this.layoutNotifier.layout;
	menuPosition = {
		x: '0',
		y: '0',
	};
	generalMenuTrigger = viewChild.required('generalMenuTrigger', {
		read: MatMenuTrigger,
	});

	selectedElements = signal<element[]>([]);

	constructor() {
		this.elementHeaders.set([
			'name',
			'beginWord',
			'endWord',
			'beginByte',
			'endByte',
			'interfaceElementAlterable',
			'description',
			'notes',
		]);
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
		const tableData = this.element().arrayElements.filter(
			(e) => e.id !== '-1'
		);
		const elementId = tableData[event.previousIndex].id;
		tableData.splice(event.previousIndex, 1);
		const newIndex = event.currentIndex - 1;
		const afterArtifactId = newIndex < 0 ? 'start' : tableData[newIndex].id;
		this.structureService
			.changeElementArrayRelationOrder(
				this.element().id,
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
		this.generalMenuTrigger().menuData = {
			element: element,
			headerElement: this.element(),
			field: field,
			header: header,
		};
		this.generalMenuTrigger().openMenu();
	}

	getHeaderByName(value: string) {
		return this.headerService.getHeaderByName(value, 'element');
	}

	elementTracker(index: number, item: element) {
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

	isChecked(element: element) {
		return this.selectedElements().find((e) => e.id === element.id);
	}

	selectAllElements() {
		if (this.selectedElements().length === 0) {
			this.selectedElements.set(
				this.element().arrayElements.filter((e) => !e.autogenerated)
			);
		} else {
			this.selectedElements.set([]);
		}
	}

	elementSelectionChange(element: element, change: MatCheckboxChange) {
		if (change.checked) {
			this.selectedElements.update((curr) => [...curr, element]);
		} else {
			this.selectedElements.update((curr) =>
				curr.filter((e) => e.id !== element.id)
			);
		}
	}
}
