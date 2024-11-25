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
	animate,
	state,
	style,
	transition,
	trigger,
} from '@angular/animations';
import { Component, input, model, signal, inject } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import { MatDialogActions, MatDialogContent } from '@angular/material/dialog';
import {
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatSelect } from '@angular/material/select';
import { MatStepperNext, MatStepperPrevious } from '@angular/material/stepper';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { validateEnumLengthIsBelowMax } from '@osee/messaging/shared/functions';
import { EnumerationUIService } from '@osee/messaging/shared/services';
import type {
	PlatformType,
	enumerationSet,
	logicalType,
} from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import {
	provideOptionalControlContainerNgForm,
	writableSlice,
} from '@osee/shared/utils';
import {
	BehaviorSubject,
	combineLatest,
	concatMap,
	distinct,
	filter,
	from,
	of,
	reduce,
	switchMap,
} from 'rxjs';
import { EnumSetFormComponent } from '../../forms/enum-set-form/enum-set-form.component';
import { NewPlatformTypeFormComponent } from '../new-platform-type-form/new-platform-type-form.component';

@Component({
	selector: 'osee-new-platform-type-form-page2',
	templateUrl: './new-platform-type-form-page2.component.html',
	styles: [],
	imports: [
		NewPlatformTypeFormComponent,
		MatOptionLoadingComponent,
		EnumSetFormComponent,
		FormsModule,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatSelect,
		MatOption,
		MatIconButton,
		MatSuffix,
		MatIcon,
		MatDialogActions,
		MatButton,
		MatStepperPrevious,
		MatStepperNext,
	],
	animations: [
		trigger('detailExpand', [
			state('collapsed', style({ maxHeight: '0px', minHeight: '0' })),
			state('expanded', style({ maxHeight: '60vh' })),
			transition(
				'expanded <=> collapsed',
				animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')
			),
		]),
	],
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class NewPlatformTypeFormPage2Component {
	private enumSetService = inject(EnumerationUIService);

	enumSetState = new BehaviorSubject<enumerationSet | undefined>(undefined);

	createNewEnum = signal(false);

	logicalType = input.required<logicalType>();

	_enumSets = this.enumSetService.enumSets;
	platformType = model<PlatformType>(new PlatformTypeSentinel());
	private platformType$ = toObservable(this.platformType);
	enumSet = writableSlice(this.platformType, 'enumSet');
	private enumSetId = writableSlice(this.enumSet, 'id');
	private enumSetName = writableSlice(this.enumSet, 'name');
	private enumSetNameId = writableSlice(this.enumSetName, 'id');
	private enumSetDescription = writableSlice(this.enumSet, 'description');
	private enumSetDescriptionId = writableSlice(this.enumSetDescription, 'id');
	private enumerations = writableSlice(this.enumSet, 'enumerations');
	enumSets = combineLatest([this.platformType$, this._enumSets]).pipe(
		switchMap(([pType, enumSets]) =>
			of({
				bitSize: pType.interfacePlatformTypeBitSize || {
					id: '-1',
					value: '0',
					attributeType:
						ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE,
					gammaId: '-1',
				},
				enumSets: enumSets,
			}).pipe(
				switchMap(({ bitSize, enumSets }) =>
					of(enumSets).pipe(
						concatMap((enumSets) =>
							from(enumSets).pipe(
								filter(
									(enumSet) =>
										!validateEnumLengthIsBelowMax(
											enumSet.enumerations?.length || 0,
											parseInt(bitSize.value)
										)
								),
								distinct()
							)
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as enumerationSet[]
						)
					)
				)
			)
		)
	);

	/**
	 * Updates the platform type to the current state from osee-new-platform-type-form
	 */
	attributesUpdate(value: PlatformType) {
		this.platformType.set(value);
	}

	/**
	 * Switches between enum creation and enum selection modes for logical type === enumeration
	 */
	toggleEnumCreationState(event?: Event) {
		event?.stopPropagation();
		if (!this.createNewEnum()) {
			//if going from selected to creation state, clear out ids
			this.enumSetId.set('-1');
			this.enumSetNameId.set('-1');
			this.enumSetDescriptionId.set('-1');
			this.enumerations.update((e) => {
				e.forEach((v) => {
					v.id = '-1';
					v.name.id = '-1';
					v.ordinal.id = '-1';
				});
				return e;
			});
		}
		this.createNewEnum.update((v) => !v);
	}

	/**
	 * Comparator for enum sets
	 */
	compareEnumSet(o1: enumerationSet, o2: enumerationSet) {
		return o1?.id === o2?.id && o1?.name === o2?.name;
	}
}
