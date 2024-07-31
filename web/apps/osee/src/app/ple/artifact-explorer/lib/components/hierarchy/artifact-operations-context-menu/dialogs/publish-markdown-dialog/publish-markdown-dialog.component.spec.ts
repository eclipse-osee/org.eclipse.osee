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

import { PublishMarkdownDialogComponent } from './publish-markdown-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { publishMarkdownDialogDataMock } from 'src/app/ple/artifact-explorer/lib/testing/artifact-explorer.data.mock';
import { ArtifactExplorerHttpService } from '../../../../../../lib/services/artifact-explorer-http.service';
import { ArtifactExplorerHttpServiceMock } from '../../../../../testing/artifact-explorer-http.service.mock';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('PublishMarkdownDialogComponent', () => {
	let component: PublishMarkdownDialogComponent;
	let fixture: ComponentFixture<PublishMarkdownDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [PublishMarkdownDialogComponent, NoopAnimationsModule],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: publishMarkdownDialogDataMock,
				},
				{
					provide: ArtifactExplorerHttpService,
					useValue: ArtifactExplorerHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(PublishMarkdownDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
