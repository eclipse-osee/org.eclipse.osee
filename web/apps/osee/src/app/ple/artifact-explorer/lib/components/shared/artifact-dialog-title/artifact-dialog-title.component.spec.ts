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

import { ArtifactDialogTitleComponent } from './artifact-dialog-title.component';
import { operationTypeMock } from '@osee/artifact-with-relations/types';

describe('ArtifactDialogTitleComponent', () => {
	let component: ArtifactDialogTitleComponent;
	let fixture: ComponentFixture<ArtifactDialogTitleComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ArtifactDialogTitleComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(ArtifactDialogTitleComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('operationType', operationTypeMock);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
