# patch between Maven and OSGi version syntax (= the separator for the qualifier part)
sed -i "s:-$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:.$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:g" ../versiontiger.repository/pom.xml
sed -i "s:-$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:.$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:g" ../com.inventage.tools.versiontiger/pom.xml
sed -i "s:-$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:.$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:g" ../com.inventage.tools.versiontiger.ui/pom.xml
sed -i "s:-$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:.$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:g" ../com.inventage.tools.versiontiger.gui/pom.xml
sed -i "s:-$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:.$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:g" ../com.inventage.tools.versiontiger.universedefinition/pom.xml
sed -i "s:-$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:.$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:g" ../com.inventage.tools.versiontiger.test/pom.xml
sed -i "s:-$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:.$BUILD_NUMBER-$GIT_REVISION</version><!-- DONT TOUCH THIS COMMENT -->:g" ../versiontiger-maven-plugin/pom.xml
