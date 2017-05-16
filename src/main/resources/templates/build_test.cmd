@echo off
rem FOR /L %%i IN (1,1,200000) DO echo %%i ping 127.0.0.1 -n 1 > nul

set /a loop=

:loop
set /a loop=%loop%+1
echo %loop%
ping 127.0.0.1 -n 2 > nul
if %loop%==5 goto ende
goto loop

:ende