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
import {
	AsyncPipe,
	NgFor,
	NgClass,
	NgSwitch,
	NgSwitchCase,
	NgSwitchDefault,
} from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { CurrentTransportTypePageService } from './lib/services/current-transport-type-page.service';
import {
	MessagingControlsMockComponent,
	transportTypes,
} from '@osee/messaging/shared/testing';
import { Observable, of } from 'rxjs';

import { TransportsComponent } from './transports.component';
import { transportType } from '@osee/messaging/shared/types';
import { transactionResultMock } from '@osee/transactions/testing';
import { transactionResult } from '@osee/transactions/types';
import { AttributeToValuePipe } from '@osee/attributes/pipes';

describe('TransportsComponent', () => {
	let component: TransportsComponent;
	let fixture: ComponentFixture<TransportsComponent>;
	const CurrentTransportTypePageServiceMock: Partial<CurrentTransportTypePageService> =
		{
			getType: function (
				_artId: string
			): Observable<Required<transportType>> {
				return of(transportTypes[0]);
			},
			createType: function (
				_type: transportType
			): Observable<transactionResult> {
				return of(transactionResultMock);
			},
			transportTypes: of(transportTypes),
			types: of(transportTypes),
		};

	beforeEach(async () => {
		await TestBed.overrideComponent(TransportsComponent, {
			set: {
				imports: [
					AsyncPipe,
					NgFor,
					NgClass,
					NgSwitch,
					NgSwitchCase,
					NgSwitchDefault,
					MatTableModule,
					MatTooltipModule,
					MatButtonModule,
					MatIconModule,
					MessagingControlsMockComponent,
					MatMenuModule,
					AttributeToValuePipe,
				],
			},
		})
			.configureTestingModule({
				imports: [
					MatTableModule,
					MatButtonModule,
					MatDialogModule,
					MatTooltipModule,
					MatIconModule,
					NoopAnimationsModule,
					MessagingControlsMockComponent,
					TransportsComponent,
				],
				providers: [
					{
						provide: CurrentTransportTypePageService,
						useValue: CurrentTransportTypePageServiceMock,
					},
					{
						provide: ActivatedRoute,
						useValue: {
							paramMap: of(
								convertToParamMap({
									branchType: 'working',
									branchId: '10',
								})
							),
						},
					},
					{
						provide: MatDialog,
						useValue: {
							open() {
								return {
									afterClosed() {
										return of({
											name: 'ETHERNET',
											byteAlignValidation: false,
											byteAlignValidationSize: 0,
											messageGeneration: false,
											messageGenerationPosition: '',
											messageGenerationType: '',
										});
									},
									close: null,
								};
							},
						},
					},
				],
				declarations: [],
			})
			.compileComponents();

		const _dialogRefSpy = jasmine.createSpyObj({
			afterClosed: of({
				name: 'ETHERNET',
				byteAlignValidation: false,
				byteAlignValidationSize: 0,
				messageGeneration: false,
				messageGenerationPosition: '',
				messageGenerationType: '',
			}),
			close: null,
		});
		fixture = TestBed.createComponent(TransportsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
