@echo on

@set BASHPATH="C:\cygwin64\bin\bash"
@set PROJECTDIR="/cygdrive/C/Users/ProRock/AndroidStudioProjects/MusicofNature/app/OpenAL"
@set NDKDIR="/cygdrive/C/Users/ProRock/AppData/Local/Android/sdk/ndk-bundle/build/ndk-build"

%BASHPATH% --login -c "cd %PROJECTDIR% && %NDKDIR%

@pause: