# Gesture_Recognizer

Gesture recognition on Android using only Accelerometer.

### Abstract

The application is a proof-of-concept for the university project. Using only raw accelerometer, we can record the user gesture as accelerometer values on time series (x,y,z).
Then we can compare this data to other gesture to see if they match. Using Dynamic Time Warping (DTW, the implementation here is FastDTW), we eliminate time variance and are left with distance as a result of the operation. 
Distance is a sum of differences on an axis between the time-series after they are warped. We can then adjust the distance threshold, below which we accept two gesture as matching, and above as not matching.

### Conclusion

If the gesture is significant (like making a full circle with your hand) and done slow enough, this algorithm will successfully label gesture 9/10 times. 1/10 times it will be false positive or false negative. The main problem is
that DTW is slow and accelerometer is not very precise. Even when the device is left flat on a table, the accelerometer will show some values on all axis. Using additional sensors, like a gyroscope, can improve this somewhat but not enough for real-life usage. 
