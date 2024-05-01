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
import { artifactExplorerUserPreferences } from '../types/user-preferences';
import { ArtifactExplorerPreferencesHttpService } from '../services/artifact-explorer-preferences-http.service';
import { of } from 'rxjs';

const artifactExplorerUserPreferencesMock: artifactExplorerUserPreferences = {
	id: '1',
	artifactExplorerPanelLocation: false,
};

export const artifactExplorerPreferencesHttpServiceMock: Partial<ArtifactExplorerPreferencesHttpService> =
	{
		getArtifactExplorerPreferences() {
			return of(artifactExplorerUserPreferencesMock);
		},
	};
