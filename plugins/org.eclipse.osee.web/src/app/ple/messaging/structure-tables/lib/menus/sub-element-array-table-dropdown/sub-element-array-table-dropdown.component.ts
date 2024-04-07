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
import { AsyncPipe, NgIf } from '@angular/common';
import { Component, Inject, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import {
	CurrentStructureService,
	HeaderService,
} from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import {
	elementHeaderSentinel,
	elementSentinel,
	type PlatformType,
	type element,
	type elementWithChanges,
} from '@osee/messaging/shared/types';
import { applic } from '@osee/shared/types/applicability';
import { difference } from '@osee/shared/types/change-report';
import { iif, of, switchMap, take } from 'rxjs';
import { RemoveElementDialogData } from '../../dialogs/remove-element-dialog/remove-element-dialog';
import { RemoveElementDialogComponent } from '../../dialogs/remove-element-dialog/remove-element-dialog.component';
import { ElementTableDropdownService } from '../../services/element-table-dropdown.service';

/**
 * Required attributes:
 * element
 * headerElement
 * header
 * branchId
 * branchType
 * editMode
 */
@Component({
	selector:
		'osee-sub-element-array-table-dropdown[element][headerElement][header][branchId][branchType][editMode]',
	standalone: true,
	imports: [
		NgIf,
		AsyncPipe,
		RouterLink,
		MatMenuItem,
		MatIcon,
		MatDivider,
		MatMenuTrigger,
		MatLabel,
		MatMenu,
		MatMenuContent,
	],
	templateUrl: './sub-element-array-table-dropdown.component.html',
})
export class SubElementArrayTableDropdownComponent {
	@Input() element: element = elementSentinel;

	@Input() headerElement: element = elementHeaderSentinel;

	@Input() header!: keyof element;
	@Input() field?: string | number | boolean | PlatformType | applic;

	@Input('branchId') _branchId: string = '';
	@Input('branchType') _branchType: string = '';

	@Input() editMode: boolean = false;

	constructor(
		public dialog: MatDialog,
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structureService: CurrentStructureService,
		private headerService: HeaderService,
		private elementDropdownService: ElementTableDropdownService
	) {}

	removeElement() {
		const dialogData: RemoveElementDialogData = {
			removeType: 'Array',
			elementName: this.element.name,
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
						this.structureService.removeElementFromArray(
							this.element,
							this.headerElement
						),
						of()
					)
				)
			)
			.subscribe();
	}

	deleteElement(element: element) {
		this.elementDropdownService.openDeleteElementDialog(element, 'Array');
	}

	openAddElementDialog(afterElement?: string, copyElement?: element) {
		this.elementDropdownService.openAddElementDialog(
			this.headerElement,
			true,
			false,
			afterElement,
			copyElement
		);
	}

	openEditElementDialog(element: element) {
		this.elementDropdownService.openEditElementDialog(element);
	}

	openEnumDialog(id: string) {
		this.elementDropdownService.openEnumDialog(id, this.editMode);
	}

	openDescriptionDialog(description: string, elementId: string) {
		this.elementDropdownService.openDescriptionDialog(
			description,
			elementId
		);
	}

	openEnumLiteralDialog(enumLiteral: string, elementId: string) {
		this.elementDropdownService.openEnumLiteralDialog(
			enumLiteral,
			elementId
		);
	}

	openNotesDialog(notes: string, elementId: string) {
		this.elementDropdownService.openNotesDialog(notes, elementId);
	}

	getHeaderByName(value: string) {
		return this.headerService.getHeaderByName(value, 'element');
	}

	viewDiff<T>(value: difference<T> | undefined, header: string) {
		this.elementDropdownService.viewDiff(value, header);
	}

	hasChanges(v: element | elementWithChanges): v is elementWithChanges {
		return this.elementDropdownService.hasChanges(v);
	}
}
