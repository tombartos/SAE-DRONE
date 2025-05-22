#!/bin/bash

set -e

echo  install of requirements
sudo apt install python3-virtualenv
virtualenv venv && . ./venv/bin/activate && pip install -r requirements.txt

echo  build of ardupilot
sudo apt update && sudo apt install -y \
    git python3 python3-pip \
    python3-dev python3-future \
    buildssential libncurses-dev \
    libpython3-dev libxml2-dev libxslt1-dev \
    libffi-dev libyaml-dev

pip3 install MAVProxy

git clone https://github.com/ArduPilot/ardupilot.git
cd ardupilot
git submodule update --init --recursive

. ~/.profile

./waf configure --board sitl
./waf sub

cd ..

echo  build of QGroundControl
mkdir qgc
cd qgc

wget https://d176tv9ibo4jno.cloudfront.net/latest/QGroundControl.AppImage
chmod +x QGroundControl.AppImage

cd ..

echo  Install done
echo 
echo  To launch Ardupilot :
echo  cd ardupilot
echo  ./Tools/autotest/sim_vehicle.py -v ArduSub --out=udp:127.0.0.1:14551 --console --map
echo 
echo  To launch QGroundControl
echo  cd qgc
echo  ./QGroundControl.AppImage
