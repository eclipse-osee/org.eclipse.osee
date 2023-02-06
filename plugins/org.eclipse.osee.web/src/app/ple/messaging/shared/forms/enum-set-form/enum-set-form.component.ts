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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, Input, Output } from '@angular/core';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Subject, BehaviorSubject } from 'rxjs';
import { applic } from '../../../../../types/applicability/applic';
import { EnumFormComponent } from '../../forms/enum-form/enum-form.component';
import { EnumSetUniqueDescriptionDirective } from '@osee/messaging/shared/directives';
import { enumeration, enumerationSet } from '@osee/messaging/shared/types';
import { ApplicabilityListUIService } from '@osee/messaging/shared/services';

@Component({
	selector: 'osee-enum-set-form',
	templateUrl: './enum-set-form.component.html',
	styleUrls: ['./enum-set-form.component.sass'],
	standalone: true,
	imports: [
		MatFormFieldModule,
		FormsModule,
		MatInputModule,
		MatButtonModule,
		MatIconModule,
		MatSelectModule,
		MatOptionModule,
		AsyncPipe,
		EnumFormComponent,
		NgFor,
		NgIf,
		EnumSetUniqueDescriptionDirective,
	],
	viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class EnumSetFormComponent {
	@Input() bitSize: string = '0';
	applics = this.applicabilityService.applic;
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
	constructor(private applicabilityService: ApplicabilityListUIService) {}

	compareApplics(o1: applic, o2: applic) {
		return o1?.id === o2?.id && o1?.name === o2?.name;
	}
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
