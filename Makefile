# This will work on the Mac, but linux people probably install Processing in different places
# If you're getting errors, just replace this line with the location of your Processing install
processing_dir=$(shell ls -d /Applications/Processing* | tail -1)
all:
	javac src/geomerative/*.java -d . -cp external/batikfont.jar:"$(processing_dir)/lib/core.jar":"$(processing_dir)/libraries/xml/library/xml.jar"
	jar cvf library/geomerative.jar geomerative/

