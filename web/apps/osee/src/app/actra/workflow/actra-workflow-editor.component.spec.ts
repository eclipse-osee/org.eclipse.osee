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
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { UpdateFromParentButtonComponentMock } from '@osee/commit/testing';
import { ActraWorkflowEditorComponent } from './actra-workflow-editor.component';
import { CreateWorkingBranchFromWorkflowButtonComponent } from '../../configuration-management/components/create-working-branch-from-workflow-button/create-working-branch-from-workflow-button';
import { ArtifactUiService } from '@osee/shared/services';
import { artifactUiServiceMock } from '@osee/shared/testing';

describe('ActraWorkflowEditorComponent', () => {
	let component: ActraWorkflowEditorComponent;
	let fixture: ComponentFixture<ActraWorkflowEditorComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ActraWorkflowEditorComponent, {
			set: {
				imports: [
					NgClass,
					ExpansionPanelComponent,
					CreateWorkingBranchFromWorkflowButtonComponent,
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
				imports: [ActraWorkflowEditorComponent],
				providers: [
					provideNoopAnimations(),
					{ provide: ActionService, useValue: actionServiceMock },
					{
						provide: TransactionService,
						useValue: transactionServiceMock,
					},
					{
						provide: ArtifactUiService,
						useValue: artifactUiServiceMock,
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

		fixture = TestBed.createComponent(ActraWorkflowEditorComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('id', '1234');
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
