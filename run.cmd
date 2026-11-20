@echo off
cd /d "%~dp0"
if not exist "target\classes\vn\ptit\qlst9\Main.class" goto build
if not exist "target\lib\h2-2.3.232.jar" goto build
goto run
:build
call mvn "-Dmaven.repo.local=%~dp0tmp\m2" -DskipTests package
if errorlevel 1 exit /b 1
:run
java -cp "target\classes;target\lib\*" vn.ptit.qlst9.Main
