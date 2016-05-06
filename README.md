# Infinity ErgoDox layout and Kiibohd kll compiler

Forked and customized from [fredZen;s project](https://github.com/fredZen/ergodox-infinity-layout).

My layout for the [Infinity ErgoDox](http://input.club/devices/infinity-ergodox) keyboard.

## Editing

The layout files are in `kiibohd/ergodox-*.kll`.

## Workflow

The Docker image used by the compile script is available from
[Docker Hub](https://hub.docker.com/r/fmerizen/ergodox-infinity-layout/).

1. Edit the .kll files in the `kiibohd` folder
2. When adding or removing a layer, change the value of PartialMaps in `kiibohd/ergodox.bash` accordingly
3. Run `./compile.sh`
4. The compiled firmware is now available as `kiibohd/*.dfu.bin`.
5. Flash the keyboard with [dfu-util](https://github.com/kiibohd/controller/wiki/Loading-DFU-Firmware). 

It's enough to flash the master half of the keyboard (the one that's plugged into the keyboard).
However, you might be surprised if you ever switch the master to the other half -- your layout is
determined by the firmware of the master half; keeping both halves in sync may spare you a lot
of confusion.
