#!/bin/bash

set -e

if [ -f /etc/os-release ]; then
    . /etc/os-release
else
    echo "Unable to determine operating system."
    exit 1
fi

# Vérification via une structure case
case "$ID" in
    ubuntu)
        echo "Ubuntu detected : $PRETTY_NAME"

        echo  install of requirements
        sudo apt install python3-virtualenv
        virtualenv venv && . ./venv/bin/activate && pip install -r requirements.txt

        echo  build of ardupilot
        sudo apt update && sudo apt install -y \
            git python3 python3-pip \
            python3-dev python3-future \
            build-essential libncurses-dev \
            libpython3-dev libxml2-dev libxslt1-dev \
            libffi-dev libyaml-dev

        sudo apt-get install libgstreamer1.0-dev libgstreamer-plugins-base1.0-dev libgstreamer-plugins-bad1.0-dev gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad gstreamer1.0-plugins-ugly gstreamer1.0-libav gstreamer1.0-tools gstreamer1.0-x gstreamer1.0-alsa gstreamer1.0-gl gstreamer1.0-gtk3 gstreamer1.0-qt5 gstreamer1.0-pulseaudio
        ;;
    debian)
        echo "Debian detected. Ce script est prévu pour Ubuntu, il pourrait fonctionner avec des ajustements."
        ;;

    fedora)
        echo "Fedora detected. Ce script est prévu pour Ubuntu, il pourrait fonctionner avec des ajustements."
        ;;

    arch)
        echo "Arch Linux detected. Ce script est prévu pour Ubuntu, il pourrait fonctionner avec des ajustements."
        ;;
    *)
        echo "Système non supporté : $NAME"
        exit 1
        ;;
esac

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
echo  source ./venv/bin/activate
echo  cd ardupilot
echo  ./Tools/autotest/sim_vehicle.py -v ArduSub --out=udp:127.0.0.1:14551 --console --map
echo
echo  To launch QGroundControl
echo  cd qgc
echo  ./QGroundControl.AppImage