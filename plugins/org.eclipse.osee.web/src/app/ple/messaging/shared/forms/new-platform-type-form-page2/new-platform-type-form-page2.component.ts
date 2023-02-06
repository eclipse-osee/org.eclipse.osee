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
	Component,
	Input,
	OnChanges,
	Output,
	SimpleChanges,
} from '@angular/core';
import {
	BehaviorSubject,
	take,
	map,
	tap,
	combineLatest,
	concatMap,
	distinct,
	filter,
	from,
	of,
	reduce,
	switchMap,
} from 'rxjs';
import { NewPlatformTypeFormComponent } from '../new-platform-type-form/new-platform-type-form.component';
import { EnumSetFormComponent } from '../../forms/enum-set-form/enum-set-form.component';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatStepperModule } from '@angular/material/stepper';
import {
	trigger,
	state,
	style,
	transition,
	animate,
} from '@angular/animations';
import {
	enumerationSet,
	logicalType,
	PlatformType,
} from '@osee/messaging/shared/types';
import { validateEnumLengthIsBelowMax } from '@osee/messaging/shared/functions';
import { EnumerationUIService } from '@osee/messaging/shared/services';
import { MatOptionLoadingComponent } from '@osee/shared/components';

@Component({
	selector: 'osee-new-platform-type-form-page2',
	standalone: true,
	templateUrl: './new-platform-type-form-page2.component.html',
	styleUrls: ['./new-platform-type-form-page2.component.sass'],
	imports: [
		NewPlatformTypeFormComponent,
		MatOptionLoadingComponent,
		EnumSetFormComponent,
		NgIf,
		NgFor,
		AsyncPipe,
		FormsModule,
		MatFormFieldModule,
		MatSelectModule,
		MatIconModule,
		MatDialogModule,
		MatButtonModule,
		MatStepperModule,
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
})
export class NewPlatformTypeFormPage2Component implements OnChanges {
	constructor(private enumSetService: EnumerationUIService) {}
	enumSetState = new BehaviorSubject<enumerationSet | undefined>(undefined);

	createNewEnum = new BehaviorSubject<boolean>(false);

	logicalTypeSubject: BehaviorSubject<logicalType> = new BehaviorSubject({
		id: '-1',
		name: '',
		idString: '-1',
		idIntValue: -1,
	});

	@Input() logicalType: logicalType = {
		id: '-1',
		name: '',
		idString: '-1',
		idIntValue: -1,
	};
	_enumSets = this.enumSetService.enumSets;
	platformType = new BehaviorSubject<Partial<PlatformType>>({});
	enumSets = combineLatest([this.platformType, this._enumSets]).pipe(
		switchMap(([pType, enumSets]) =>
			of({
				bitSize: pType.interfacePlatformTypeBitSize || '0',
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
											parseInt(bitSize)
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

	@Output() typeFormState = combineLatest([
		this.platformType,
		this.enumSetState,
		this.createNewEnum,
	]).pipe(
		map(([pType, enumSet, create]) => {
			return {
				platformType: pType,
				createEnum: create,
				enumSet: enumSet,
				enumSetName: enumSet?.name || '',
				enumSetId: enumSet?.id || '-1',
				enumSetDescription: enumSet?.description || '',
				enumSetApplicability: enumSet?.applicability || {
					id: '1',
					name: 'Base',
				},
				enums: enumSet?.enumerations || [],
			};
		})
	);

	/**
	 * Updates the platform type to the current state from osee-new-platform-type-form
	 */
	attributesUpdate(value: Partial<PlatformType>) {
		this.logicalTypeSubject
			.pipe(
				take(1),
				map((logicalTypeSelected) => {
					return {
						...value,
						interfaceLogicalType: logicalTypeSelected.name,
					};
				}),
				tap((v) => this.platformType.next(v))
			)
			.subscribe();
	}

	/**
	 * Updates the enum set's state in this form
	 */
	updateEnumSet(value: enumerationSet) {
		this.enumSetState.next(value);
	}

	/**
	 * Switches between enum creation and enum selection modes for logical type === enumeration
	 */
	toggleEnumCreationState(event?: Event) {
		event?.stopPropagation();
		this.createNewEnum.next(!this.createNewEnum.getValue());
	}

	/**
	 * Comparator for enum sets
	 */
	compareEnumSet(o1: enumerationSet, o2: enumerationSet) {
		return o1?.id === o2?.id && o1?.name === o2?.name;
	}

	ngOnChanges(changes: SimpleChanges) {
		if (
			changes.logicalType.currentValue !==
			this.logicalTypeSubject.getValue()
		) {
			this.logicalTypeSubject.next(changes.logicalType.currentValue);
		}
	}
}
