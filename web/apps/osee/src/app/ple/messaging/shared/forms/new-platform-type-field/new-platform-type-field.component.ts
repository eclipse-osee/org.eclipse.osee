/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { Component, input, model, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { UniquePlatformTypeNameDirective } from '@osee/messaging/shared/directives';
import {
	DisplayablePlatformTypeProps,
	PlatformType,
	logicalTypeFieldInfo,
} from '@osee/messaging/shared/types';
import {
	FirstLetterLowerPipe,
	provideOptionalControlContainerNgModelGroup,
	writableSlice,
} from '@osee/shared/utils';
let nextUniqueId = 0;
@Component({
	selector: 'osee-new-platform-type-field',
	imports: [
		MatFormField,
		MatLabel,
		MatHint,
		MatInput,
		UniquePlatformTypeNameDirective,
		FirstLetterLowerPipe,
		FormsModule,
	],
	template: ` <mat-form-field class="tw-w-full">
		<mat-label>{{ form().name }}</mat-label>
		@if (form().attributeType === 'Name') {
			<input
				matInput
				type="text"
				[(ngModel)]="innerValue"
				[name]="
					(form().attributeType | oseeFirstLetterLower) +
					_componentId()
				"
				[id]="form().attributeTypeId"
				oseeUniquePlatformTypeName
				[ngModelOptions]="{ updateOn: 'blur' }"
				[required]="form().required"
				[attr.data-cy]="'field-' + form().attributeType" />
		}
		@if (form().attributeType !== 'Name') {
			<input
				matInput
				type="text"
				[(ngModel)]="innerValue"
				[name]="
					(form().attributeType | oseeFirstLetterLower) +
					_componentId()
				"
				[id]="form().attributeTypeId"
				[required]="form().required"
				[attr.data-cy]="'field-' + form().attributeType" />
		}
		@if (form().defaultValue) {
			<mat-hint align="end"
				>Default Value: {{ form().defaultValue }}</mat-hint
			>
		}
	</mat-form-field>`,
	viewProviders: [provideOptionalControlContainerNgModelGroup()],
})
export class NewPlatformTypeFieldComponent<
	K extends keyof Omit<
		DisplayablePlatformTypeProps,
		| 'id'
		| 'gammaId'
		| 'applicability'
		| 'enumSet'
		| 'added'
		| 'deleted'
		| 'changes'
	>,
> {
	protected _componentId = signal(`${nextUniqueId++}`);
	form = input.required<logicalTypeFieldInfo<K>>();
	value = model.required<PlatformType[K]>();
	protected innerValue = writableSlice(this.value, 'value');
}
