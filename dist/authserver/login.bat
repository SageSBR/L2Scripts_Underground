@echo off
title Login
:start

SET java_opts=-Xms1G
SET java_opts=%java_opts% -Xmx1G

SET java_settings=%java_settings% -Dfile.encoding=UTF-8
SET java_settings=%java_settings% -Djava.net.preferIPv4Stack=true

java -server %java_settings% %java_opts% -cp config;./../lib/*;authserver.jar l2s.authserver.AuthServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin Restart
echo.
goto start
:error
echo.
echo Server terminated abnormaly
echo.
:end
echo.
echo Server terminated
echo.
pause