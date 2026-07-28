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
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	input,
	signal,
	viewChild,
} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { artifactTab } from '../../../types/artifact-explorer';
import { ArtifactInfoPanelComponent } from '../artifact-info-panel/artifact-info-panel.component';
import { AttributesEditorPanelComponent } from '../attributes-editor-panel/attributes-editor-panel.component';
import { RelationsEditorPanelComponent } from '../relations-editor-panel/relations-editor-panel.component';
import { ArtifactHistoryPanelComponent } from '../artifact-history-panel/artifact-history-panel.component';
import { HelpTopicRegistryService } from '@osee/shared/components';
import { HelpButtonComponent } from '@osee/shared/components';
import { HelpAnchorDirective } from '@osee/shared/components';

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
		HelpButtonComponent,
		HelpAnchorDirective,
	],
	templateUrl: './artifact-editor.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ArtifactEditorComponent {
	private readonly helpRegistry = inject(HelpTopicRegistryService);

	tab = input.required<artifactTab>();

	/** Which editor section is currently visible. */
	protected activeSection = signal<EditorSection>('attributes');

	/** Whether delete mode is active (shows × on deletable attributes). */
	protected deleteMode = signal(false);

	/** Reference to the attributes panel for triggering add dialog. */
	private readonly attrPanel =
		viewChild<AttributesEditorPanelComponent>('attrPanel');

	private readonly _registerHelp = this.helpRegistry.register({
		id: 'attribute-editor',
		label: 'Attribute Editor',
		markdownPath: 'assets/help/attribute-editor/overview.md',
		sections: [
			{ id: 'editing', label: 'Editing', anchorId: 'attr-panel' },
			{
				id: 'toolbar-actions',
				label: 'Toolbar Actions',
				anchorId: 'attr-toolbar',
			},
			{
				id: 'adding-attributes',
				label: 'Adding Attributes',
				anchorId: 'attr-add-btn',
			},
			{
				id: 'deleting-attributes',
				label: 'Deleting Attributes',
				anchorId: 'attr-delete-btn',
			},
			{
				id: 'grouped-attributes',
				label: 'Grouped Attributes',
				anchorId: '',
			},
		],
	});

	protected openAddAttributeDialog() {
		this.attrPanel()?.openAddAttributeDialog();
	}

	protected toggleDeleteMode() {
		this.deleteMode.update((v) => !v);
	}
}
