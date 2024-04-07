/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, Input, Output } from '@angular/core';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import {
	MatError,
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { EnumSetUniqueDescriptionDirective } from '@osee/messaging/shared/directives';
import type { enumeration, enumerationSet } from '@osee/messaging/shared/types';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import { BehaviorSubject, Subject } from 'rxjs';
import { EnumFormComponent } from '../../forms/enum-form/enum-form.component';

@Component({
	selector: 'osee-enum-set-form',
	templateUrl: './enum-set-form.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		MatIconButton,
		MatSuffix,
		MatIcon,
		MatError,
		CdkTextareaAutosize,
		AsyncPipe,
		EnumFormComponent,
		NgFor,
		NgIf,
		EnumSetUniqueDescriptionDirective,
		ApplicabilitySelectorComponent,
	],
	viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class EnumSetFormComponent {
	@Input() bitSize: string = '0';
	enumSet: enumerationSet = {
		name: '',
		description: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
	};

	@Output('enumSet') private _enumSet = new BehaviorSubject<enumerationSet>({
		name: '',
		description: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
	});
	@Output('closed') _closeForm = new Subject();

	updateDescription(value: string) {
		this.enumSet.description = value;
	}

	updateEnums(value: enumeration[]) {
		let enumSet = this._enumSet.getValue();
		enumSet.enumerations = value;
		this._enumSet.next(enumSet);
	}
	updateEnumSet() {
		this._enumSet.next(this.enumSet);
	}
	closeForm() {
		this._closeForm.next(true);
	}
}
