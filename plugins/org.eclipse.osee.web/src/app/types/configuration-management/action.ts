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

export interface action {
	id: number;
	Name: string;
	AtsId: string;
	ActionAtsId: string;
	TeamWfAtsId: string;
	ArtifactType: string;
	actionLocation: string;
}
export class actionImpl implements action {
	id: number = 0;
	Name: string = '';
	AtsId: string = '';
	ActionAtsId: string = '';
	TeamWfAtsId: string = '';
	ArtifactType: string = ' ';
	actionLocation: string = '';
}
