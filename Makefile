
JAVAC_ARGS=-source 1.3 -target 1.1
#
# TODO: add check that PROCESSING_PATH is defined
#

library/geomerative.jar: src/geomerative/*.java
	mkdir -p build
	mkdir -p library
	javac $(JAVAC_ARGS) src/geomerative/*.java -d build -cp external/batikfont.jar:"$(PROCESSING_PATH)/lib/core.jar":"$(PROCESSING_PATH)/libraries/xml/library/xml.jar"
	jar cvf library/geomerative.jar -C build geomerative


test: library/geomerative.jar test/geomerative/*.java
	mkdir -p build/test
	javac test/geomerative/*.java -d build/test -cp library/geomerative.jar:external/batikfont.jar:external/junit.jar:"$(PROCESSING_PATH)/lib/core.jar":"$(PROCESSING_PATH)/libraries/xml/library/xml.jar"
	java -cp build/test:library/geomerative.jar:external/batikfont.jar:external/junit.jar:"$(PROCESSING_PATH)/lib/core.jar":"$(PROCESSING_PATH)/libraries/xml/library/xml.jar" geomerative.ClipTest


clean:
	rm -rf build
	rm -rf tests_build
	rm -rf library/geomerative.jar
