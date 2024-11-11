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
import { Component, computed, inject, input } from '@angular/core';
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
	type PlatformType,
	type element,
	type structure,
} from '@osee/messaging/shared/types';
import { difference } from '@osee/shared/types/change-report';
import { iif, of, switchMap, take } from 'rxjs';
import { RemoveElementDialogData } from '../../dialogs/remove-element-dialog/remove-element-dialog';
import { RemoveElementDialogComponent } from '../../dialogs/remove-element-dialog/remove-element-dialog.component';
import { ElementTableDropdownService } from '../../services/element-table-dropdown.service';
import { MoveElementDialogComponent } from '../../dialogs/move-element-dialog/move-element-dialog.component';

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
	selector: 'osee-sub-element-table-dropdown',
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

	element = input.required<element>();
	structure = input.required<structure>();
	header = input.required<keyof DisplayableElementProps>();
	branchId = input.required<string>();
	branchType = input.required<string>();
	editMode = input.required<boolean>();

	hasChanges = computed(() =>
		this.elementDropdownService.hasChanges(this.element())
	);

	removeElement() {
		const dialogData: RemoveElementDialogData = {
			removeType: 'Structure',
			elementName: this.element().name.value,
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
							this.element(),
							this.structure()
						),
						of()
					)
				)
			)
			.subscribe();
	}

	deleteElement() {
		this.elementDropdownService.openDeleteElementDialog(
			this.element(),
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

	openEditElementDialog() {
		this.elementDropdownService.openEditElementDialog(
			this.element(),
			false
		);
	}

	openEnumDialog(platformType: PlatformType) {
		this.elementDropdownService.openEnumDialog(
			platformType,
			this.editMode()
		);
	}

	openDescriptionDialog() {
		this.elementDropdownService.openDescriptionDialog(
			this.element(),
			this.editMode()
		);
	}

	/**
	 * Need to verify if type is required
	 */
	openEnumLiteralDialog() {
		this.elementDropdownService.openEnumLiteralDialog(
			this.element(),
			this.editMode()
		);
	}

	openNotesDialog() {
		this.elementDropdownService.openNotesDialog(
			this.element(),
			this.editMode()
		);
	}

	openMoveElementDialog() {
		this.dialog
			.open(MoveElementDialogComponent, {
				data: {
					element: this.element(),
					structure: this.structure(),
				},
				minHeight: '40vh',
				minWidth: '40vw',
			})
			.afterClosed()
			.pipe(
				switchMap((afterArtifact) => {
					if (!afterArtifact) {
						return of();
					}
					return this.structureService.changeElementRelationOrder(
						this.structure().id,
						this.element().id,
						afterArtifact
					);
				})
			)
			.subscribe();
	}

	getHeaderByName(value: string) {
		return this.headerService.getHeaderByName(value, 'element');
	}

	viewDiff<T>(value: difference<T> | undefined, header: string) {
		this.elementDropdownService.viewDiff(value, header);
	}

	protected isDiffableHeader(
		value: keyof DisplayableElementProps
	): value is keyof DiffableElementProps {
		//@ts-expect-error this is valid
		return diffableElementHeaders.includes(value);
	}
}
