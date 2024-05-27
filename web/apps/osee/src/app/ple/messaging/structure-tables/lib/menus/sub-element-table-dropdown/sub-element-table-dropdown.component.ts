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
import { applic, applicabilitySentinel } from '@osee/applicability/types';
import { AttributeToValuePipe } from '@osee/attributes/pipes';
import { HeaderService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import {
	DiffableElementProps,
	DisplayableElementProps,
	diffableElementHeaders,
	elementSentinel,
	type PlatformType,
	type element,
	type structure,
} from '@osee/messaging/shared/types';
import { difference } from '@osee/shared/types/change-report';
import { iif, of, switchMap, take } from 'rxjs';
import { RemoveElementDialogData } from '../../dialogs/remove-element-dialog/remove-element-dialog';
import { RemoveElementDialogComponent } from '../../dialogs/remove-element-dialog/remove-element-dialog.component';
import { ElementTableDropdownService } from '../../services/element-table-dropdown.service';

/**
 * Required attributes:
 * element
 * structure
 * header
 * branchId
 * branchType
 * editMode
 */
@Component({
	selector:
		'osee-sub-element-table-dropdown[element][structure][header][branchId][branchType][editMode]',
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
	templateUrl: './sub-element-table-dropdown.component.html',
	styles: [],
})
export class SubElementTableDropdownComponent {
	dialog = inject(MatDialog);
	private structureService = inject(STRUCTURE_SERVICE_TOKEN);
	private headerService = inject(HeaderService);
	private elementDropdownService = inject(ElementTableDropdownService);

	@Input() element: element = elementSentinel;

	@Input() structure: structure = {
		id: '-1',
		gammaId: '-1',
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: '',
		},
		nameAbbrev: {
			id: '-1',
			typeId: '8355308043647703563',
			gammaId: '-1',
			value: '',
		},
		description: {
			id: '-1',
			typeId: '1152921504606847090',
			gammaId: '-1',
			value: '',
		},
		interfaceMaxSimultaneity: {
			id: '-1',
			typeId: '2455059983007225756',
			gammaId: '-1',
			value: '',
		},
		interfaceMinSimultaneity: {
			id: '-1',
			typeId: '2455059983007225755',
			gammaId: '-1',
			value: '',
		},
		interfaceTaskFileType: {
			id: '-1',
			typeId: '2455059983007225760',
			gammaId: '-1',
			value: 0,
		},
		interfaceStructureCategory: {
			id: '-1',
			typeId: '2455059983007225764',
			gammaId: '-1',
			value: '',
		},
		applicability: applicabilitySentinel,
		elements: [],
	};

	@Input() header!: keyof DisplayableElementProps;
	@Input() field?: string | number | boolean | PlatformType | applic;

	@Input('branchId') _branchId = '';
	@Input('branchType') _branchType = '';

	@Input() editMode = false;

	removeElement(element: element, structure: structure) {
		const dialogData: RemoveElementDialogData = {
			removeType: 'Structure',
			elementName: element.name.value,
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
		this.elementDropdownService.openDeleteElementDialog(
			element,
			'Structure'
		);
	}

	openAddElementDialog(
		structure: structure | element,
		isArray: boolean,
		allowArray: boolean,
		afterElement?: string,
		copyElement?: element
	) {
		this.elementDropdownService.openAddElementDialog(
			structure,
			isArray,
			allowArray,
			afterElement,
			copyElement
		);
	}

	openEditElementDialog(element: element) {
		this.elementDropdownService.openEditElementDialog(element, false);
	}

	openEnumDialog(id: string) {
		this.elementDropdownService.openEnumDialog(id, this.editMode);
	}

	openDescriptionDialog(element: element) {
		this.elementDropdownService.openDescriptionDialog(element);
	}

	/**
	 * Need to verify if type is required
	 */
	openEnumLiteralDialog(element: element) {
		this.elementDropdownService.openEnumLiteralDialog(element);
	}

	openNotesDialog(element: element) {
		this.elementDropdownService.openNotesDialog(element);
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
