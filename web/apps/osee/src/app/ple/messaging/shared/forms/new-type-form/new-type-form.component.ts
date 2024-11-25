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
import { KeyValuePipe, TitleCasePipe } from '@angular/common';
import {
	Component,
	effect,
	model,
	output,
	signal,
	inject,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import {
	MatDialogActions,
	MatDialogContent,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatLabel } from '@angular/material/form-field';
import {
	MatStep,
	MatStepContent,
	MatStepper,
	MatStepperNext,
	MatStepperPrevious,
} from '@angular/material/stepper';
import { applic } from '@osee/applicability/types';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { TypesService } from '@osee/messaging/shared/services';
import type { PlatformType, logicalType } from '@osee/messaging/shared/types';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import { Observable } from 'rxjs';
import { LogicalTypeSelectorComponent } from '../logical-type-selector/logical-type-selector.component';
import { NewPlatformTypeFormPage2Component } from '../new-platform-type-form-page2/new-platform-type-form-page2.component';
import { FormsModule } from '@angular/forms';

/**
 * Form used to create a new platform type
 */
@Component({
	selector: 'osee-new-type-form',
	imports: [
		MatDialogTitle,
		MatStepper,
		MatStep,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatStepperNext,
		MatLabel,
		MatStepperPrevious,
		TitleCasePipe,
		KeyValuePipe,
		LogicalTypeSelectorComponent,
		NewPlatformTypeFormPage2Component,
		MatStepContent,
		FormsModule,
	],
	templateUrl: './new-type-form.component.html',
	styles: [':host{width: 100%;height: 100%;}'],
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class NewTypeFormComponent {
	private typesService = inject(TypesService);

	logicalTypes: Observable<logicalType[]> = this.typesService.logicalTypes;

	private __lt = toSignal(this.logicalTypes, { initialValue: [] });
	selectedLogicalType = signal<logicalType>({
		id: '-1',
		name: '',
		idString: '-1',
		idIntValue: -1,
	});

	platformType = model<PlatformType>(new PlatformTypeSentinel());

	private _updateLogicalTypeBasedOnPlatformType = effect(
		() => {
			const foundType = this.__lt().find(
				(v) =>
					v.name.toLowerCase() ===
					this.platformType().interfaceLogicalType.value.toLowerCase()
			);
			if (foundType) {
				this.setLogicalType(foundType);
			}
		},
		{ allowSignalWrites: true }
	);

	private _removeIds = effect(() => {
		//if the platform type, or it's attributes have ids they should be zeroized to -1
		//enum sets and enums should continue to have their ids until the user selects to create a new enumset/enum
		const _platformType = this.platformType();
		_platformType.id = '-1';
		_platformType.description.id = '-1';
		_platformType.interfaceDefaultValue.id = '-1';
		_platformType.interfaceLogicalType.id = '-1';
		_platformType.interfacePlatformType2sComplement.id = '-1';
		_platformType.interfacePlatformTypeAnalogAccuracy.id = '-1';
		_platformType.interfacePlatformTypeBitSize.id = '-1';
		_platformType.interfacePlatformTypeBitsResolution.id = '-1';
		_platformType.interfacePlatformTypeCompRate.id = '-1';
		_platformType.interfacePlatformTypeMaxval.id = '-1';
		_platformType.interfacePlatformTypeMinval.id = '-1';
		_platformType.interfacePlatformTypeMsbValue.id = '-1';
		_platformType.interfacePlatformTypeUnits.id = '-1';
		_platformType.interfacePlatformTypeValidRangeDescription.id = '-1';
		_platformType.name.id = '-1';
	});
	closeForm = output<boolean>();

	resultType = output<PlatformType>();

	/**
	 * Sets the current logical type
	 */
	setLogicalType(value: logicalType) {
		this.selectedLogicalType.set(value);
	}
	/**
	 * Closes the form and returns a result
	 */
	triggerClose() {
		this.closeForm.emit(true);
		this.resultType.emit(this.platformType());
	}

	isApplic(value: unknown): value is applic {
		return (
			(value as unknown) !== undefined &&
			(value as applic).id !== undefined &&
			(value as applic).name !== undefined
		);
	}
}
