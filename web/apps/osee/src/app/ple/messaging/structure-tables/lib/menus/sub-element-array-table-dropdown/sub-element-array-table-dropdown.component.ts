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
import { AsyncPipe } from '@angular/common';
import { Component, Input, inject } from '@angular/core';
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
import { applic } from '@osee/applicability/types';
import { AttributeToValuePipe } from '@osee/attributes/pipes';
import { HeaderService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import {
	DiffableElementProps,
	DisplayableElementProps,
	diffableElementHeaders,
	elementHeaderSentinel,
	elementSentinel,
	type PlatformType,
	type element,
} from '@osee/messaging/shared/types';
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
		AsyncPipe,
		RouterLink,
		MatMenuItem,
		MatIcon,
		MatDivider,
		MatMenuTrigger,
		MatLabel,
		MatMenu,
		MatMenuContent,
		AttributeToValuePipe,
	],
	templateUrl: './sub-element-array-table-dropdown.component.html',
})
export class SubElementArrayTableDropdownComponent {
	dialog = inject(MatDialog);
	private structureService = inject(STRUCTURE_SERVICE_TOKEN);
	private headerService = inject(HeaderService);
	private elementDropdownService = inject(ElementTableDropdownService);

	@Input() element: element = elementSentinel;

	@Input() headerElement: element = elementHeaderSentinel;

	@Input() header!: keyof DisplayableElementProps;
	@Input() field?: string | number | boolean | PlatformType | applic;

	@Input('branchId') _branchId = '';
	@Input('branchType') _branchType = '';

	@Input() editMode = false;

	removeElement() {
		const dialogData: RemoveElementDialogData = {
			removeType: 'Array',
			elementName: this.element.name.value,
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
		this.elementDropdownService.openEditElementDialog(element, true);
	}

	openEnumDialog(id: string) {
		this.elementDropdownService.openEnumDialog(id, this.editMode);
	}

	openDescriptionDialog(element: element) {
		this.elementDropdownService.openDescriptionDialog(
			element,
			this.editMode
		);
	}

	openEnumLiteralDialog(element: element) {
		this.elementDropdownService.openEnumLiteralDialog(
			element,
			this.editMode
		);
	}

	openNotesDialog(element: element) {
		this.elementDropdownService.openNotesDialog(element, this.editMode);
	}

	getHeaderByName(value: string) {
		return this.headerService.getHeaderByName(value, 'element');
	}

	viewDiff<T>(value: difference<T> | undefined, header: string) {
		this.elementDropdownService.viewDiff(value, header);
	}

	hasChanges(v: element): v is Required<element> {
		return this.elementDropdownService.hasChanges(v);
	}
	protected isDiffableHeader(
		value: keyof DisplayableElementProps
	): value is keyof DiffableElementProps {
		//@ts-expect-error this is valid
		return diffableElementHeaders.includes(value);
	}
}
