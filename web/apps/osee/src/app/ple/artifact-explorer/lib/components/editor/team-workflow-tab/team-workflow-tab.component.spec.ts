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
import { WorkflowService } from '../../../../../../actra/services/workflow.service';
import { WorkflowServiceMock } from '../../../../../../actra/workflow/workflow.service.mock';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';
import { ActionService } from '@osee/configuration-management/services';
import {
	ActionDropdownStub,
	actionServiceMock,
	CommitManagerButtonStub,
} from '@osee/configuration-management/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { NgClass } from '@angular/common';
import {
	AttributesEditorComponent,
	ExpansionPanelComponent,
} from '@osee/shared/components';
import { CreateActionWorkingBranchButtonComponent } from '@osee/configuration-management/components';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { UpdateFromParentButtonComponentMock } from '@osee/commit/testing';

describe('TeamWorkflowTabComponent', () => {
	let component: TeamWorkflowTabComponent;
	let fixture: ComponentFixture<TeamWorkflowTabComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(TeamWorkflowTabComponent, {
			set: {
				imports: [
					NgClass,
					ExpansionPanelComponent,
					CreateActionWorkingBranchButtonComponent,
					ActionDropdownStub,
					AttributesEditorComponent,
					UpdateFromParentButtonComponentMock,
					MatButton,
					MatIcon,
					MatTooltip,
					NgClass,
					CommitManagerButtonStub,
				],
			},
		})
			.configureTestingModule({
				imports: [TeamWorkflowTabComponent],
				providers: [
					provideNoopAnimations(),
					{ provide: ActionService, useValue: actionServiceMock },
					{
						provide: ArtifactExplorerHttpService,
						useValue: ArtifactExplorerHttpServiceMock,
					},
					{
						provide: WorkflowService,
						useValue: WorkflowServiceMock,
					},
					{
						provide: TransactionService,
						useValue: transactionServiceMock,
					},
					{
						provide: ActivatedRoute,
						useValue: {
							queryParamMap: of(new Map<string, string>()),
						},
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(TeamWorkflowTabComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('teamWorkflowId', '1234');
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
