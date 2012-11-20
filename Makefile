
GEOMERATIVE_VERSION=37

JAVAC_ARGS=-source 1.3 -target 1.1
CLASSPATH=external/batikfont.jar:"$(PROCESSING2_PATH)/core/library/core.jar":"$(PROCESSING2_PATH)/libraries/xml/library/xml.jar"
TEST_CLASSPATH=library/geomerative.jar:external/junit.jar:$(CLASSPATH)
DIST_DIR=distribution/geomerative

#
# TODO: add check that PROCESSING2_PATH is defined
#

library/geomerative.jar: src/geomerative/*.java
	mkdir -p build
	mkdir -p library
	javac $(JAVAC_ARGS) src/geomerative/*.java -d build -cp $(CLASSPATH)
	cp external/batikfont.jar library/geomerative.jar
	jar uvf library/geomerative.jar -C build geomerative
	

test: library/geomerative.jar test/geomerative/*.java
	mkdir -p build/test
	javac test/geomerative/*.java -d build/test -cp $(TEST_CLASSPATH)
	java -cp build/test:$(TEST_CLASSPATH) geomerative.ClipTest


dist: library/geomerative.jar doc
	mkdir -p $(DIST_DIR)/library

	##  Copy library.properties
	cp library.properties $(DIST_DIR)

	##  Copy libs
	cp library/geomerative.jar $(DIST_DIR)/library
	cp external/batikfont.jar $(DIST_DIR)/library

	##  Copy docs
	cp README $(DIST_DIR)
	cp COPYING $(DIST_DIR)
	cp HANDBOOK $(DIST_DIR)

	##  Copy files
	cp -r examples $(DIST_DIR)
	#cp -r tutorial $(DIST_DIR)
	cp -r src $(DIST_DIR)

	##  Zip up
	rm -f distribution/geomerative.zip
	cd distribution && zip -r geomerative.zip .
	cp library.properties $(DIST_DIR)/../geomerative.txt

	mkdir -p $(DIST_DIR)/library

doc:
	mkdir -p $(DIST_DIR)
	#javadoc -classpath $(CLASSPATH) -doclet prodoc.StartDoclet -docletpath external -sourcepath src geomerative
	rm -rf $(DIST_DIR)/documentation
	ant -f geomerative-javadoc.xml
	cp -r documentation $(DIST_DIR)


clean:
	rm -rf build
	rm -rf distribution

clean_all: clean
	rm -rf library
