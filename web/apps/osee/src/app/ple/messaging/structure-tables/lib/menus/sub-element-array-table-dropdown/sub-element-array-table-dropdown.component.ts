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
import { AttributeToValuePipe } from '@osee/attributes/pipes';
import { HeaderService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import {
	DiffableElementProps,
	DisplayableElementProps,
	diffableElementHeaders,
	type PlatformType,
	type element,
} from '@osee/messaging/shared/types';
import { difference } from '@osee/shared/types/change-report';
import { iif, of, switchMap, take } from 'rxjs';
import { RemoveElementDialogData } from '../../dialogs/remove-element-dialog/remove-element-dialog';
import { RemoveElementDialogComponent } from '../../dialogs/remove-element-dialog/remove-element-dialog.component';
import { ElementTableDropdownService } from '../../services/element-table-dropdown.service';
import { MoveArrayElementDialogComponent } from '../../dialogs/move-array-element-dialog/move-array-element-dialog.component';

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

	element = input.required<element>();
	headerElement = input.required<element>();
	header = input.required<keyof DisplayableElementProps>();
	branchId = input.required<string>();
	branchType = input.required<string>();
	editMode = input.required<boolean>();
	selectedElements = input.required<element[]>();

	multiselect = computed(() => this.selectedElements().length > 0);

	removeElement() {
		const elementsToRemove = this.multiselect()
			? this.selectedElements()
			: [this.element()];
		const dialogData: RemoveElementDialogData = {
			deleteRemove: 'remove',
			elementsToRemove,
			removeFromName: this.element().name.value,
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
						this.structureService.removeElementsFromArray(
							elementsToRemove,
							this.headerElement()
						),
						of()
					)
				)
			)
			.subscribe();
	}

	deleteElement() {
		const elementsToDelete = this.multiselect()
			? this.selectedElements()
			: [this.element()];
		this.elementDropdownService.openDeleteElementDialog(
			elementsToDelete,
			this.headerElement().name.value
		);
	}

	openAddElementDialog(afterElement?: string, copyElement?: element) {
		this.elementDropdownService.openAddElementDialog(
			this.headerElement(),
			true,
			false,
			afterElement,
			copyElement
		);
	}

	openEditElementDialog() {
		this.elementDropdownService.openEditElementDialog(this.element(), true);
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
			.open(MoveArrayElementDialogComponent, {
				data: {
					element: this.element(),
					parent: this.headerElement(),
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
					return this.structureService.changeElementArrayRelationOrder(
						this.headerElement().id,
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

	noop() {
		// Do nothing
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
