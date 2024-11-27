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
import { PeerReviewDialogComponent } from './peer-review-dialog.component';
import { PeerReviewUiService } from '../../services/peer-review-ui.service';
import { PeerReviewUiServiceMock } from '../../testing/peer-review-ui.service.mock';
import {
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
} from '@angular/material/dialog';
import { dialogRef } from '@osee/messaging/shared/testing';
import { CreatePeerReviewButtonComponentMock } from '../../testing/create-peer-review-button.component.mock';
import { PeerReviewBranchSelectorComponentMock } from '../../testing/peer-review-branch-selector.component.mock';
import {
	MatListItem,
	MatListOption,
	MatSelectionList,
} from '@angular/material/list';
import { MatTooltip } from '@angular/material/tooltip';
import { FormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ManageActionButtonComponentMock } from '@osee/configuration-management/testing';
import { BranchInfoService } from '@osee/shared/services';
import { BranchInfoServiceMock } from '@osee/shared/testing';

describe('PeerReviewDialogComponent', () => {
	let component: PeerReviewDialogComponent;
	let fixture: ComponentFixture<PeerReviewDialogComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(PeerReviewDialogComponent, {
			set: {
				imports: [
					CreatePeerReviewButtonComponentMock,
					PeerReviewBranchSelectorComponentMock,
					MatSelectionList,
					MatListItem,
					MatListOption,
					MatTooltip,
					MatDialogActions,
					MatDialogContent,
					FormsModule,
					MatFormField,
					MatIcon,
					MatLabel,
					MatInput,
					ManageActionButtonComponentMock,
				],
			},
		})
			.configureTestingModule({
				imports: [
					PeerReviewDialogComponent,
					MatSelectionList,
					MatListOption,
					MatTooltip,
					MatDialogActions,
					MatDialogContent,
					FormsModule,
					MatFormField,
					MatIcon,
					MatLabel,
					MatInput,
				],
				providers: [
					provideNoopAnimations(),
					{
						provide: PeerReviewUiService,
						useValue: PeerReviewUiServiceMock,
					},
					{
						provide: BranchInfoService,
						useValue: BranchInfoServiceMock,
					},
					{ provide: MatDialogRef, useValue: dialogRef },
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(PeerReviewDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
