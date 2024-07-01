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

import { ArtifactOperationsContextMenuComponent } from './artifact-operations-context-menu.component';
import { TransactionService } from '@osee/shared/transactions';
import { transactionServiceMock } from '@osee/shared/transactions/testing';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { artifactHierarchyPathServiceMock } from '../../../testing/artifact-hierarchy-path.service.mock';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactExplorerHttpServiceMock } from '../../../testing/artifact-explorer-http.service.mock';

describe('ArtifactOperationsContextMenuComponent', () => {
	let component: ArtifactOperationsContextMenuComponent;
	let fixture: ComponentFixture<ArtifactOperationsContextMenuComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ArtifactOperationsContextMenuComponent],
			providers: [
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
				{
					provide: ArtifactHierarchyPathService,
					useValue: artifactHierarchyPathServiceMock,
				},
				{
					provide: ArtifactExplorerHttpService,
					useValue: ArtifactExplorerHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(
			ArtifactOperationsContextMenuComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
