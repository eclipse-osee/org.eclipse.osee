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
import { CurrentTransportTypeService } from '@osee/messaging/shared/services';
import {
	CurrentTransportTypeServiceMock,
	MessagingControlsMockComponent,
} from '@osee/messaging/shared/testing';
import { of } from 'rxjs';

import { TransportsComponent } from './transports.component';

describe('TransportsComponent', () => {
	let component: TransportsComponent;
	let fixture: ComponentFixture<TransportsComponent>;

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
						provide: CurrentTransportTypeService,
						useValue: CurrentTransportTypeServiceMock,
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

		let dialogRefSpy = jasmine.createSpyObj({
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
