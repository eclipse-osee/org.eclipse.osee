#!/bin/bash

if [ -z "$2" ]; then	
	echo "Usage: update_versions <from_version> <to_version> "	
	exit	
fi
FROM_VERSION=$1
TO_VERSION=$2

#########################################################################################
# MANIFEST.MF	[FROM].qualifier [TO].qualifier
# feature.xml 	[FROM].qualifier [TO].qualifier
# pom.xml 	<version>[FROM]-SNAPSHOT</version> <version>[TO]-SNAPSHOT</version>
# category.xml 	[FROM].qualifier [TO].qualifier
# .product 	[FROM].qualifier [TO].qualifier
#########################################################################################


sed -i 's/'"$FROM_VERSION"'.qualifier/'"$TO_VERSION"'.qualifier/g' */*/META-INF/MANIFEST.MF
sed -i 's/'"$FROM_VERSION"'.qualifier/'"$TO_VERSION"'.qualifier/g' */*/feature.xml
sed -i 's#<version>'"$FROM_VERSION"'-SNAPSHOT</version>#<version>'"$TO_VERSION"'-SNAPSHOT</version>#g' */*/pom.xml
sed -i 's/'"$FROM_VERSION"'.qualifier/'"$TO_VERSION"'.qualifier/g' */*/category.xml
sed -i 's/'"$FROM_VERSION"'.qualifier/'"$TO_VERSION"'.qualifier/g' */*/*.product
sed -i 's/'$FROM_VERSION'-SNAPSHOT/'$TO_VERSION'-SNAPSHOT/g' plugins/org.eclipse.osee.server.parent/pom.xml

git add -A
if [ ! -z "$1" ]; then
	git commit -m "refactor: Update build numbers to $TO_VERSION"
fi
