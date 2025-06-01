@echo off
echo Cleaning bin directory...
if not exist bin mkdir bin
del /s /q bin\*.class 2>nul

echo Compiling...
javac -d bin src\*.java

if %ERRORLEVEL% NEQ 0 (
  echo Compilation failed!
  pause
  exit /b
)

echo Running application...
java -cp bin MainFrame

echo Removing any .class files from src directory...
del /s /q src\*.class 2>nul

echo Done.
pause