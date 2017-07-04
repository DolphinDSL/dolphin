mvn clean install
rm -fr  $1/plugins-dev/dolphin/lib/*
cp nvl-neptus/target/nvl-neptus-0.1-SNAPSHOT.jar $1/plugins-dev/dolphin/lib/
