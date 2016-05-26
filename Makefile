default: compile

compile:
	./compile.sh


install: 
	dfu-util -D kiibohd/left_kiibohd.dfu.bin


