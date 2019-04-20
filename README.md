# MiniMcGeeV2
A Raspberry Pi powered robot running Java code, with bluetooth or wifi keyboard controls. I created this series of robots to experiment with different drivetrain types, as a technical test to my skills, and because it's fun!

2nd version of the MiniMcGee robot series, this time with a swerve drivetrain

This uses most of the same code derived from MiniMcGeeV1, but ditches the mecanum drivetrain, adds support for stepper motors, and adds the swerve drivetrain code. There is no implementation yet for a camera, as there was for V1, but that is coming in the future.

This remains version 2.1 because version 2 was a failed attempt to create a "true" swerve drive, without wires to limit the range of motion. The 3D printed tests did not work, so something more simple was devised. 

<h3>Running the Code</h3>

In order to use this code for yourself, you need to download and compile the JInput library from source on the Raspberry Pi, and install the shared libraries manually. This allows the bluetooth Xbox controller to be seen as a USB HID by the pi. Pair your bluetooth Xbox controller using bluetoothctl and start the program, and you should be good to go.

It is recommended that you create a runnable jar file from this to make running easier.

For images and videos of it working, visit [my Google Photos album](https://photos.app.goo.gl/2zUikkyRRp2dGM538)

Here are the [CAD files](https://www.thingiverse.com/thing:3564919)

<h3>What I Learned</h3>
This was my first attempt using stepper motors, and I learned the limitations of them. They quickly lose their position due to step skipping, and are terribly slow. I did however learn about the algorithms behind stepper motors, and coding techniques to report the current position in real time without the use of an external sensor.

The most difficult part was designing the new swerve modules, for version 2.2. I had to teach myself FreeCAD as I have moved completely to linux, and about tolerances and designing gears.

I am constantly updating this design with new ideas, and plan to make it completely autonomous as a delivery robot at some point.

<h3>UPDATE</h3>
The new swerve module is complete! CAD is going to be released soon, but in the mean time, photos are available in the gallery above.
