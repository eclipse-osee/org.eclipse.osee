/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component, input } from '@angular/core';
import { artifactTab } from '../../../types/artifact-explorer';
import { ArtifactInfoPanelComponent } from '../artifact-info-panel/artifact-info-panel.component';
import { AttributesEditorPanelComponent } from '../attributes-editor-panel/attributes-editor-panel.component';
import { RelationsEditorPanelComponent } from '../relations-editor-panel/relations-editor-panel.component';

@Component({
	selector: 'osee-artifact-editor',
	standalone: true,
	imports: [
		RelationsEditorPanelComponent,
		ArtifactInfoPanelComponent,
		AttributesEditorPanelComponent,
	],
	templateUrl: './artifact-editor.component.html',
})
export class ArtifactEditorComponent {
	tab = input.required<artifactTab>();

	constructor() {}
}
