/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { TeamWorkflowService } from '../../ple/artifact-explorer/lib/services/team-workflow.service';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';
import { ActionService } from '@osee/configuration-management/services';
import {
	ActionDropdownStub,
	actionServiceMock,
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
import { ArtifactExplorerHttpService } from '../../ple/artifact-explorer/lib/services/artifact-explorer-http.service';
import { ArtifactExplorerHttpServiceMock } from '../../ple/artifact-explorer/lib/testing/artifact-explorer-http.service.mock';
import { teamWorkflowServiceMock } from '../../ple/artifact-explorer/lib/testing/team-workflow.service.mock';
import { ActraWorkflowComponent } from './actra-workflow.component';

describe('ActraWorkflowComponent', () => {
	let component: ActraWorkflowComponent;
	let fixture: ComponentFixture<ActraWorkflowComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ActraWorkflowComponent, {
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
				],
			},
		})
			.configureTestingModule({
				imports: [ActraWorkflowComponent],
				providers: [
					provideNoopAnimations(),
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
						useValue: {
							queryParamMap: of(new Map<string, string>()),
						},
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(ActraWorkflowComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('workflowId', '1234');
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
