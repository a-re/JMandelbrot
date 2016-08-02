# JMandelbrot
A simple mandelbrot visualizer that zooms written in Java. Has a lot of bugs and the code isn't fantastic
If you want to help or have any comments, submit and issue/pull here or email calix1999@gmail.com

## Bugs
* Stops working at around iteration 1000 of the render loop, probably because it's hitting the double lower limit
* Slows down as the iterations get higher because the loop always starts from 0 and goes until n iterations. Higher n means slower rendering.
* Not the best code