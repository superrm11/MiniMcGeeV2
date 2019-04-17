# MiniMcGeeV2
2nd version of the MiniMcGee robot series, this time with a swerve drivetrain

This uses most of the same code derived from MiniMcGeeV1, but ditches the mecanum drivetrain, adds support for stepper motors, and adds the swerve drivetrain code. There is no implementation yet for a camera, as there was for V1, but that is coming in the future.

This remains version 2.1 because version 2 was a failed attempt to create a "true" swerve drive, without wires to limit the range of motion. The 3D printed tests did not work, so something more simple was devised.

In order to use this code for yourself, you need to download and compile the JInput library from source on the Raspberry Pi, and install the shared libraries manually. This allows the bluetooth Xbox controller to be seen as a USB HID by the pi. Pair your bluetooth Xbox controller using bluetoothctl and start the program, and you should be good to go.

For images and videos of it working, visit [my Google Photos album](https://photos.app.goo.gl/2zUikkyRRp2dGM538)

Here are the [CAD files](https://www.thingiverse.com/thing:3564919)

<h3>UPDATE</h3>

Version 2.2 is in the works! A CAD assembly is being put together to use 3D printed gears to create a true swerve system, learning from the failed first attempt. From initial testing, it looks like it is going to work this time! Also, it will not use the power hungry and inaccurate stepper motors, but rather a 360 potentiometer and another N20 motor for direction. I will keep this updated!
