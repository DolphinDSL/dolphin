mvn clean install
rm -fr  $1/plugins-dev/dolphin/lib/*
cp dolphin-neptus/target/dolphin-neptus-0.1-SNAPSHOT.jar $1/plugins-dev/dolphin/lib/
