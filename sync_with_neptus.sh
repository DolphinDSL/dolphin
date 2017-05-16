mvn clean install
rm -fr  $1/plugins-dev/nvl_runtime/lib/*
cp nvl-neptus/target/nvl-neptus-0.1-SNAPSHOT.jar $1/plugins-dev/nvl_runtime/lib/
