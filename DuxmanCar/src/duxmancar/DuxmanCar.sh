#!/bin/bash
PI4J_HOME=/home/pi/pi4j/pi4j
CP=$CP:./lib/pi4j-core.jar:./DuxmanCar.jar:./lib/bluecove-2.1.0-SNAPSHOT.jar:./lib/bluecove-gpl-2.1.0-SNAPSHOT.jar./lib/libbluecove_arm.so
sudo java -cp $CP duxmancar.ServoTest

