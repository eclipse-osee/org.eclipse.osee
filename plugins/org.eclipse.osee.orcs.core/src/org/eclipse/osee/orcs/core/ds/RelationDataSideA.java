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

package org.eclipse.osee.orcs.core.ds;

import java.util.Objects;
import java.util.TreeMap;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Audrey Denk
 */
public class RelationDataSideA {

	private final ArtifactId artA;
	private final RelationTypeToken relType;
	private TreeMap<Integer, Pair<ArtifactId, GammaId>> relOrders;

	public RelationDataSideA(ArtifactId artA, RelationTypeToken relType,
			TreeMap<Integer, Pair<ArtifactId, GammaId>> relOrders) {
		this.artA = artA;
		this.relType = relType;
		this.setRelOrders(relOrders);
	}

	public ArtifactId getArtA() {
		return artA;
	}

	public RelationTypeToken getRelType() {
		return relType;
	}

	public void addRelOrder(ArtifactId b_art_id, int relOrder) {
		relOrders.put(relOrder, new Pair<ArtifactId, GammaId>(b_art_id, GammaId.SENTINEL));
	}

	@Override
	public int hashCode() {
		return Objects.hash(artA.getIdString(), relType.getIdString());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RelationDataSideA other = (RelationDataSideA) obj;
		return Objects.equals(artA, other.artA) && Objects.equals(relType, other.relType);
	}

	@Override
	public String toString() {
		return "RelationOrderData [artA=" + artA.getIdString() + ", relType=" + relType.getIdString() + "]";
	}

	public TreeMap<Integer, Pair<ArtifactId, GammaId>> getRelOrders() {
		return relOrders;
	}

	public void setRelOrders(TreeMap<Integer, Pair<ArtifactId, GammaId>> relOrders) {
		this.relOrders = relOrders;
	}
	

}