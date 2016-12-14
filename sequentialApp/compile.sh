echo "Building program..." 1>&2
if [ ! -d out ] ; then mkdir out; fi
rm -f out/*.class

javac -cp ~/lib/nemolib.jar -d out ./src/SequentialApp.java
