/*********************************************************************
 * Copyright (c) 2021 Boeing
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
	artifact,
	createArtifact,
	relation,
	transaction,
} from '../../types/transaction';
import { TransactionBuilderService } from '../transaction-builder.service';
import { transactionMock } from './transaction.mock';

export const transactionBuilderMock: Partial<TransactionBuilderService> = {
	createArtifact<T extends Partial<createArtifact>>(
		value: T,
		artifactType: string,
		relations: relation[],
		transaction?: transaction,
		branchId?: string,
		txComment?: string
	) {
		return transactionMock;
	},
	deleteArtifact(
		value: string,
		transaction?: transaction,
		branchId?: string,
		txComment?: string
	) {
		return transactionMock;
	},
	modifyArtifact<T extends Partial<artifact>>(
		value: T,
		transaction?: transaction,
		branchId?: string,
		txComment?: string
	) {
		return transactionMock;
	},
	addRelation(
		typeName?: string,
		typeId?: string,
		firstId?: string,
		secondId?: string,
		rationale?: string,
		transaction?: transaction,
		branchId?: string,
		txComment?: string
	) {
		return transactionMock;
	},
	deleteRelation(
		typeName?: string,
		typeId?: string,
		firstId?: string,
		secondId?: string,
		rationale?: string,
		transaction?: transaction,
		branchId?: string,
		txComment?: string
	) {
		return transactionMock;
	},
};
