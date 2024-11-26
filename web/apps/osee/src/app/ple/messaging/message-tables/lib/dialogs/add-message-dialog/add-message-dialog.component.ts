/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import { MessagePeriodicityDropdownComponent } from '@osee/messaging/message-periodicity/message-periodicity-dropdown';
import { MessageTypeDropdownComponent } from '@osee/messaging/message-type/message-type-dropdown';
import { NodeDropdownComponent } from '@osee/messaging/nodes/dropdown';
import { RateDropdownComponent } from '@osee/messaging/rate/rate-dropdown';
import {
	CurrentMessagesService,
	TransportTypeUiService,
} from '@osee/messaging/shared/services';
import { message } from '@osee/messaging/shared/types';
import { writableSlice } from '@osee/shared/utils';
import { AddSubMessageDialogComponent } from '../add-sub-message-dialog/add-sub-message-dialog.component';
import { toSignal } from '@angular/core/rxjs-interop';
import { applicabilitySentinel } from '@osee/applicability/types';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
	selector: 'osee-messaging-add-message-dialog',
	templateUrl: './add-message-dialog.component.html',
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatInput,
		CdkTextareaAutosize,
		MatSlideToggle,
		MatDialogActions,
		MatButton,
		MatDialogClose,
		FormsModule,
		ApplicabilityDropdownComponent,
		RateDropdownComponent,
		MessageTypeDropdownComponent,
		MessagePeriodicityDropdownComponent,
		NodeDropdownComponent,
		MatTooltip,
	],
})
export class AddMessageDialogComponent {
	dialogRef =
		inject<MatDialogRef<AddSubMessageDialogComponent>>(MatDialogRef);
	private currentMessagesService = inject(CurrentMessagesService);
	private transportTypeService = inject(TransportTypeUiService);

	protected data = signal(inject<message>(MAT_DIALOG_DATA));
	private nameAttr = writableSlice(this.data, 'name');
	protected name = writableSlice(this.nameAttr, 'value');
	private descriptionAttr = writableSlice(this.data, 'description');
	protected description = writableSlice(this.descriptionAttr, 'value');
	protected rateAttr = writableSlice(this.data, 'interfaceMessageRate');
	protected periodicityAttr = writableSlice(
		this.data,
		'interfaceMessagePeriodicity'
	);
	private writeAccessAttr = writableSlice(
		this.data,
		'interfaceMessageWriteAccess'
	);
	protected writeAccess = writableSlice(this.writeAccessAttr, 'value');
	protected messageTypeAttr = writableSlice(
		this.data,
		'interfaceMessageType'
	);
	private messageNumberAttr = writableSlice(
		this.data,
		'interfaceMessageNumber'
	);
	protected messageNumber = writableSlice(this.messageNumberAttr, 'value');
	protected publisherNodes = writableSlice(this.data, 'publisherNodes');
	protected subscriberNodes = writableSlice(this.data, 'subscriberNodes');
	protected applicability = writableSlice(this.data, 'applicability');

	transportType = toSignal(this.transportTypeService.currentTransportType, {
		initialValue: {
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: '',
			},
			byteAlignValidation: {
				id: '-1',
				typeId: '1682639796635579163',
				gammaId: '-1',
				value: false,
			},
			messageGeneration: {
				id: '-1',
				typeId: '6696101226215576386',
				gammaId: '-1',
				value: false,
			},
			byteAlignValidationSize: {
				id: '-1',
				typeId: '6745328086388470469',
				gammaId: '-1',
				value: 0,
			},
			messageGenerationType: {
				id: '-1',
				typeId: '7121809480940961886',
				gammaId: '-1',
				value: '',
			},
			messageGenerationPosition: {
				id: '-1',
				typeId: '7004358807289801815',
				gammaId: '-1',
				value: '',
			},
			minimumPublisherMultiplicity: {
				id: '-1',
				typeId: '7904304476851517',
				gammaId: '-1',
				value: 0,
			},
			maximumPublisherMultiplicity: {
				id: '-1',
				typeId: '8536169210675063038',
				gammaId: '-1',
				value: 0,
			},
			minimumSubscriberMultiplicity: {
				id: '-1',
				typeId: '6433031401579983113',
				gammaId: '-1',
				value: 0,
			},
			maximumSubscriberMultiplicity: {
				id: '-1',
				typeId: '7284240818299786725',
				gammaId: '-1',
				value: 0,
			},
			availableMessageHeaders: {
				id: '-1',
				typeId: '2811393503797133191',
				gammaId: '-1',
				value: [],
			},
			availableSubmessageHeaders: {
				id: '-1',
				typeId: '3432614776670156459',
				gammaId: '-1',
				value: [],
			},
			availableStructureHeaders: {
				id: '-1',
				typeId: '3020789555488549747',
				gammaId: '-1',
				value: [],
			},
			availableElementHeaders: {
				id: '-1',
				typeId: '3757258106573748121',
				gammaId: '-1',
				value: [],
			},
			interfaceLevelsToUse: {
				id: '-1',
				typeId: '1668394842614655222',
				gammaId: '-1',
				value: [],
			},
			dashedPresentation: {
				id: '-1',
				typeId: '3564212740439618526',
				gammaId: '-1',
				value: false,
			},
			spareAutoNumbering: {
				id: '-1',
				typeId: '6696101226215576390',
				gammaId: '-1',
				value: false,
			},
			id: '-1',
			gammaId: '-1',
			applicability: applicabilitySentinel,
			directConnection: false,
		},
	});

	onNoClick() {
		this.dialogRef.close();
	}
	compareIds(o1: { id: string }, o2: { id: string }[]) {
		return (
			o1 !== null && o2 !== null && o2.map((v) => v.id).includes(o1.id)
		);
	}
}
