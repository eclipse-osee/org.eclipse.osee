/*********************************************************************
 * Copyright (c) 2026 Boeing
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

import { UserPublicCertificateManagementComponent } from './user-public-certificate-management.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { UserPublicCertificateManagementService } from '../../services/user-public-certificate-management.service';
import { UserPublicCertificateManagementServiceMock } from '../../services/testing/user-public-certificate-management.service.mock';

describe('UserPublicCertificateManagementComponent', () => {
	let component: UserPublicCertificateManagementComponent;
	let fixture: ComponentFixture<UserPublicCertificateManagementComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [UserPublicCertificateManagementComponent],
			providers: [
				{
					provide: UserPublicCertificateManagementService,
					useValue: UserPublicCertificateManagementServiceMock,
				},
				provideHttpClient(),
				provideHttpClientTesting(),
			],
		}).compileComponents();

		fixture = TestBed.createComponent(
			UserPublicCertificateManagementComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
