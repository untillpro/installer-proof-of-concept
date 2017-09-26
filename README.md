# installer-proof-of-concept

Can windows service runs an upgrade tool which 
- stops the service
- reboots the PC
- upgrades the service
- runs the service

## Build
```batch
gradlew createWindowsService jar
```
Result:
- build\windows-service - service
- build\libs - installer jar

## Install service
Require elevated command prompt.
```batch
java -jar build\libs\installer-proof-of-concept.jar install
```

## Upgrade service from service
```batch
echo java -jar ..\installer-proof-of-concept.jar upgrade >build\libs\TestService\doRun
```

## Upgrade service from service with reboot
```batch
echo java -jar ..\installer-proof-of-concept.jar rebootAndUpgrade >build\libs\TestService\doRun
```
