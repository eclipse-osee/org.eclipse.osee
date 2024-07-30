/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ApiKeyGeneratorComponent } from './api-key-generator.component';
import { ApiKeyService } from '../../services/api-key.service';
import { ApiKeyServiceMock } from '../../services/testing/api-key.service.mock';
import { MatDialogRef } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ApiKeyGeneratorComponent', () => {
	let component: ApiKeyGeneratorComponent;
	let fixture: ComponentFixture<ApiKeyGeneratorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ApiKeyGeneratorComponent, NoopAnimationsModule],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: ApiKeyService,
					useValue: ApiKeyServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ApiKeyGeneratorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
