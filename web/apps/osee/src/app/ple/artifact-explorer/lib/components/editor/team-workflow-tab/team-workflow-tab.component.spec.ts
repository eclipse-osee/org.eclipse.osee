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
import { TeamWorkflowTabComponent } from './team-workflow-tab.component';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactExplorerHttpServiceMock } from '../../../testing/artifact-explorer-http.service.mock';
import { TeamWorkflowService } from '../../../services/team-workflow.service';
import { teamWorkflowServiceMock } from '../../../testing/team-workflow.service.mock';
import { TransactionService } from '@osee/shared/transactions';
import { transactionServiceMock } from '@osee/shared/transactions/testing';
import { ActionService } from '@osee/configuration-management/services';
import { actionServiceMock } from '@osee/configuration-management/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('TeamWorkflowTabComponent', () => {
	let component: TeamWorkflowTabComponent;
	let fixture: ComponentFixture<TeamWorkflowTabComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [TeamWorkflowTabComponent],
			providers: [
				{ provide: ActionService, useValue: actionServiceMock },
				{
					provide: ArtifactExplorerHttpService,
					useValue: ArtifactExplorerHttpServiceMock,
				},
				{
					provide: TeamWorkflowService,
					useValue: teamWorkflowServiceMock,
				},
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
				{
					provide: ActivatedRoute,
					useValue: { queryParamMap: of(new Map<string, string>()) },
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(TeamWorkflowTabComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
