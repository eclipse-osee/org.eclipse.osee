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
import { Component, input, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { artifactTab } from '../../../types/artifact-explorer';
import { ArtifactInfoPanelComponent } from '../artifact-info-panel/artifact-info-panel.component';
import { AttributesEditorPanelComponent } from '../attributes-editor-panel/attributes-editor-panel.component';
import { RelationsEditorPanelComponent } from '../relations-editor-panel/relations-editor-panel.component';
import { ArtifactHistoryPanelComponent } from '../artifact-history-panel/artifact-history-panel.component';

export type EditorSection = 'attributes' | 'relations' | 'history' | 'info';

@Component({
	selector: 'osee-artifact-editor',
	imports: [
		MatIcon,
		MatIconButton,
		MatTooltip,
		RelationsEditorPanelComponent,
		ArtifactInfoPanelComponent,
		AttributesEditorPanelComponent,
		ArtifactHistoryPanelComponent,
	],
	templateUrl: './artifact-editor.component.html',
})
export class ArtifactEditorComponent {
	tab = input.required<artifactTab>();

	/** Which editor section is currently visible. */
	protected activeSection = signal<EditorSection>('attributes');
}
