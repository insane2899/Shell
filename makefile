Terminal:	Terminal.jar
	echo '#!/usr/bin/java -jar' > Terminal
	cat Terminal.jar >> Terminal
	chmod +x Terminal

Terminal.jar:	Color.jar jline-3.9.0.jar Shell.java
	javac -cp Color.jar:jline-3.9.0.jar Shell.java
	printf "Manifest-Version: 1.0\nClass-Path: Color.jar jline-3.9.0.jar\nMain-Class: Shell\n" >> MANIFEST.MF
	jar -cvfm Terminal.jar ./MANIFEST.MF Shell.class

clean:
	rm Terminal.jar MANIFEST.MF Shell.class Terminal
