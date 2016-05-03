rm -f stagingRepositoryId.txt
stagingRepositoryId=`cat ../versiontiger.parent/target/nexus-staging/staging/40b0fe65bfb1.properties | grep stagingRepository.id | sed "s/\bstagingRepository.id=\b//g"`
echo "stagingRepositoryId=$stagingRepositoryId" > stagingRepositoryId.txt
