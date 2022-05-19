@echo off

set api=%test%
REM ------------------------------------------------------------------------------
REM Create folders
REM ------------------------------------------------------------------------------
rmdir /s /q %api%GPP\%NAME%
REM Feature[ROBOT_SPEAKER=SPKR_B]
mkdir %speaker_b%
REM End Feature

REM ------------------------------------------------------------------------------
REM Remove Files
REM ------------------------------------------------------------------------------
REM Feature[ROBOT_SPEAKER=SPKR_A]
del /s /q %speaker_b%\*
del /s /q %speaker_b%\*
REM End Feature

exit /B %errors%
