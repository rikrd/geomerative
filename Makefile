
GEOMERATIVE_VERSION=23

JAVAC_ARGS=-source 1.3 -target 1.1
CLASSPATH=external/batikfont.jar:"$(PROCESSING_PATH)/lib/core.jar":"$(PROCESSING_PATH)/libraries/xml/library/xml.jar"
TEST_CLASSPATH=library/geomerative.jar:external/junit.jar:$(CLASSPATH)
DIST_DIR=distribution/geomerative

#
# TODO: add check that PROCESSING_PATH is defined
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

        ##  Copy libs
	cp library/geomerative.jar $(DIST_DIR)/library
	cp external/batikfont.jar $(DIST_DIR)/library

        ##  Copy docs
	cp README $(DIST_DIR)
	cp COPYING $(DIST_DIR)
	cp HANDBOOK $(DIST_DIR)

        ##  Copy files
	cp -r examples $(DIST_DIR)
	cp -r tutorial $(DIST_DIR)
	cp -r src $(DIST_DIR)

        ##  Zip up
	rm -f distribution/geomerative-*.zip
	cd distribution && zip -r geomerative-$(GEOMERATIVE_VERSION).zip .

doc:
	mkdir -p $(DIST_DIR)
	javadoc -classpath $(CLASSPATH) -doclet prodoc.StartDoclet -docletpath external -sourcepath src geomerative
	rm -rf $(DIST_DIR)/documentation
	mv -f src/documentation $(DIST_DIR)
	cp templates/stylesheet.css $(DIST_DIR)/documentation/


clean:
	rm -rf build
	rm -rf distribution

clean_all: clean
	rm -rf library
