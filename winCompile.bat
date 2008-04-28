cd C:\Down\=Master\Geomerative
javac -target 1.1 -classpath "C:\Down\=Software\processing-0098\lib\core.jar;C:\Down\=Master\Geomerative\external\batikfont.jar" -source 1.3 -d .\library .\geomerative\*.java
cd library
jar -cf .\geomerative.jar .\geomerative
cd ..
copy .\library\geomerative.jar "..\..\=Software\processing-0098\libraries\geomerative\library"
