%~d0
cd %~p0

cmd /k mvn -U -e -Dmaven.test.skip=true android:ndk-build install
