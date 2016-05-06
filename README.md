# Infinity ErgoDox layout and Kiibohd kll compiler

Forked and customized from [fredZen's project](https://github.com/fredZen/ergodox-infinity-layout).

My layout for the [Infinity ErgoDox](http://input.club/devices/infinity-ergodox) keyboard.

## Workflow

The Docker image used by the compile script is available from
[Docker Hub](https://hub.docker.com/r/fmerizen/ergodox-infinity-layout/).

1. Edit the `ergodox-*.kll` files in the `kiibohd` folder
2. When adding or removing a layer, change the value of PartialMaps in `kiibohd/ergodox.bash` accordingly
3. Run `./compile.sh`
4. The compiled firmware is now available as as files `kiibohd/left_kiibohd.dfu.bin` and `kiibohd/right_kiibohd.dfu.bin`
5. Flash the keyboard (see below)

It's enough to flash the master half of the keyboard (the one that's plugged into the host computer).
However, you might be surprised if you ever switch the master to the other half -- your layout is
determined by the firmware of the master half; keeping both halves in sync may spare you a lot
of confusion.

## Flashing

You need to install [dfu-util](https://github.com/kiibohd/controller/wiki/Loading-DFU-Firmware).  For me, this was `brew install dfu-util`.

Place your keyboard into flash mode, by hitting the switch on the bottom of the PCB, or using a key sequence that enables flashing.

Execute `dfu-util -D kiibohd/left_kiibohd.dfu.bin`:

```
18:56:59 ~/workspaces/github/ergodox-infinity-layout > dfu-util -D kiibohd/left_kiibohd.dfu.bin
dfu-util 0.9

Copyright 2005-2009 Weston Schmidt, Harald Welte and OpenMoko Inc.
Copyright 2010-2016 Tormod Volden and Stefan Schmidt
This program is Free Software and has ABSOLUTELY NO WARRANTY
Please report bugs to http://sourceforge.net/p/dfu-util/tickets/

Match vendor ID from file: 1c11
Match product ID from file: b007
Opening DFU capable USB device...
ID 1c11:b007
Run-time device DFU version 0110
Claiming USB DFU Interface...
Setting Alternate Setting #0 ...
Determining device status: state = dfuIDLE, status = 0
dfuIDLE, continuing
DFU mode device DFU version 0110
Device returned transfer size 1024
Copying data from PC to DFU device
Download	[=========================] 100%        43432 bytes
Download done.
state(7) = dfuMANIFEST, status(0) = No error condition is present
dfu-util: unable to read DFU status after completion
18:58:47 ~/workspaces/github/ergodox-infinity-layout >
```

Obiously, if you use the right half of your Ergodox as the master, flash using the other file.
