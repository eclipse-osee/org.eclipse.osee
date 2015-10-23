#!/bin/sh
export BASE_AREA=`pwd | sed 's/\/c\//\/c\:\//'`
mvn clean verify -Dosee.x.core.p2=file://$BASE_AREA/../org.eclipse.osee.x.core.p2/target/repository -Dosee.base.p2=file://$BASE_AREA/../org.eclipse.osee.client.all.p2/target/repository 