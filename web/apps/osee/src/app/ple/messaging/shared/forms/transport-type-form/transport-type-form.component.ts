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
import { Component, Input, Output, inject } from '@angular/core';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent } from '@angular/material/dialog';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MessageTypeDropdownComponent } from '@osee/messaging/shared/dropdowns';
import { HeaderService } from '@osee/messaging/shared/services';
import {
	INTERFACELEVELS,
	TransportType,
	TransportTypeForm,
	transportTypeAttributes,
} from '@osee/messaging/shared/types';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import { ParentErrorStateMatcher } from '@osee/shared/matchers';
import { Subject } from 'rxjs';

@Component({
	selector: 'osee-transport-type-form',
	standalone: true,
	imports: [
		FormsModule,
		AsyncPipe,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatInput,
		MatSlideToggle,
		MatHint,
		MatSelect,
		MatOption,
		MatDialogActions,
		MatButton,
		MessageTypeDropdownComponent,
		ApplicabilitySelectorComponent,
	],
	templateUrl: './transport-type-form.component.html',
	styles: [],
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
