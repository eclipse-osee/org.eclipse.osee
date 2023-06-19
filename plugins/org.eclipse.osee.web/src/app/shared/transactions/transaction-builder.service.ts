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
import { Injectable } from '@angular/core';
import {
	artifact,
	createArtifact,
	attributeType,
	modifyArtifact,
	transaction,
	relation,
} from '../types/transaction';
import { TransactionTranslations } from './transactions.translations';

@Injectable({
	providedIn: 'root',
})
export class TransactionBuilderService {
	translations: TransactionTranslations = new TransactionTranslations();

	constructor() {}

	createArtifact<T extends Partial<createArtifact & artifact>>(
		value: T,
		artifactType: string,
		relations: relation[],
		transaction?: transaction,
		branchId?: string,
		txComment?: string,
		key?: string
	): transaction {
		let currentTransaction: transaction = transaction || {
			branch: branchId || '',
			txComment: txComment || '',
			createArtifacts: [],
		};
		if (currentTransaction.createArtifacts === undefined) {
			currentTransaction.createArtifacts = [];
		}
		let attributes: attributeType[] = [];
		(
			Object.entries(value) as [
				string,
				string | number | boolean | string[]
			][]
		).forEach((entry) => {
			if (
				entry[0] !== 'name' &&
				entry[0] !== 'applicabilityId' &&
				entry[0] !== 'applicability' &&
				entry[0] !== 'id' &&
				!this.translations.contains(entry[0])
			) {
				attributes.push({
					typeName: this.attributeNameToHumanReadable(entry[0]),
					value: entry[1],
				});
			} else if (
				this.translations.contains(entry[0]) &&
				this.translations.transform(entry[0]) !== entry[0]
			) {
				attributes.push({
					typeName: this.translations.transform(entry[0]),
					value: entry[1],
				});
			}
		});
		let artifact: createArtifact = {
			typeId: artifactType,
			name: value?.name || '',
			applicabilityId: value?.applicabilityId || value?.applicability?.id,
			attributes: attributes,
			relations: relations,
			key: key,
		};
		currentTransaction.createArtifacts!.push(artifact);
		return currentTransaction;
	}

	modifyArtifact<T extends Partial<artifact>>(
		value: T,
		transaction?: transaction,
		branchId?: string,
		txComment?: string
	) {
		let currentTransaction: transaction = transaction || {
			branch: branchId || '',
			txComment: txComment || '',
			modifyArtifacts: [],
		};
		let attributes: attributeType[] = [];
		(
			Object.entries(value) as [
				string,
				string | number | boolean | string[]
			][]
		).forEach((entry) => {
			if (
				// entry[0] !== 'name' && //name needs to be modifiable attribute
				entry[0] !== 'applicabilityId' &&
				entry[0] !== 'applicability' &&
				entry[0] !== 'id' &&
				!this.translations.contains(entry[0])
			) {
				attributes.push({
					typeName: this.attributeNameToHumanReadable(entry[0]),
					value: entry[1],
				});
			} else if (
				this.translations.contains(entry[0]) &&
				this.translations.transform(entry[0]) !== entry[0]
			) {
				attributes.push({
					typeName: this.translations.transform(entry[0]),
					value: entry[1],
				});
			}
		});
		let modifyArtifact: modifyArtifact = {
			id: value?.id || '',
			applicabilityId: value?.applicability?.id,
			setAttributes: attributes,
		};
		if (!currentTransaction.modifyArtifacts) {
			currentTransaction.modifyArtifacts = [];
		}
		currentTransaction.modifyArtifacts!.push(modifyArtifact);
		//relation code
		return currentTransaction;
	}

	deleteArtifact(
		value: string,
		transaction?: transaction,
		branchId?: string,
		txComment?: string
	) {
		let currentTransaction: transaction = transaction || {
			branch: branchId || '',
			txComment: txComment || '',
			deleteArtifacts: [],
		};
		currentTransaction.deleteArtifacts!.push(value);
		return currentTransaction;
	}

	deleteArtifacts(values: string[], branchId: string, txComment: string) {
		let transaction: transaction = {
			branch: branchId,
			txComment: txComment,
			deleteArtifacts: [],
		};
		values.forEach((value) => {
			transaction = this.deleteArtifact(value, transaction);
		});
		return transaction;
	}

	modifyArtifacts(
		values: any[],
		branchId: string,
		txComment: string
	): transaction {
		let transaction: transaction = {
			branch: branchId,
			txComment: txComment,
			modifyArtifacts: [],
		};
		values.forEach((value) => {
			transaction = this.modifyArtifact(value, transaction);
		});
		return transaction;
	}

	createArtifacts(
		values: any[],
		relations: relation[][],
		branchId: string,
		txComment: string,
		artifactTypes: string[]
	): transaction {
		let transaction: transaction = {
			branch: branchId,
			txComment: txComment,
			createArtifacts: [],
		};
		values.forEach((value, index) => {
			transaction = this.createArtifact(
				value,
				artifactTypes[index],
				relations[index],
				transaction
			);
		});
		return transaction;
	}

	addRelation(
		typeName?: string,
		typeId?: string,
		firstId?: string,
		secondId?: string,
		afterArtifactId?: string,
		rationale?: string,
		transaction?: transaction,
		branchId?: string,
		txComment?: string
	) {
		let currentTransaction: transaction = transaction || {
			branch: branchId || '',
			txComment: txComment || '',
			addRelations: [],
		};
		if (!currentTransaction?.addRelations) {
			currentTransaction.addRelations = [];
		}
		currentTransaction.addRelations.push({
			typeName: typeName,
			typeId: typeId,
			aArtId: firstId,
			bArtId: secondId,
			afterArtifact: afterArtifactId,
			rationale: rationale,
		});
		return currentTransaction;
	}

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
		let currentTransaction: transaction = transaction || {
			branch: branchId || '',
			txComment: txComment || '',
			deleteRelations: [],
		};
		if (!currentTransaction?.deleteRelations) {
			currentTransaction.deleteRelations = [];
		}
		currentTransaction.deleteRelations.push({
			typeName: typeName,
			typeId: typeId,
			aArtId: firstId,
			bArtId: secondId,
			rationale: rationale,
		});
		return currentTransaction;
	}

	private attributeNameToHumanReadable(value: string): string {
		let values = value
			.match(/[A-Z]+[^A-Z]*|[^A-Z]+/g)
			?.map((val) =>
				val.replace(/(^\w{1})|(\s+\w{1})/g, (letter) =>
					letter.toUpperCase()
				)
			);
		return values!.join(' ');
	}
}
