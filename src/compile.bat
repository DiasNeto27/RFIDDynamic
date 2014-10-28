set PROJECT_HOME=%cd%
dir /s /B *.java > sources.txt
javac @sources.txt -cp ".;%PROJECT_HOME%\br\edu\fatecbauru\lib\jna.jar" -d ..\classes 
javac br\edu\fatecbauru\*.java -cp ".;%cd%\src\br\edu\fatecbauru\lib\jna.jar" -d ..\classes