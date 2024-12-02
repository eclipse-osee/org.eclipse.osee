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
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { Component, computed, model, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import {
	MatSlideToggle,
	MatSlideToggleChange,
} from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { NewTypeFormComponent } from '@osee/messaging/shared/forms';
import {
	CurrentStructureService,
	TypesUIService,
} from '@osee/messaging/shared/services';
import {
	ElementDialog,
	PlatformType,
	element,
} from '@osee/messaging/shared/types';
import { PlatformTypeDropdownComponent } from '@osee/messaging/types/dropdown';
import {
	provideOptionalControlContainerNgForm,
	writableSlice,
} from '@osee/shared/utils';
import { filter, take, tap } from 'rxjs';
// import { AddElementDialogComponent } from '../../dialogs/add-element-dialog/add-element-dialog.component';
import { DefaultAddElementDialog } from '../../dialogs/add-element-dialog/add-element-dialog.default';
import { RemoveArrayElementsDialogComponent } from '../../dialogs/remove-array-elements-dialog/remove-array-elements-dialog.component';

const _platformTypeStates = ['SELECT', 'QUERY', 'CREATE'] as const;
type platformTypeStates =
	(typeof _platformTypeStates)[keyof typeof _platformTypeStates];

@Component({
	selector: 'osee-element-form',
	imports: [
		AsyncPipe,
		NgTemplateOutlet,
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		MatHint,
		CdkTextareaAutosize,
		MatSlideToggle,
		MatTooltip,
		MatIcon,
		MatSelect,
		MatOption,
		MatIconButton,
		MatDivider,
		ApplicabilityDropdownComponent,
		PlatformTypeDropdownComponent,
		NewTypeFormComponent,
	],
	templateUrl: './element-form.component.html',
	styles: [],
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class ElementFormComponent {
	private structures = inject(CurrentStructureService);
	private dialog = inject(MatDialog);
	private typeDialogService = inject(TypesUIService);

	data = model.required<ElementDialog>();

	createdPlatformTypes = writableSlice(this.data, 'createdTypes');

	protected element = writableSlice(this.data, 'element');
	private nameAttr = writableSlice(this.element, 'name');
	protected name = writableSlice(this.nameAttr, 'value');
	private descriptionAttr = writableSlice(this.element, 'description');
	protected description = writableSlice(this.descriptionAttr, 'value');
	private notesAttr = writableSlice(this.element, 'notes');
	protected notes = writableSlice(this.notesAttr, 'value');
	private enumLiteralAttr = writableSlice(this.element, 'enumLiteral');
	protected enumLiteral = writableSlice(this.enumLiteralAttr, 'value');
	private interfaceElementAlterableAttr = writableSlice(
		this.element,
		'interfaceElementAlterable'
	);
	protected interfaceElementAlterable = writableSlice(
		this.interfaceElementAlterableAttr,
		'value'
	);

	private interfaceElementBlockDataAttr = writableSlice(
		this.element,
		'interfaceElementBlockData'
	);
	protected interfaceElementBlockData = writableSlice(
		this.interfaceElementBlockDataAttr,
		'value'
	);
	protected applicability = writableSlice(this.element, 'applicability');
	private interfaceElementIndexStartAttr = writableSlice(
		this.element,
		'interfaceElementIndexStart'
	);
	protected interfaceElementIndexStart = writableSlice(
		this.interfaceElementIndexStartAttr,
		'value'
	);
	private interfaceElementIndexEndAttr = writableSlice(
		this.element,
		'interfaceElementIndexEnd'
	);
	protected interfaceElementIndexEnd = writableSlice(
		this.interfaceElementIndexEndAttr,
		'value'
	);
	private interfaceElementArrayHeaderAttr = writableSlice(
		this.element,
		'interfaceElementArrayHeader'
	);
	protected interfaceElementArrayHeader = writableSlice(
		this.interfaceElementArrayHeaderAttr,
		'value'
	);
	private interfaceElementArrayIndexOrderAttr = writableSlice(
		this.element,
		'interfaceElementArrayIndexOrder'
	);
	protected interfaceElementArrayIndexOrder = writableSlice(
		this.interfaceElementArrayIndexOrderAttr,
		'value'
	);
	private interfaceElementArrayIndexDelimiterOneAttr = writableSlice(
		this.element,
		'interfaceElementArrayIndexDelimiterOne'
	);
	protected interfaceElementArrayIndexDelimiterOne = writableSlice(
		this.interfaceElementArrayIndexDelimiterOneAttr,
		'value'
	);
	private interfaceElementArrayIndexDelimiterTwoAttr = writableSlice(
		this.element,
		'interfaceElementArrayIndexDelimiterTwo'
	);
	protected interfaceElementArrayIndexDelimiterTwo = writableSlice(
		this.interfaceElementArrayIndexDelimiterTwoAttr,
		'value'
	);
	protected interfaceElementWriteArrayHeaderName = writableSlice(
		this.element,
		'interfaceElementWriteArrayHeaderName'
	);
	protected arrayElements = writableSlice(this.element, 'arrayElements');
	protected platformType = writableSlice(this.element, 'platformType');

	elementExample = computed(() => {
		const d1 = this.interfaceElementArrayIndexDelimiterOne();
		const d2 = this.interfaceElementArrayIndexDelimiterTwo();
		if (this.interfaceElementArrayIndexOrder() === 'INNER_OUTER') {
			return [
				this.name() + d2 + 1,
				this.name() + d2 + 2,
				this.name() + d2 + 3,
			];
		} else {
			return [
				this.name() + d1 + 1,
				this.name() + d1 + 2,
				this.name() + d1 + 3,
			];
		}
	});
	arrayExample = computed(() => {
		const d1 = this.interfaceElementArrayIndexDelimiterOne();
		const d2 = this.interfaceElementArrayIndexDelimiterTwo();
		if (this.interfaceElementArrayIndexOrder() === 'INNER_OUTER') {
			return [
				this.name() + d1 + 1 + d2 + 1,
				this.name() + d1 + 2 + d2 + 1,
				this.name() + d1 + 3 + d2 + 1,
				this.name() + d1 + 1 + d2 + 2,
				this.name() + d1 + 2 + d2 + 2,
				this.name() + d1 + 3 + d2 + 2,
			];
		} else {
			return [
				this.name() + d1 + 1 + d2 + 1,
				this.name() + d1 + 1 + d2 + 2,
				this.name() + d1 + 1 + d2 + 3,
				this.name() + d1 + 2 + d2 + 1,
				this.name() + d1 + 2 + d2 + 2,
				this.name() + d1 + 2 + d2 + 3,
			];
		}
	});

	protected typeState = signal<platformTypeStates>('SELECT');
	arrayIndexOrders = this.structures.arrayIndexOrders;

	openPlatformTypeDialog(event?: Event) {
		event?.stopPropagation();
		this.typeState.set('CREATE');
	}

	closeAddMenu() {
		this.typeState.set('SELECT');
	}

	receivePlatformTypeData(value: PlatformType) {
		this.closeAddMenu();
		const { enumSet, ...platformType } = value;
		if (platformType.interfaceLogicalType.value === 'enumeration') {
			this.enumLiteral.set(enumSet.description.value);
		}
		this.createdPlatformTypes.update((v) => [...v, value]);
	}
	createPlatformType(results: PlatformType) {
		return this.typeDialogService.createType(results);
	}

	toggleArrayHeader(val: MatSlideToggleChange) {
		if (
			!val.checked &&
			this.arrayElements() &&
			this.arrayElements().length > 0
		) {
			const dialogRef = this.dialog.open(
				RemoveArrayElementsDialogComponent
			);
			dialogRef
				.afterClosed()
				.pipe(
					take(1),
					tap((res) => {
						if (res === 'ok') {
							this.arrayElements.set([]);
							this.interfaceElementArrayHeader.set(false);
							this.platformType.set(new PlatformTypeSentinel());
						} else {
							this.interfaceElementArrayHeader.set(true);
						}
					})
				)
				.subscribe();
		}
	}

	async addArrayElement() {
		const dialogData = new DefaultAddElementDialog(
			'',
			this.name() + ' Array',
			undefined,
			undefined,
			'add',
			false,
			false,
			this.createdPlatformTypes()
		);
		const { AddElementDialogComponent } = await import(
			'../../dialogs/add-element-dialog/add-element-dialog.component'
		);
		const dialogRef = this.dialog.open(AddElementDialogComponent, {
			data: dialogData,
			minWidth: '80vw',
			minHeight: '90vh',
		});
		const createElement = dialogRef.afterClosed().pipe(
			take(1),
			filter(
				(val) =>
					(val !== undefined || val !== null) &&
					val?.element !== undefined
			),
			tap((value: ElementDialog) => {
				if (this.arrayElements()) {
					this.platformType.set(value.type);
					this.arrayElements.update((elements) => [
						...elements,
						value.element,
					]);
					this.createdPlatformTypes.set([...value.createdTypes]);
				}
			})
		);
		createElement.subscribe();
	}

	removeFromArray(element: element) {
		this.arrayElements.update((elements) =>
			elements.filter(
				(v) => v.name !== element.name && v.id !== element.id
			)
		);
	}

	async editArrayElement(element: element) {
		const dialogData: ElementDialog = {
			id: '',
			name: '',
			startingElement: structuredClone(element),
			element: element,
			type: element.platformType,
			mode: 'edit',
			allowArray: false,
			arrayChild: true,
			createdTypes: this.createdPlatformTypes(),
		};
		// Lazy loading the Edit Element Dialog to avoid a circular dependency
		const { EditElementDialogComponent } = await import(
			'@osee/messaging/structure-tables'
		);
		const dialogRef = this.dialog.open(EditElementDialogComponent, {
			data: dialogData,
			minWidth: '70vw',
			minHeight: '80vh',
		});
		dialogRef.afterClosed().pipe(take(1)).subscribe();
	}

	compareArrayIndexOrders(a1: string, a2: string) {
		return a1 === a2;
	}
}
