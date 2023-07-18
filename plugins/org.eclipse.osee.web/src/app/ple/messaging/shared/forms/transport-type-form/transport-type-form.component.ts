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
import { Component, inject, Input, Output } from '@angular/core';
import { AsyncPipe, NgFor } from '@angular/common';
import {
	INTERFACELEVELS,
	TransportTypeForm,
	TransportType,
	transportTypeAttributes,
} from '@osee/messaging/shared/types';
import { MatDialogModule } from '@angular/material/dialog';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MessageTypeDropdownComponent } from '@osee/messaging/shared/dropdowns';
import { ParentErrorStateMatcher } from '@osee/shared/matchers';
import { Subject } from 'rxjs';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import { HeaderService } from '@osee/messaging/shared/services';

@Component({
	selector: 'osee-transport-type-form',
	standalone: true,
	imports: [
		FormsModule,
		NgFor,
		AsyncPipe,
		MatFormFieldModule,
		MatButtonModule,
		MatInputModule,
		MatSelectModule,
		MatSlideToggleModule,
		MatDialogModule,
		MessageTypeDropdownComponent,
		ApplicabilitySelectorComponent,
	],
	templateUrl: './transport-type-form.component.html',
	styleUrls: ['./transport-type-form.component.sass'],
	viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class TransportTypeFormComponent {
	@Input() transportType: TransportTypeForm = new TransportType();

	private headerService = inject(HeaderService);
	protected messageHeaders = this.headerService.AllMessages;
	protected subMessageHeaders = this.headerService.AllSubMessages;
	protected structureHeaders = this.headerService.AllStructures;
	protected elementHeaders = this.headerService.AllElements;
	protected levels = INTERFACELEVELS;
	parentMatcher = new ParentErrorStateMatcher();

	@Output() completion = new Subject<
		{ type: 'CANCEL' } | { type: 'SUBMIT'; data: transportTypeAttributes }
	>();
	onNoClick() {
		this.completion.next({ type: 'CANCEL' });
	}
	formComplete() {
		this.completion.next({ type: 'SUBMIT', data: this.transportType });
	}
}
